package com.mymusiclist.backend.member.service.impl;

import com.mymusiclist.backend.components.MailComponents;
import com.mymusiclist.backend.exception.impl.DuplicateEmailException;
import com.mymusiclist.backend.exception.impl.DuplicateNicknameException;
import com.mymusiclist.backend.exception.impl.ExpiredException;
import com.mymusiclist.backend.exception.impl.InvalidAuthCodeException;
import com.mymusiclist.backend.exception.impl.InvalidEmailException;
import com.mymusiclist.backend.exception.impl.InvalidPasswordConfirmationException;
import com.mymusiclist.backend.exception.impl.InvalidPasswordException;
import com.mymusiclist.backend.exception.impl.InvalidTokenException;
import com.mymusiclist.backend.exception.impl.NotFoundMemberException;
import com.mymusiclist.backend.exception.impl.SuspendedMemberException;
import com.mymusiclist.backend.exception.impl.WaitingMemberException;
import com.mymusiclist.backend.member.domain.MemberEntity;
import com.mymusiclist.backend.member.dto.MemberDto;
import com.mymusiclist.backend.member.dto.MemberInfoDto;
import com.mymusiclist.backend.member.dto.TokenCreateDto;
import com.mymusiclist.backend.member.dto.request.LoginRequest;
import com.mymusiclist.backend.member.dto.request.ResetRequest;
import com.mymusiclist.backend.member.dto.request.SignUpRequest;
import com.mymusiclist.backend.member.dto.request.UpdateRequest;
import com.mymusiclist.backend.member.jwt.JwtTokenProvider;
import com.mymusiclist.backend.member.repository.MemberRepository;
import com.mymusiclist.backend.member.service.ManagementService;
import com.mymusiclist.backend.post.domain.CommentEntity;
import com.mymusiclist.backend.post.domain.PostEntity;
import com.mymusiclist.backend.post.repository.CommentRepository;
import com.mymusiclist.backend.post.repository.PostRepository;
import com.mymusiclist.backend.type.MemberStatus;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Service
@RequiredArgsConstructor
@Slf4j
public class ManagementServiceImpl implements ManagementService {

  private final MemberRepository memberRepository;
  private final MailComponents mailComponents;
  private final PostRepository postRepository;
  private final CommentRepository commentRepository;
  private final JwtTokenProvider jwtTokenProvider;
  private final PasswordEncoder passwordEncoder;
  private final RedisTemplate redisTemplate;

  @Override
  public String signUp(SignUpRequest signUpRequest) {

    if (!isValidEmail(signUpRequest.getEmail())) {
      throw new InvalidEmailException();
    }

    if (!signUpRequest.getPassword().equals(signUpRequest.getCheckPassword())) {
      throw new InvalidPasswordConfirmationException();
    }

    Optional<MemberEntity> byNickname = memberRepository.findByNickname(
        signUpRequest.getNickname());
    if (byNickname.isPresent()) {
      MemberEntity member = byNickname.get();
      if (member.getStatus().equals(MemberStatus.ACTIVE)) {
        throw new DuplicateNicknameException();
      }
    }

    Optional<MemberEntity> byEmail = memberRepository.findByEmail(signUpRequest.getEmail());
    if (byEmail.isPresent()) {
      MemberEntity member = byEmail.get();
      // 탈퇴한 회원이 다시 가입할 시
      if (!member.getStatus().equals(MemberStatus.WITHDRAWN)) {
        throw new DuplicateEmailException();
      } else {
        MemberEntity memberEntity = SignUpRequest.reSignUpInput(member, signUpRequest);
        memberRepository.save(memberEntity);

        // 새로 가입하는 계정의 닉네임 정보로 탈퇴할때 탈퇴한 회원으로 닉네임 변경이 처리된 게시글, 댓글의 닉네임 변경
        updateNicknameInPostsAndComments(member, signUpRequest.getNickname());

        String email = signUpRequest.getEmail();
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        String title = "MyMusicList 회원인증";
        String message = "<h3>MyMusicList 회원가입에 성공했습니다. 아래의 링크를 클릭하셔서 회원인증을 완료해주세요.</h3>" +
            "<div><a href='" + baseUrl + "/member/auth?email=" + email + "&code="
            + memberEntity.getAuthCode() + "'> 인증 링크 </a></div>";
        mailComponents.sendMail(email, title, message);

        return "가입한 이메일을 확인해 회원인증을 진행해주세요.";
      }
    }

    MemberEntity memberEntity = SignUpRequest.signUpInput(signUpRequest);
    memberRepository.save(memberEntity);

    String email = signUpRequest.getEmail();
    String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
    String title = "MyMusicList 회원인증";
    String message = "<h3>MyMusicList 회원가입에 성공했습니다. 아래의 링크를 클릭하셔서 회원인증을 완료해주세요.</h3>" +
        "<div><a href='" + baseUrl + "/member/auth?email=" + email + "&code="
        + memberEntity.getAuthCode() + "'> 인증 링크 </a></div>";
    mailComponents.sendMail(email, title, message);

    return "가입한 이메일을 확인해 회원인증을 진행해주세요.";
  }

