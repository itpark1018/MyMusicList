package com.mymusiclist.backend.admin.controller;

import com.mymusiclist.backend.admin.dto.AdminCommentListDto;
import com.mymusiclist.backend.admin.dto.AdminPostListDto;
import com.mymusiclist.backend.admin.dto.MemberDetailDto;
import com.mymusiclist.backend.admin.dto.request.CommentUpdateRequest;
import com.mymusiclist.backend.admin.dto.request.MemberUpdateRequest;
import com.mymusiclist.backend.admin.dto.request.PostUpdateRequest;
import com.mymusiclist.backend.admin.service.AdminService;
import com.mymusiclist.backend.type.MemberStatus;
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
  public ResponseEntity<String> setMemberStatus(
      @PathVariable(name = "memberId") Long memberId,
      @Valid @NotNull(message = "회원상태는 NULL 일 수 없습니다.") @RequestParam MemberStatus memberStatus) {
    String response = adminService.setMemberStatus(memberId, memberStatus);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/members/{memberId}")
  public ResponseEntity<MemberDetailDto> getMemberInfo(
      @PathVariable(name = "memberId") Long memberId) {
    MemberDetailDto response = adminService.getMemberInfo(memberId);
    return ResponseEntity.ok(response);
  }

  @PutMapping("/members/{memberId}")
  public ResponseEntity<String> memberUpdate(
      @PathVariable(name = "memberId") Long memberId,
      @Valid @RequestBody MemberUpdateRequest memberUpdateRequest) {
    String response = adminService.memberUpdate(memberId, memberUpdateRequest);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/members")
  public ResponseEntity<List<MemberDetailDto>> searchMember(
      @Valid @NotNull(message = "검색어가 없으면 안됩니다.") @RequestParam(name = "keyword") String keyword,
      @Valid @NotBlank(message = "검색옵션이 없으면 안됩니다.") @RequestParam(name = "searchOption") String searchOption) {
    List<MemberDetailDto> response = adminService.searchMember(keyword, searchOption);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/posts")
  public ResponseEntity<String> postDelete(
      @Valid @NotNull(message = "게시글 식별자는 NULL 일 수 없습니다.") @RequestParam(name = "postId") Long postId) {
    String response = adminService.postDelete(postId);
    return ResponseEntity.ok(response);
  }

  @PutMapping("/posts")
  public ResponseEntity<String> postUpdate(
      @Valid @RequestBody PostUpdateRequest postUpdateRequest) {
    String response = adminService.postUpdate(postUpdateRequest);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/comments")
  public ResponseEntity<String> commentDelete(
      @Valid @NotNull(message = "댓글 식별자는 NULL 일 수 없습니다.") @RequestParam(name = "commentId") Long commentId) {
    String response = adminService.commentDelete(commentId);
    return ResponseEntity.ok(response);
  }

  @PutMapping("/comments")
  public ResponseEntity<String> commentUpdate(
      @Valid @RequestBody CommentUpdateRequest commentUpdateRequest) {
    String response = adminService.commentUpdate(commentUpdateRequest);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/posts")
  public ResponseEntity<List<AdminPostListDto>> searchPost(
      @Valid @NotNull(message = "검색어가 없으면 안됩니다.") @RequestParam(name = "keyword") String keyword,
      @Valid @NotBlank(message = "검색옵션이 없으면 안됩니다.") @RequestParam(name = "searchOption") String searchOption) {
    List<AdminPostListDto> response = adminService.searchPost(keyword, searchOption);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/comments")
  public ResponseEntity<List<AdminCommentListDto>> searchComment(
      @Valid @NotNull(message = "검색어가 없으면 안됩니다.") @RequestParam(name = "keyword") String keyword,
      @Valid @NotBlank(message = "검색옵션이 없으면 안됩니다.") @RequestParam(name = "searchOption") String searchOption) {
    List<AdminCommentListDto> response = adminService.searchComment(keyword,
        searchOption);
    return ResponseEntity.ok(response);
  }

}
