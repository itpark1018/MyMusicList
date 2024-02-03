package com.mymusiclist.backend.member.service;

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
import com.mymusiclist.backend.member.dto.TokenDto;
import com.mymusiclist.backend.member.dto.request.LoginRequest;
import com.mymusiclist.backend.member.dto.request.ResetRequest;
import com.mymusiclist.backend.member.dto.request.SignUpRequest;
import com.mymusiclist.backend.member.dto.request.UpdateRequest;
import com.mymusiclist.backend.member.jwt.JwtTokenProvider;
import com.mymusiclist.backend.member.repository.MemberRepository;
import com.mymusiclist.backend.post.domain.CommentEntity;
import com.mymusiclist.backend.post.domain.PostEntity;
import com.mymusiclist.backend.post.repository.CommentRepository;
import com.mymusiclist.backend.post.repository.PostRepository;
import com.mymusiclist.backend.type.MemberStatus;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
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
public class MemberServiceImpl implements MemberService {

  private final MemberRepository memberRepository;
  private final MailComponents mailComponents;
  private final JwtTokenProvider jwtTokenProvider;
  private final PasswordEncoder passwordEncoder;
  private final RedisTemplate redisTemplate;
  private final PostRepository postRepository;
  private final CommentRepository commentRepository;
  private final TokenService tokenService;

  @Override
  @Transactional
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
  @Transactional
  public String auth(String email, String code) {

    Optional<MemberEntity> byEmail = memberRepository.findByEmail(email);
    if (byEmail.isEmpty()) {
      throw new NotFoundMemberException();
    }

    MemberEntity member = byEmail.get();
    if (!code.equals(member.getAuthCode())) {
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
  public TokenDto login(LoginRequest loginRequest) {

    Optional<MemberEntity> byEmail = memberRepository.findByEmail(loginRequest.getEmail());
    if (byEmail.isEmpty()) {
      throw new NotFoundMemberException();
    }

    MemberEntity member = byEmail.get();
    if (member.getStatus().equals(MemberStatus.SUSPENDED)) {
      throw new SuspendedMemberException();
    } else if (member.getStatus().equals(MemberStatus.WAITING_FOR_APPROVAL)) {
      throw new WaitingMemberException();
    }

    // 패스워드 검증
    if (passwordEncoder.matches(loginRequest.getPassword(), member.getPassword())) {
      // AccessToken 및 RefreshToken 생성
      TokenDto token = tokenService.create(member.getEmail(), member.getAdminYn());
      return token;
    } else {
      throw new InvalidPasswordException();
    }
  }

  @Override
  public void logout(HttpServletRequest request) {

    String accessToken = request.getHeader("Authorization");
    if (accessToken != null && accessToken.startsWith("Bearer ")) {
      accessToken = accessToken.substring(7); // "Bearer " 이후의 토큰 값만 추출
      System.out.println("AccessToken: " + accessToken);
    }

    // 로그아웃 하고 싶은 토큰이 유효한지 검증
    if (!jwtTokenProvider.validateToken(accessToken)) {
      throw new InvalidTokenException();
    }

    // 토큰을 통해 사용자 정보르 받아오기
    Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);

    // Redis에서 해당 유저의 email로 저장된 RefreshToken이 있는지 확인 후 있을 경우 삭제
    if (redisTemplate.opsForValue().get("RT:" + authentication.getName()) != null) {
      redisTemplate.delete("RT:" + authentication.getName());
    }

    // 해당 Access Token 유효시간을 가지고 와서 BlackList에 저장하기
    long expiration = jwtTokenProvider.getExpiration(accessToken);
    long now = (new Date()).getTime();
    long accessTokenExpiresIn = expiration - now;
    redisTemplate.opsForValue()
        .set(accessToken, "logout", accessTokenExpiresIn, TimeUnit.MILLISECONDS);
  }

  @Override
  @Transactional
  public String resetPassword(ResetRequest resetRequest) {

    Optional<MemberEntity> byEmail = memberRepository.findByEmail(resetRequest.getEmail());
    if (byEmail.isEmpty()) {
      throw new NotFoundMemberException();
    }

    MemberEntity member = byEmail.get();
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

    Optional<MemberEntity> byEmail = memberRepository.findByEmail(email);
    if (byEmail.isEmpty()) {
      throw new NotFoundMemberException();
    }

    MemberEntity member = byEmail.get();
    if (!code.equals(member.getPasswordAuthCode())) {
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
  @Transactional
  public MemberDto update(UpdateRequest updateRequest) {

    // AccessToken에서 email을 가져와서 회원 조회
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();
    Optional<MemberEntity> byEmail = memberRepository.findByEmail(email);
    if (byEmail.isEmpty()) {
      throw new NotFoundMemberException();
    }

    Optional<MemberEntity> byNickname = memberRepository.findByNickname(
        updateRequest.getNickname());
    if (byNickname.isPresent()) {
      MemberEntity member = byNickname.get();
      if (member.getStatus().equals(MemberStatus.ACTIVE)) {
        throw new DuplicateNicknameException();
      }
    }

    MemberEntity member = byEmail.get();
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
  public String withdrawal() {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();
    Optional<MemberEntity> byEmail = memberRepository.findByEmail(email);
    if (byEmail.isEmpty()) {
      throw new NotFoundMemberException();
    }
    MemberEntity member = byEmail.get();

    MemberEntity memberEntity = member.toBuilder()
        .nickname("탈퇴한 회원 " + member.getNickname())
        .status(MemberStatus.WITHDRAWN)
        .build();
    memberRepository.save(memberEntity);

    // 탈퇴하는 회원이 작성한 게시글과, 댓글의 닉네임을 탈퇴한 회원으로 변경
    updateNicknameInPostsAndComments(member, "탈퇴한 회원");

    return "회원탈퇴가 정상적으로 완료되었습니다.";
  }

  @Override
  public MemberDto myInfo() {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();
    Optional<MemberEntity> byEmail = memberRepository.findByEmail(email);
    if (byEmail.isEmpty()) {
      throw new NotFoundMemberException();
    }
    MemberEntity member = byEmail.get();

    return MemberDto.of(member);
  }

  @Override
  public MemberInfoDto memberInfo(String nickname) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();
    Optional<MemberEntity> byEmail = memberRepository.findByEmail(email);
    if (byEmail.isEmpty()) {
      throw new NotFoundMemberException();
    }

    Optional<MemberEntity> byNickname = memberRepository.findByNickname(nickname);
    if (byNickname.isEmpty()) {
      throw new NotFoundMemberException();
    }
    MemberEntity member = byNickname.get();

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
