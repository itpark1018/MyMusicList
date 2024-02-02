package com.mymusiclist.backend.member.service;

import com.mymusiclist.backend.components.MailComponents;
import com.mymusiclist.backend.exception.impl.DuplicateEmailException;
import com.mymusiclist.backend.exception.impl.InvalidAuthCodeException;
import com.mymusiclist.backend.exception.impl.InvalidEmailException;
import com.mymusiclist.backend.exception.impl.InvalidPasswordConfirmationException;
import com.mymusiclist.backend.exception.impl.InvalidPasswordException;
import com.mymusiclist.backend.exception.impl.InvalidTokenException;
import com.mymusiclist.backend.exception.impl.NotFoundMemberException;
import com.mymusiclist.backend.exception.impl.SuspendedMemberException;
import com.mymusiclist.backend.member.domain.MemberEntity;
import com.mymusiclist.backend.member.dto.MemberDto;
import com.mymusiclist.backend.member.dto.request.LoginRequest;
import com.mymusiclist.backend.member.dto.request.SignUpRequest;
import com.mymusiclist.backend.member.dto.request.TokenRequest;
import com.mymusiclist.backend.member.jwt.JwtTokenProvider;
import com.mymusiclist.backend.member.repository.MemberRepository;
import com.mymusiclist.backend.type.MemberStatus;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.mymusiclist.backend.exception.impl.NotFoundMemberException;
import com.mymusiclist.backend.member.domain.MemberEntity;
import com.mymusiclist.backend.member.dto.MemberDto;
import com.mymusiclist.backend.member.dto.parameter.SignUpParameter;
import com.mymusiclist.backend.member.repository.MemberRepository;
import com.mymusiclist.backend.type.MemberStatus;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
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

  @Override
  @Transactional
  public String signUp(SignUpRequest signUpRequest) {

    Optional<MemberEntity> byEmail = memberRepository.findByEmail(signUpRequest.getEmail());
    if (byEmail.isPresent()) {
      throw new DuplicateEmailException();
    }

    if (!signUpRequest.getPassword().equals(signUpRequest.getCheckPassword())) {
      throw new InvalidPasswordConfirmationException();
    }

    if (!isValidEmail(signUpRequest.getEmail())) {
      throw new InvalidEmailException();
    }

    MemberEntity memberEntity = MemberDto.signUpInput(signUpRequest);
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

    MemberEntity memberEntity = MemberEntity.builder()
        .memberId(member.getMemberId())
        .email(member.getEmail())
        .password(member.getPassword())
        .name(member.getName())
        .nickname(member.getNickname())
        .regDate(member.getRegDate())
        .auth(true)
        .authCode(member.getAuthCode())
        .imageUrl(member.getImageUrl())
        .introduction(member.getIntroduction())
        .status(MemberStatus.ACTIVE.getDescription())
        .build();
    memberRepository.save(memberEntity);

    return "인증을 완료 했습니다.";
  }

  @Override
  public Map<String, String> login(LoginRequest loginRequest) {

    Optional<MemberEntity> byEmail = memberRepository.findByEmail(loginRequest.getEmail());
    if (byEmail.isEmpty()) {
      throw new NotFoundMemberException();
    }

    MemberEntity member = byEmail.get();
    if (member.getStatus().equals(MemberStatus.SUSPENDED.getDescription())) {
      throw new SuspendedMemberException();
    }

    // 패스워드 검증
    if (passwordEncoder.matches(loginRequest.getPassword(), member.getPassword())) {
      // AccessToken 및 RefreshToken 생성
      Map<String, String> token = jwtTokenProvider.createTokens(member.getEmail(), member.getAdminYn());

      // Redis에 RefreshToken 저장
      String refreshToken = token.get("refreshToken");
      long refreshTokenExpiresIn = 86400000;

      redisTemplate.opsForValue().set("RT:"+member.getEmail(), refreshToken, refreshTokenExpiresIn, TimeUnit.MILLISECONDS);

      return token;
    } else {
      throw new InvalidPasswordException();
    }
  }

  @Override
  public void logout(TokenRequest tokenRequest) {

    // 로그아웃 하고 싶은 토큰이 유효한지 검증
    if (!jwtTokenProvider.validateToken(tokenRequest.getAccessToken())) {
      throw new InvalidTokenException();
    }

    // 토큰을 통해 사용자 정보르 받아오기
    Authentication authentication = jwtTokenProvider.getAuthentication(tokenRequest.getAccessToken());

    // Redis에서 해당 유저의 email로 저장된 RefreshToken이 있는지 확인 후 있을 경우 삭제
    if (redisTemplate.opsForValue().get("RT:"+authentication.getName()) != null) {
      redisTemplate.delete("RT:"+authentication.getName());
    }

    // 해당 Access Token 유효시간을 가지고 와서 BlackList에 저장하기
    long expiration = jwtTokenProvider.getExpiration(tokenRequest.getAccessToken());
    long now = (new Date()).getTime();
    long accessTokenExpiresIn = expiration - now;
    redisTemplate.opsForValue().set(tokenRequest.getAccessToken(), "logout", accessTokenExpiresIn, TimeUnit.MILLISECONDS);
  }
  
  private boolean isValidEmail(String email) {
    return EmailValidator.getInstance().isValid(email);
  }
}
