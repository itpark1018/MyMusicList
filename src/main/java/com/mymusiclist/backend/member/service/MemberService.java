package com.mymusiclist.backend.member.service;

import com.mymusiclist.backend.member.dto.parameter.SignUpParameter;
import java.net.URISyntaxException;
import org.springframework.stereotype.Service;

@Service
public interface MemberService {

  String signUp(SignUpParameter signUpParameter);

  String auth(String email, String code);
}
