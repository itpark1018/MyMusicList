package com.mymusiclist.backend.post.controller;

import com.mymusiclist.backend.post.dto.PostDetailDto;
import com.mymusiclist.backend.post.dto.PostListDto;
import com.mymusiclist.backend.post.dto.request.PostRequest;
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

  @PostMapping("/create")
  public ResponseEntity<String> create(@RequestBody PostRequest postRequest) {
    String response = postService.create(postRequest);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/{title}/update")
  public ResponseEntity<String> update(@PathVariable String title,
      @RequestBody PostRequest postRequest) {
    String response = postService.update(title, postRequest);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/{title}/delete")
  public ResponseEntity<String> delete(@PathVariable String title) {
    String response = postService.delete(title);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/list")
  public ResponseEntity<List<PostListDto>> getList(
      @RequestParam(name = "sortByLikes", defaultValue = "false") Boolean sortByLikes) {
    List<PostListDto> response = postService.getList(sortByLikes);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/list/{title}")
  public ResponseEntity<PostDetailDto> getDetail(@PathVariable String title,
      @RequestParam(name = "nickname") String nickname) {
    PostDetailDto response = postService.getDetail(title, nickname);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/myPost")
  public ResponseEntity<List<PostListDto>> getMyPost() {
    List<PostListDto> response = postService.getMyPost();
    return ResponseEntity.ok(response);
  }

  @PostMapping("/list/{title}/like")
  public void postLike(@PathVariable String title,
      @RequestParam(name = "nickname") String writerNickname) {
    postService.like(title, writerNickname);
  }

  @GetMapping("/search")
  public ResponseEntity<List<PostListDto>> search(@RequestParam(name = "keyword") String keyword,
      @RequestParam(name = "searchOption") String searchOption) {
    List<PostListDto> response = postService.search(keyword, searchOption);
    return ResponseEntity.ok(response);
  }

  @PostMapping("list/{title}/comment")
  public void comment(@PathVariable String title) {

  }

}
