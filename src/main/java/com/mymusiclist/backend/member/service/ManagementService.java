package com.mymusiclist.backend.member.service;

import com.mymusiclist.backend.member.dto.MemberDto;
import com.mymusiclist.backend.member.dto.MemberInfoDto;
import com.mymusiclist.backend.member.dto.TokenCreateDto;
import com.mymusiclist.backend.member.dto.request.LoginRequest;
import com.mymusiclist.backend.member.dto.request.ResetRequest;
import com.mymusiclist.backend.member.dto.request.SignUpRequest;
import com.mymusiclist.backend.member.dto.request.UpdateRequest;
import org.springframework.stereotype.Service;

@Service
public interface ManagementService {

  String signUp(SignUpRequest signUpRequest);

  String auth(String email, String code);

  TokenCreateDto login(LoginRequest loginRequest);

  String logout(String accessToken);

  String resetPassword(ResetRequest resetRequest);

  String passwordAuth(String email, String code, String resetPassword);

  MemberDto update(String accessToken, UpdateRequest updateRequest);

  String withdrawal(String accessToken);

  MemberDto myInfo(String accessToken);

  MemberInfoDto memberInfo(String accessToken, String nickname);
}
