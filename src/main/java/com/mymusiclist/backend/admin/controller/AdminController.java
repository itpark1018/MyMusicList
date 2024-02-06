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
      @RequestBody MemberStatusRequest memberStatusRequest) {
    String response = adminService.setMemberStatus(memberStatusRequest);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/member/info")
  public ResponseEntity<MemberDetailDto> getMemberInfo(
      @RequestParam(name = "memberId") Long memberId) {
    MemberDetailDto response = adminService.getMemberInfo(memberId);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/member/update")
  public ResponseEntity<String> memberUpdate(@RequestBody MemberUpdateRequest memberUpdateRequest) {
    String response = adminService.memberUpdate(memberUpdateRequest);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/member/search/name")
  public ResponseEntity<List<MemberDetailDto>> searchName(
      @RequestParam(name = "name") String name) {
    List<MemberDetailDto> response = adminService.searchName(name);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/member/search/nickname")
  public ResponseEntity<MemberDetailDto> searchNickname(
      @RequestParam(name = "nickname") String nickname) {
    MemberDetailDto response = adminService.searchNickname(nickname);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/post/delete")
  public ResponseEntity<String> postDelete(@RequestParam(name = "postId") Long postId) {
    String response = adminService.postDelete(postId);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/post/update")
  public ResponseEntity<String> postUpdate(@RequestBody PostUpdateRequest postUpdateRequest) {
    String response = adminService.postUpdate(postUpdateRequest);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/comment/delete")
  public ResponseEntity<String> commentDelete(@RequestParam(name = "commentId") Long commentId) {
    String response = adminService.commentDelete(commentId);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/comment/update")
  public ResponseEntity<String> commentUpdate(
      @RequestBody CommentUpdateRequest commentUpdateRequest) {
    String response = adminService.commentUpdate(commentUpdateRequest);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/search/post")
  public ResponseEntity<List<AdminPostListDto>> searchPost(
      @RequestParam(name = "keyword") String keyword,
      @RequestParam(name = "searchOption") String searchOption) {
    List<AdminPostListDto> response = adminService.searchPost(keyword, searchOption);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/search/comment")
  public ResponseEntity<List<AdminCommentListDto>> searchComment(
      @RequestParam(name = "keyword") String keyword,
      @RequestParam(name = "searchOption") String searchOption) {
    List<AdminCommentListDto> response = adminService.searchComment(keyword,
        searchOption);
    return ResponseEntity.ok(response);
  }

}
