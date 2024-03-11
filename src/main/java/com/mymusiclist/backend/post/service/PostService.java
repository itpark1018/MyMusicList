package com.mymusiclist.backend.post.service;

import com.mymusiclist.backend.post.dto.PostDetailDto;
import com.mymusiclist.backend.post.dto.PostListDto;
import com.mymusiclist.backend.post.dto.request.PostRequest;
import com.mymusiclist.backend.type.SearchOption;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface PostService {

  String create(String accessToken, PostRequest postRequest);

  String update(String accessToken, Long postId, PostRequest postRequest);

  String delete(String accessToken, Long postId);

  List<PostListDto> getList(Boolean sortByLikes);

  PostDetailDto getDetail(Long postId);

  List<PostListDto> getMyPost(String accessToken);

  void like(String accessToken, Long postId);

  List<PostListDto> search(String accessToken, String keyword, SearchOption searchOption);
}
