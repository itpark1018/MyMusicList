package com.mymusiclist.backend.admin.controller;

import com.mymusiclist.backend.admin.dto.AdminCommentListDto;
import com.mymusiclist.backend.admin.dto.AdminPostListDto;
import com.mymusiclist.backend.admin.dto.MemberDetailDto;
import com.mymusiclist.backend.admin.dto.request.CommentUpdateRequest;
import com.mymusiclist.backend.admin.dto.request.MemberUpdateRequest;
import com.mymusiclist.backend.admin.dto.request.PostUpdateRequest;
import com.mymusiclist.backend.admin.service.AdminService;
import com.mymusiclist.backend.type.MemberStatus;
import com.mymusiclist.backend.type.SearchOption;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

  private final AdminService adminService;

  @PatchMapping("/members/{memberId}/status")
  public ResponseEntity<String> setMemberStatus(HttpServletRequest request,
      @PathVariable(name = "memberId") Long memberId,
      @Valid @NotNull(message = "회원상태는 NULL 일 수 없습니다.") @RequestParam MemberStatus memberStatus) {
    String accessToken = getToken(request);
    String response = adminService.setMemberStatus(accessToken, memberId, memberStatus);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/members/{memberId}")
  public ResponseEntity<MemberDetailDto> getMemberInfo(HttpServletRequest request,
      @PathVariable(name = "memberId") Long memberId) {
    String accessToken = getToken(request);
    MemberDetailDto response = adminService.getMemberInfo(accessToken, memberId);
    return ResponseEntity.ok(response);
  }

  @PutMapping("/members/{memberId}")
  public ResponseEntity<String> memberUpdate(HttpServletRequest request,
      @PathVariable(name = "memberId") Long memberId,
      @Valid @RequestBody MemberUpdateRequest memberUpdateRequest) {
    String accessToken = getToken(request);
    String response = adminService.memberUpdate(accessToken, memberId, memberUpdateRequest);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/members")
  public ResponseEntity<List<MemberDetailDto>> searchMember(HttpServletRequest request,
      @Valid @NotNull(message = "검색어가 없으면 안됩니다.") @RequestParam(name = "keyword") String keyword,
      @Valid @NotBlank(message = "검색옵션이 없으면 안됩니다.") @RequestParam(name = "searchOption") SearchOption searchOption) {
    String accessToken = getToken(request);
    List<MemberDetailDto> response = adminService.searchMember(accessToken, keyword, searchOption);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/posts")
  public ResponseEntity<String> postDelete(HttpServletRequest request,
      @Valid @NotNull(message = "게시글 식별자는 NULL 일 수 없습니다.") @RequestParam(name = "postId") Long postId) {
    String accessToken = getToken(request);
    String response = adminService.postDelete(accessToken, postId);
    return ResponseEntity.ok(response);
  }

  @PutMapping("/posts")
  public ResponseEntity<String> postUpdate(HttpServletRequest request,
      @Valid @RequestBody PostUpdateRequest postUpdateRequest) {
    String accessToken = getToken(request);
    String response = adminService.postUpdate(accessToken, postUpdateRequest);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/comments")
  public ResponseEntity<String> commentDelete(HttpServletRequest request,
      @Valid @NotNull(message = "댓글 식별자는 NULL 일 수 없습니다.") @RequestParam(name = "commentId") Long commentId) {
    String accessToken = getToken(request);
    String response = adminService.commentDelete(accessToken, commentId);
    return ResponseEntity.ok(response);
  }

  @PutMapping("/comments")
  public ResponseEntity<String> commentUpdate(HttpServletRequest request,
      @Valid @RequestBody CommentUpdateRequest commentUpdateRequest) {
    String accessToken = getToken(request);
    String response = adminService.commentUpdate(accessToken, commentUpdateRequest);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/posts")
  public ResponseEntity<List<AdminPostListDto>> searchPost(HttpServletRequest request,
      @Valid @NotNull(message = "검색어가 없으면 안됩니다.") @RequestParam(name = "keyword") String keyword,
      @Valid @NotBlank(message = "검색옵션이 없으면 안됩니다.") @RequestParam(name = "searchOption") SearchOption searchOption) {
    String accessToken = getToken(request);
    List<AdminPostListDto> response = adminService.searchPost(accessToken, keyword, searchOption);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/comments")
  public ResponseEntity<List<AdminCommentListDto>> searchComment(HttpServletRequest request,
      @Valid @NotNull(message = "검색어가 없으면 안됩니다.") @RequestParam(name = "keyword") String keyword,
      @Valid @NotBlank(message = "검색옵션이 없으면 안됩니다.") @RequestParam(name = "searchOption") SearchOption searchOption) {
    String accessToken = getToken(request);
    List<AdminCommentListDto> response = adminService.searchComment(accessToken, keyword,
        searchOption);
    return ResponseEntity.ok(response);
  }

  private static String getToken(HttpServletRequest request) {
    String token = request.getHeader("Authorization");
    if (token != null && token.startsWith("Bearer ")) {
      token = token.substring(7); // "Bearer " 이후의 토큰 값만 추출
    }
    return token;
  }
}
