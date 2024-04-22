package com.mymusiclist.backend.member.service.impl;

import com.mymusiclist.backend.member.dto.MemberDto;
import com.mymusiclist.backend.member.dto.MemberInfoDto;
import com.mymusiclist.backend.member.dto.TokenCreateDto;
import com.mymusiclist.backend.member.dto.TokenDto;
import com.mymusiclist.backend.member.dto.request.LoginRequest;
import com.mymusiclist.backend.member.dto.request.ResetRequest;
import com.mymusiclist.backend.member.dto.request.SignUpRequest;
import com.mymusiclist.backend.member.dto.request.UpdateRequest;
import com.mymusiclist.backend.member.service.ManagementService;
import com.mymusiclist.backend.member.service.MemberService;
import com.mymusiclist.backend.member.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

  private final ManagementService managementService;
  private final TokenService tokenService;

  @Override
  public String signUp(SignUpRequest signUpRequest) {

    return managementService.signUp(signUpRequest);
  }

  @Override
  public String auth(String email, String code) {

    return managementService.auth(email, code);
  }

  @Override
  public TokenDto login(LoginRequest loginRequest) {

    TokenCreateDto createDto = managementService.login(loginRequest);
    TokenDto tokenDto = tokenService.create(createDto.getEmail(), createDto.getAdminYn());
    log.info("token create: {}", loginRequest.getEmail());
    return tokenDto;
  }

  @Override
  public String logout(String accessToken) {

   return managementService.logout(accessToken);
  }

  @Override
  public String resetPassword(ResetRequest resetRequest) {

   return managementService.resetPassword(resetRequest);
  }

  @Override
  public String passwordAuth(String email, String code, String resetPassword) {

    return managementService.passwordAuth(email, code, resetPassword);
  }

  @Override
  public MemberDto update(String accessToken, UpdateRequest updateRequest) {

    return managementService.update(accessToken, updateRequest);
  }

  @Override
  public String withdrawal(String accessToken) {

    return managementService.withdrawal(accessToken);
  }

  @Override
  public MemberDto myInfo(String accessToken) {

    return managementService.myInfo(accessToken);
  }

  @Override
  public MemberInfoDto memberInfo(String accessToken, String nickname) {

    return managementService.memberInfo(accessToken, nickname);
  }
}
