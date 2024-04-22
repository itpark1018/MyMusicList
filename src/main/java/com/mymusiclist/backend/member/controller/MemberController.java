package com.mymusiclist.backend.member.controller;

import com.mymusiclist.backend.member.dto.MemberDto;
import com.mymusiclist.backend.member.dto.MemberInfoDto;
import com.mymusiclist.backend.member.dto.TokenDto;
import com.mymusiclist.backend.member.dto.request.LoginRequest;
import com.mymusiclist.backend.member.dto.request.ResetRequest;
import com.mymusiclist.backend.member.dto.request.SignUpRequest;
import com.mymusiclist.backend.member.dto.request.UpdateRequest;
import com.mymusiclist.backend.member.jwt.JwtTokenProvider;
import com.mymusiclist.backend.member.service.MemberService;
import com.mymusiclist.backend.member.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

  private final MemberService memberService;
  private final TokenService tokenService;
  private final JwtTokenProvider jwtTokenProvider;

  @PostMapping("/signup")
  public ResponseEntity<String> singUp(@Valid @RequestBody SignUpRequest signUpRequest) {
    String response = memberService.signUp(signUpRequest);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/auth")
  public ResponseEntity<String> auth(@RequestParam(name = "email") String email,
      @RequestParam(name = "code") String code) {
    String response = memberService.auth(email, code);
    log.info("Authentication successful for email: {}", email);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/login")
  public ResponseEntity<TokenDto> login(@Valid @RequestBody LoginRequest loginRequest) {
    TokenDto response = memberService.login(loginRequest);
    log.info("login user: {}", loginRequest.getEmail());
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/logout")
  public ResponseEntity<String> logout(HttpServletRequest request) {
    String accessToken = jwtTokenProvider.resolveToken(request);
    String email = memberService.logout(accessToken);
    log.info("logout user: {}", email);
    return ResponseEntity.ok("로그아웃 완료");
  }

  @PostMapping("/reissue")
  public ResponseEntity<TokenDto> reIssue(HttpServletRequest request) {
    String refreshToken = jwtTokenProvider.resolveToken(request);
    TokenDto response = tokenService.reIssue(refreshToken);
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
    log.info("resetPassword user: {}", email);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/withdrawal")
  public ResponseEntity<String> withdrawal(HttpServletRequest request) {
    String accessToken = jwtTokenProvider.resolveToken(request);
    String email = memberService.withdrawal(accessToken);
    log.info("withdrawal user: {}", email);
    return ResponseEntity.ok("회원탈퇴가 정상적으로 완료되었습니다.");
  }

  @PutMapping("/my-info")
  public ResponseEntity<MemberDto> update(HttpServletRequest request,
      @Valid @RequestBody UpdateRequest updateRequest) {
    String accessToken = jwtTokenProvider.resolveToken(request);
    MemberDto response = memberService.update(accessToken,updateRequest);
    log.info("update user: {}, update content - nickname: {}, imageUrl: {}, introduction: {}",
        response.getEmail(), updateRequest.getNickname(), updateRequest.getImageUrl(),
        updateRequest.getIntroduction());
    return ResponseEntity.ok(response);
  }

  @GetMapping("/my-info")
  public ResponseEntity<MemberDto> myInfo(HttpServletRequest request) {
    String accessToken = jwtTokenProvider.resolveToken(request);
    MemberDto response = memberService.myInfo(accessToken);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/info/{nickname}")
  public ResponseEntity<MemberInfoDto> memberInfo(HttpServletRequest request,
      @PathVariable(name = "nickname") String nickname) {
    String accessToken = jwtTokenProvider.resolveToken(request);
    MemberInfoDto response = memberService.memberInfo(accessToken, nickname);
    return ResponseEntity.ok(response);
  }
}
