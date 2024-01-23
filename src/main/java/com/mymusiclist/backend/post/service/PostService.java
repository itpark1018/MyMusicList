package com.mymusiclist.backend.post.service;

import com.mymusiclist.backend.post.dto.PostDetailDto;
import com.mymusiclist.backend.post.dto.PostListDto;
import com.mymusiclist.backend.post.dto.request.PostRequest;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface PostService {

  String create(PostRequest postRequest);

  String update(Long postId, PostRequest postRequest);

  String delete(Long postId);

  List<PostListDto> getList(Boolean sortByLikes);

  PostDetailDto getDetail(Long postId);

  List<PostListDto> getMyPost();

  void like(Long postId);

  List<PostListDto> search(String keyword, String searchOption);
}
