package com.mymusiclist.backend.member.service;

import com.mymusiclist.backend.exception.impl.InvalidTokenException;
import com.mymusiclist.backend.exception.impl.NotFoundMemberException;
import com.mymusiclist.backend.member.domain.MemberEntity;
import com.mymusiclist.backend.member.jwt.JwtTokenProvider;
import com.mymusiclist.backend.member.repository.MemberRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService{

  private final JwtTokenProvider jwtTokenProvider;
  private final RedisTemplate redisTemplate;
  private final MemberRepository memberRepository;

  @Override
  public Map<String, String> reIssue(String refreshToken) {

    // Redis에서 저장된 RefreshToken 가져오기
    Claims refreshTokenClaims = jwtTokenProvider.getClaimsFromToken(refreshToken);
    String email = refreshTokenClaims.getSubject();
    String storedRefreshToken = (String) redisTemplate.opsForValue().get("RT:"+email);
    if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
      throw new InvalidTokenException();
    }

    Optional<MemberEntity> byEmail = memberRepository.findByEmail(email);
    if (byEmail.isEmpty()) {
      throw new NotFoundMemberException();
    }
    MemberEntity member = byEmail.get();

    // 새로운 토큰 발급
    Map<String, String> newToken = jwtTokenProvider.createTokens(member.getEmail(), member.getAdminYn());

    // Redis에 newRefreshToken 저장
    String newRefreshToken  = newToken.get("refreshToken");
    long refreshTokenExpiresIn = 86400000;

    redisTemplate.opsForValue().set("RT:"+member.getEmail(), newRefreshToken, refreshTokenExpiresIn, TimeUnit.MILLISECONDS);

    return newToken;
  }
}
