package com.mymusiclist.backend.member.controller;

import com.mymusiclist.backend.member.dto.request.LoginRequest;
import com.mymusiclist.backend.member.dto.request.SignUpRequest;
import com.mymusiclist.backend.member.dto.request.TokenRequest;
import com.mymusiclist.backend.member.service.MemberService;
import com.mymusiclist.backend.member.service.TokenService;
import java.util.Map;
import com.mymusiclist.backend.member.dto.parameter.SignUpParameter;
import com.mymusiclist.backend.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

  private final MemberService memberService;
  private final TokenService tokenService;

  @PostMapping("/signup")
  public ResponseEntity<String> singUp(@RequestBody SignUpRequest signUpRequest) {
    String response = memberService.signUp(signUpRequest);

  @PostMapping("/auth")
  public ResponseEntity<String> auth(@RequestParam(name = "email") String email,
                                     @RequestParam(name = "code") String code) {
    String response = memberService.auth(email, code);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/login")
  public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest loginRequest) {
    Map<String, String> response = memberService.login(loginRequest);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/logout")
  public void logout(@RequestBody TokenRequest tokenRequest) {
    memberService.logout(tokenRequest);
  }

  @PostMapping("/reissue")
  public ResponseEntity<Map<String, String>> reIssue(@RequestBody TokenRequest TokenRequest) {
    Map<String, String> response = tokenService.reIssue(TokenRequest.getRefreshToken());
    return ResponseEntity.ok(response);
  }
}