  @Override
  public String auth(String email, String code) {

    MemberEntity member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new NotFoundMemberException());

    if (!code.equals(member.getAuthCode())) {
      log.warn("signUpAuth InvalidAuthCodeException: {}", email);
      throw new InvalidAuthCodeException();
    }

    MemberEntity memberEntity = member.toBuilder()
        .auth(true)
        .status(MemberStatus.ACTIVE)
        .build();
    memberRepository.save(memberEntity);

    return "인증을 완료 했습니다.";
  }

  @Override
  public TokenCreateDto login(LoginRequest loginRequest) {

    MemberEntity member = memberRepository.findByEmail(loginRequest.getEmail())
        .orElseThrow(() -> new NotFoundMemberException());

    if (member.getStatus().equals(MemberStatus.SUSPENDED)) {
      throw new SuspendedMemberException();
    } else if (member.getStatus().equals(MemberStatus.WAITING_FOR_APPROVAL)) {
      throw new WaitingMemberException();
    }

    // 패스워드 검증
    if (passwordEncoder.matches(loginRequest.getPassword(), member.getPassword())) {
      throw new InvalidPasswordException();
    }

    TokenCreateDto result = TokenCreateDto.builder()
        .email(member.getEmail())
        .adminYn(member.getAdminYn())
        .build();

    return result;
  }

  @Override
  public String logout(String accessToken) {

    // 로그아웃 하고 싶은 토큰이 유효한지 검증
    if (!jwtTokenProvider.validateToken(accessToken)) {
      throw new InvalidTokenException();
    }

    // 토큰을 통해 사용자 정보르 받아오기
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();

    // Redis에서 해당 유저의 email로 저장된 RefreshToken이 있는지 확인 후 있을 경우 삭제
    if (redisTemplate.opsForValue().get("RT:" + email) != null) {
      redisTemplate.delete("RT:" + email);
    }

    // 해당 Access Token 유효시간을 가지고 와서 BlackList에 저장하기
    long expiration = jwtTokenProvider.getExpiration(accessToken);
    long now = (new Date()).getTime();
    long accessTokenExpiresIn = expiration - now;
    redisTemplate.opsForValue()
        .set(accessToken, "logout", accessTokenExpiresIn, TimeUnit.MILLISECONDS);

    return email;
  }

  @Override
  public String resetPassword(ResetRequest resetRequest) {

    MemberEntity member = memberRepository.findByEmail(resetRequest.getEmail())
        .orElseThrow(() -> new NotFoundMemberException());

    String uuid = UUID.randomUUID().toString();
    String encPassword = BCrypt.hashpw(resetRequest.getResetPassword(), BCrypt.gensalt());

    MemberEntity memberEntity = member.toBuilder()
        .passwordAuthCode(uuid)
        .passwordDate(LocalDateTime.now().plusMinutes(30))
        .build();
    memberRepository.save(memberEntity);

    String email = member.getEmail();
    String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
    String title = "MyMusicList 비밀번호 변경";
    String message = "<h3>MyMusicList 비밀번호 변경을 위해서 아래의 링크를 클릭하셔서 인증을 완료해주세요.</h3>" +
        "<div><a href='" + baseUrl + "/member/password/auth?email=" + email + "&code="
        + memberEntity.getPasswordAuthCode() + "&resetPassword=" + encPassword
        + "'> 인증 링크 </a></div>";
    mailComponents.sendMail(email, title, message);

    return "이메일을 확인해 인증을 진행해주세요.";
  }

  @Override
  public String passwordAuth(String email, String code, String resetPassword) {

    MemberEntity member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new NotFoundMemberException());

    if (!code.equals(member.getPasswordAuthCode())) {
      log.warn("passwordAuth InvalidAuthCodeException: {}", email);
      throw new InvalidAuthCodeException();
    }

    // 비밀번호 변경 기간이 지나면 exception 발생
    if (member.getPasswordDate().isBefore(LocalDateTime.now())) {
      throw new ExpiredException();
    }

    MemberEntity memberEntity = member.toBuilder()
        .password(resetPassword)
        .modDate(LocalDateTime.now())
        .auth(true)
        .passwordAuthCode(null)
        .passwordDate(null)
        .build();
    memberRepository.save(memberEntity);

    return "비밀번호 변경을 완료했습니다.";
  }

  @Override
  public MemberDto update(String accessToken, UpdateRequest updateRequest) {

    if (!jwtTokenProvider.validateToken(accessToken)) {
      throw new InvalidTokenException();
    }

    // AccessToken에서 email을 가져와서 회원 조회
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();

    MemberEntity member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new NotFoundMemberException());

    // 닉네임 중복체크
    // 기존 본인의 닉네임이랑 같을 때는 중복체크를 하지 않음
    if (!updateRequest.getNickname().equals(member.getNickname())) {
      memberRepository.findByNicknameAndStatus(updateRequest.getNickname(), MemberStatus.ACTIVE)
          .ifPresent(existing -> {
            throw new DuplicateNicknameException();
          });
    }

    if (!updateRequest.getNickname().equals(member.getNickname())) {
      // 사용자가 작성한 게시글, 댓글의 닉네임을 새롭게 변경하는 닉네임으로 변경
      updateNicknameInPostsAndComments(member, updateRequest.getNickname());
    }

    MemberEntity memberEntity = member.toBuilder()
        .nickname(updateRequest.getNickname())
        .modDate(LocalDateTime.now())
        .imageUrl(updateRequest.getImageUrl())
        .introduction(updateRequest.getIntroduction())
        .build();
    memberRepository.save(memberEntity);

    return MemberDto.of(memberEntity);
  }

  @Override
  public String withdrawal(String accessToken) {

    if (!jwtTokenProvider.validateToken(accessToken)) {
      throw new InvalidTokenException();
    }

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();

    MemberEntity member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new NotFoundMemberException());

    MemberEntity memberEntity = member.toBuilder()
        .nickname("탈퇴한 회원 " + member.getNickname())
        .status(MemberStatus.WITHDRAWN)
        .build();
    memberRepository.save(memberEntity);

    // 탈퇴하는 회원이 작성한 게시글과, 댓글의 닉네임을 탈퇴한 회원으로 변경
    updateNicknameInPostsAndComments(member, "탈퇴한 회원");

    return email;
  }

  @Override
  public MemberDto myInfo(String accessToken) {

    if (!jwtTokenProvider.validateToken(accessToken)) {
      throw new InvalidTokenException();
    }

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();
    MemberEntity member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new InvalidTokenException());

    return MemberDto.of(member);
  }

  @Override
  public MemberInfoDto memberInfo(String accessToken, String nickname) {

    if (!jwtTokenProvider.validateToken(accessToken)) {
      throw new InvalidTokenException();
    }

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();
    memberRepository.findByEmail(email).orElseThrow(() -> new InvalidTokenException());

    MemberEntity member = memberRepository.findByNickname(nickname)
        .orElseThrow(() -> new NotFoundMemberException());
    if (!member.getStatus().equals(MemberStatus.ACTIVE)) {
      throw new SuspendedMemberException();
    }

    return MemberInfoDto.of(member);
  }

  @Transactional
  public void updateNicknameInPostsAndComments(MemberEntity member, String newNickname) {
    // 사용자가 작성한 게시글의 닉네임 변경
    List<PostEntity> userPosts = postRepository.findAllByMemberId(member);
    List<PostEntity> updatedPosts = userPosts.stream()
        .map(post -> post.toBuilder().nickname(newNickname).build())
        .collect(Collectors.toList());
    postRepository.saveAll(updatedPosts);

    // 사용자가 작성한 댓글의 닉네임 변경
    List<CommentEntity> userComments = commentRepository.findAllByMemberId(member);
    List<CommentEntity> updatedComments = userComments.stream()
        .map(comment -> comment.toBuilder().nickname(newNickname).build())
        .collect(Collectors.toList());
    commentRepository.saveAll(updatedComments);
  }

  private boolean isValidEmail(String email) {
    return EmailValidator.getInstance().isValid(email);
  }
}
