package com.mymusiclist.backend.member.controller;

import com.mymusiclist.backend.member.dto.MemberDto;
import com.mymusiclist.backend.member.dto.MemberInfoDto;
import com.mymusiclist.backend.member.dto.TokenDto;
import com.mymusiclist.backend.member.dto.request.LoginRequest;
import com.mymusiclist.backend.member.dto.request.ResetRequest;
import com.mymusiclist.backend.member.dto.request.SignUpRequest;
import com.mymusiclist.backend.member.dto.request.TokenRequest;
import com.mymusiclist.backend.member.dto.request.UpdateRequest;
import com.mymusiclist.backend.member.service.MemberService;
import com.mymusiclist.backend.member.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

  private final MemberService memberService;
  private final TokenService tokenService;

  @PostMapping("/signup")
  public ResponseEntity<String> singUp(@Valid @RequestBody SignUpRequest signUpRequest) {
    String response = memberService.signUp(signUpRequest);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/auth")
  public ResponseEntity<String> auth(@RequestParam(name = "email") String email,
      @RequestParam(name = "code") String code) {
    String response = memberService.auth(email, code);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/login")
  public ResponseEntity<TokenDto> login(@Valid @RequestBody LoginRequest loginRequest) {
    TokenDto response = memberService.login(loginRequest);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/logout")
  public ResponseEntity<String> logout(HttpServletRequest request) {
    String accessToken = request.getHeader("Authorization");
    if (accessToken != null && accessToken.startsWith("Bearer ")) {
      accessToken = accessToken.substring(7); // "Bearer " 이후의 토큰 값만 추출
    }

    memberService.logout(accessToken);
    return ResponseEntity.ok("로그아웃 완료");
  }

  @PostMapping("/reissue")
  public ResponseEntity<TokenDto> reIssue(@RequestBody TokenRequest TokenRequest) {
    TokenDto response = tokenService.reIssue(TokenRequest.getRefreshToken());
    return ResponseEntity.ok(response);
  }

  @PostMapping("/password/reset")
  public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetRequest resetRequest) {
    String response = memberService.resetPassword(resetRequest);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/password/auth")
  public ResponseEntity<String> passwordAuth(@RequestParam(name = "email") String email,
      @RequestParam(name = "code") String code,
      @RequestParam(name = "resetPassword") String resetPassword) {
    String response = memberService.passwordAuth(email, code, resetPassword);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/withdrawal")
  public ResponseEntity<String> withdrawal() {
    String response = memberService.withdrawal();
    return ResponseEntity.ok(response);
  }

  @PutMapping("/my-info")
  public ResponseEntity<MemberDto> update(@Valid @RequestBody UpdateRequest updateRequest) {
    MemberDto response = memberService.update(updateRequest);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/my-info")
  public ResponseEntity<MemberDto> myInfo() {
    MemberDto response = memberService.myInfo();
    return ResponseEntity.ok(response);
  }

  @GetMapping("/info/{nickname}")
  public ResponseEntity<MemberInfoDto> memberInfo(
      @PathVariable(name = "nickname") String nickname) {
    MemberInfoDto response = memberService.memberInfo(nickname);
    return ResponseEntity.ok(response);
  }
}
