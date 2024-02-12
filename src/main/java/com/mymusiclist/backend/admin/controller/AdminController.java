package com.mymusiclist.backend.admin.controller;

import com.mymusiclist.backend.admin.dto.AdminCommentListDto;
import com.mymusiclist.backend.admin.dto.AdminPostListDto;
import com.mymusiclist.backend.admin.dto.MemberDetailDto;
import com.mymusiclist.backend.admin.dto.request.CommentUpdateRequest;
import com.mymusiclist.backend.admin.dto.request.MemberStatusRequest;
import com.mymusiclist.backend.admin.dto.request.MemberUpdateRequest;
import com.mymusiclist.backend.admin.dto.request.PostUpdateRequest;
import com.mymusiclist.backend.admin.service.AdminService;
import com.mymusiclist.backend.post.dto.PostDetailDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

  private final AdminService adminService;

  @PostMapping("/member/status")
  public ResponseEntity<String> setMemberStatus(
      @Valid @RequestBody MemberStatusRequest memberStatusRequest) {
    String response = adminService.setMemberStatus(memberStatusRequest);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/member/info")
  public ResponseEntity<MemberDetailDto> getMemberInfo(
      @Valid @NotBlank(message = "회원 식별자는 공백일 수 없습니다.") @RequestParam(name = "memberId") Long memberId) {
    MemberDetailDto response = adminService.getMemberInfo(memberId);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/member/update")
  public ResponseEntity<String> memberUpdate(@Valid @RequestBody MemberUpdateRequest memberUpdateRequest) {
    String response = adminService.memberUpdate(memberUpdateRequest);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/member/search/name")
  public ResponseEntity<List<MemberDetailDto>> searchName(
      @Valid @NotBlank(message = "회원 이름은 공백일 수 없습니다.") @RequestParam(name = "name") String name) {
    List<MemberDetailDto> response = adminService.searchName(name);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/member/search/nickname")
  public ResponseEntity<MemberDetailDto> searchNickname(
      @Valid @NotBlank(message = "닉네임은 공백일 수 없습니다.")@RequestParam(name = "nickname") String nickname) {
    MemberDetailDto response = adminService.searchNickname(nickname);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/post/delete")
  public ResponseEntity<String> postDelete(@Valid @NotBlank(message = "게시글 식별자는 공백일 수 없습니다.") @RequestParam(name = "postId") Long postId) {
    String response = adminService.postDelete(postId);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/post/update")
  public ResponseEntity<String> postUpdate(@Valid @RequestBody PostUpdateRequest postUpdateRequest) {
    String response = adminService.postUpdate(postUpdateRequest);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/comment/delete")
  public ResponseEntity<String> commentDelete(@Valid @NotBlank(message = "댓글 식별자는 공백일 수 없습니다.") @RequestParam(name = "commentId") Long commentId) {
    String response = adminService.commentDelete(commentId);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/comment/update")
  public ResponseEntity<String> commentUpdate(
      @Valid @RequestBody CommentUpdateRequest commentUpdateRequest) {
    String response = adminService.commentUpdate(commentUpdateRequest);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/search/post")
  public ResponseEntity<List<AdminPostListDto>> searchPost(
      @Valid @NotNull(message = "검색어가 없으면 안됩니다.") @RequestParam(name = "keyword") String keyword,
      @Valid @NotBlank(message = "검색옵션이 없으면 안됩니다.") @RequestParam(name = "searchOption") String searchOption) {
    List<AdminPostListDto> response = adminService.searchPost(keyword, searchOption);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/search/comment")
  public ResponseEntity<List<AdminCommentListDto>> searchComment(
      @Valid @NotNull(message = "검색어가 없으면 안됩니다.") @RequestParam(name = "keyword") String keyword,
      @Valid @NotBlank(message = "검색옵션이 없으면 안됩니다.") @RequestParam(name = "searchOption") String searchOption) {
    List<AdminCommentListDto> response = adminService.searchComment(keyword,
        searchOption);
    return ResponseEntity.ok(response);
  }

}
