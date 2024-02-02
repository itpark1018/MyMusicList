package com.mymusiclist.backend.member.service;

import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public interface TokenService {

  Map<String, String> reIssue(String refreshToken);
}
