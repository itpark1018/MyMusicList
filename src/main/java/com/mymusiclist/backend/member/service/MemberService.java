package com.mymusiclist.backend.member.service;

import com.mymusiclist.backend.member.dto.MemberDto;
import com.mymusiclist.backend.member.dto.TokenDto;
import com.mymusiclist.backend.member.dto.request.LoginRequest;
import com.mymusiclist.backend.member.dto.request.ResetRequest;
import com.mymusiclist.backend.member.dto.request.SignUpRequest;
import com.mymusiclist.backend.member.dto.request.TokenRequest;
import com.mymusiclist.backend.member.dto.request.UpdateRequest;
import java.util.Map;
import com.mymusiclist.backend.member.dto.parameter.SignUpParameter;
import java.net.URISyntaxException;
import org.springframework.stereotype.Service;

@Service
public interface MemberService {

  String signUp(SignUpRequest signUpRequest);

  String auth(String email, String code);

  TokenDto login(LoginRequest loginRequest);

  void logout(TokenRequest tokenRequest);

  String resetPassword(ResetRequest resetRequest);

  String passwordAuth(String email, String code, String resetPassword);

  MemberDto update(UpdateRequest updateRequest);
}
