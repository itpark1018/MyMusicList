package com.mymusiclist.backend.member.controller;

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

  @PostMapping("/signup")
  public ResponseEntity<String> singUp(@RequestBody SignUpParameter signUpParameter) {
    String response = memberService.signUp(signUpParameter);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/auth")
  public ResponseEntity<String> auth(@RequestParam(name = "email") String email,
      @RequestParam(name = "code") String code) {
    String response = memberService.auth(email, code);
    return ResponseEntity.ok(response);
  }

}
