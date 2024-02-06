package com.mymusiclist.backend.post.controller;

import com.mymusiclist.backend.post.dto.MyCommentDto;
import com.mymusiclist.backend.post.dto.PostDetailDto;
import com.mymusiclist.backend.post.dto.PostListDto;
import com.mymusiclist.backend.post.dto.request.CommentRequest;
import com.mymusiclist.backend.post.dto.request.PostRequest;
import com.mymusiclist.backend.post.service.CommentService;
import com.mymusiclist.backend.post.service.PostService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {

  private final PostService postService;
  private final CommentService commentService;

  @PostMapping("/create")
  public ResponseEntity<String> create(@RequestBody PostRequest postRequest) {
    String response = postService.create(postRequest);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/{postId}/update")
  public ResponseEntity<String> update(@PathVariable(name = "postId") Long postId,
      @RequestBody PostRequest postRequest) {
    String response = postService.update(postId, postRequest);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/{postId}/delete")
  public ResponseEntity<String> delete(@PathVariable(name = "postId") Long postId) {
    String response = postService.delete(postId);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/list")
  public ResponseEntity<List<PostListDto>> getList(
      @RequestParam(name = "sortByLikes", defaultValue = "false") Boolean sortByLikes) {
    List<PostListDto> response = postService.getList(sortByLikes);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/list/{postId}")
  public ResponseEntity<PostDetailDto> getDetail(@PathVariable(name = "postId") Long postId) {
    PostDetailDto response = postService.getDetail(postId);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/myPost")
  public ResponseEntity<List<PostListDto>> getMyPost() {
    List<PostListDto> response = postService.getMyPost();
    return ResponseEntity.ok(response);
  }

  @PostMapping("/list/{postId}/like")
  public void postLike(@PathVariable(name = "postId") Long postId) {
    postService.like(postId);
  }

  @GetMapping("/search")
  public ResponseEntity<List<PostListDto>> search(@RequestParam(name = "keyword") String keyword,
      @RequestParam(name = "searchOption") String searchOption) {
    List<PostListDto> response = postService.search(keyword, searchOption);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/list/{postId}/comment/create")
  public ResponseEntity<String> commentCreate(@PathVariable(name = "postId") Long postId,
      @RequestBody
      CommentRequest commentRequest) {
    String response = commentService.create(postId, commentRequest);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/list/{postId}/comment/delete/{commentId}")
  public ResponseEntity<String> commentDelete(@PathVariable(name = "postId") Long postId,
      @PathVariable(name = "commentId") Long commentId) {
    String response = commentService.delete(postId, commentId);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/list/{postId}/comment/update/{commentId}")
  public ResponseEntity<String> commentUpdate(@PathVariable(name = "postId") Long postId,
      @PathVariable(name = "commentId") Long commentId,
      @RequestBody CommentRequest commentRequest) {
    String response = commentService.update(postId, commentId, commentRequest);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/list/{postId}/comment/{commentId}/like")
  public void commentLike(@PathVariable(name = "postId") Long postId,
      @PathVariable(name = "commentId") Long commentId) {
    commentService.like(postId, commentId);
  }

  @GetMapping("/myComment")
  public ResponseEntity<List<MyCommentDto>> getMyComment() {
    List<MyCommentDto> response = commentService.getMyComment();
    return ResponseEntity.ok(response);
  }
}
