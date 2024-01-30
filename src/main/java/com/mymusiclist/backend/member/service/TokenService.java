package com.mymusiclist.backend.member.service;

import com.mymusiclist.backend.member.dto.TokenDto;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public interface TokenService {

  TokenDto reIssue(String refreshToken);

  TokenDto create(String email, Boolean adminYn);
}
