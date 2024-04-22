package com.mymusiclist.backend.post.controller;

import com.mymusiclist.backend.member.jwt.JwtTokenProvider;
import com.mymusiclist.backend.post.dto.MyCommentDto;
import com.mymusiclist.backend.post.dto.PostDetailDto;
import com.mymusiclist.backend.post.dto.PostListDto;
import com.mymusiclist.backend.post.dto.request.CommentRequest;
import com.mymusiclist.backend.post.dto.request.PostRequest;
import com.mymusiclist.backend.post.service.CommentService;
import com.mymusiclist.backend.post.service.PostService;
import com.mymusiclist.backend.type.SearchOption;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
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
@RequestMapping("/posts")
public class PostController {

  private final PostService postService;
  private final CommentService commentService;
  private final JwtTokenProvider jwtTokenProvider;

  @PostMapping
  public ResponseEntity<String> create(HttpServletRequest request,
      @Valid @RequestBody PostRequest postRequest) {
    String accessToken = jwtTokenProvider.resolveToken(request);
    String response = postService.create(accessToken, postRequest);
    return ResponseEntity.ok(response);
  }

  @PutMapping("/{postId}")
  public ResponseEntity<String> update(HttpServletRequest request,
      @Valid @PathVariable(name = "postId") Long postId,
      @RequestBody PostRequest postRequest) {
    String accessToken = jwtTokenProvider.resolveToken(request);
    String response = postService.update(accessToken, postId, postRequest);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{postId}")
  public ResponseEntity<String> delete(HttpServletRequest request,
      @PathVariable(name = "postId") Long postId) {
    String accessToken = jwtTokenProvider.resolveToken(request);
    String response = postService.delete(accessToken, postId);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/lists")
  public ResponseEntity<List<PostListDto>> getList(
      @RequestParam(name = "sortByLikes", defaultValue = "false") Boolean sortByLikes) {
    List<PostListDto> response = postService.getList(sortByLikes);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/lists/{postId}")
  public ResponseEntity<PostDetailDto> getDetail(@PathVariable(name = "postId") Long postId) {
    PostDetailDto response = postService.getDetail(postId);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/my-post")
  public ResponseEntity<List<PostListDto>> getMyPost(HttpServletRequest request) {
    String accessToken = jwtTokenProvider.resolveToken(request);
    List<PostListDto> response = postService.getMyPost(accessToken);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/lists/{postId}/like")
  public void postLike(HttpServletRequest request, @PathVariable(name = "postId") Long postId) {
    String accessToken = jwtTokenProvider.resolveToken(request);
    postService.like(accessToken, postId);
  }

  @GetMapping("/search")
  public ResponseEntity<List<PostListDto>> search(HttpServletRequest request,
      @Valid @NotNull(message = "검색어가 없으면 안됩니다.") @RequestParam(name = "keyword") String keyword,
      @RequestParam(name = "searchOption") SearchOption searchOption) {
    String accessToken = jwtTokenProvider.resolveToken(request);
    List<PostListDto> response = postService.search(accessToken, keyword, searchOption);
    log.info("post searchKeyword: {}, post searchOption: {}", keyword, searchOption);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/lists/{postId}/comments")
  public ResponseEntity<String> commentCreate(HttpServletRequest request,
      @PathVariable(name = "postId") Long postId,
      @Valid @RequestBody CommentRequest commentRequest) {
    String accessToken = jwtTokenProvider.resolveToken(request);
    String response = commentService.create(accessToken, postId, commentRequest);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/lists/{postId}/comments/{commentId}")
  public ResponseEntity<String> commentDelete(HttpServletRequest request,
      @PathVariable(name = "postId") Long postId,
      @PathVariable(name = "commentId") Long commentId) {
    String accessToken = jwtTokenProvider.resolveToken(request);
    String response = commentService.delete(accessToken, postId, commentId);
    return ResponseEntity.ok(response);
  }

  @PutMapping("/lists/{postId}/comments/{commentId}")
  public ResponseEntity<String> commentUpdate(HttpServletRequest request,
      @PathVariable(name = "postId") Long postId, @PathVariable(name = "commentId") Long commentId,
      @Valid @RequestBody CommentRequest commentRequest) {
    String accessToken = jwtTokenProvider.resolveToken(request);
    String response = commentService.update(accessToken, postId, commentId, commentRequest);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/lists/{postId}/comments/{commentId}/like")
  public void commentLike(HttpServletRequest request, @PathVariable(name = "postId") Long postId,
      @PathVariable(name = "commentId") Long commentId) {
    String accessToken = jwtTokenProvider.resolveToken(request);
    commentService.like(accessToken, postId, commentId);
  }

  @GetMapping("/my-comment")
  public ResponseEntity<List<MyCommentDto>> getMyComment(HttpServletRequest request) {
    String accessToken = jwtTokenProvider.resolveToken(request);
    List<MyCommentDto> response = commentService.getMyComment(accessToken);
    return ResponseEntity.ok(response);
  }
}
