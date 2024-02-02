package com.mymusiclist.backend.member.service;

import com.mymusiclist.backend.member.dto.request.LoginRequest;
import com.mymusiclist.backend.member.dto.request.SignUpRequest;
import com.mymusiclist.backend.member.dto.request.TokenRequest;
import java.util.Map;
import com.mymusiclist.backend.member.dto.parameter.SignUpParameter;
import java.net.URISyntaxException;
import org.springframework.stereotype.Service;

@Service
public interface MemberService {

  String signUp(SignUpRequest signUpRequest);

  String auth(String email, String code);

  Map<String, String> login(LoginRequest loginRequest);

  void logout(TokenRequest tokenRequest);
}
