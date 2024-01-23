package com.mymusiclist.backend.post.service;

import com.mymusiclist.backend.post.dto.PostDetailDto;
import com.mymusiclist.backend.post.dto.PostListDto;
import com.mymusiclist.backend.post.dto.request.PostRequest;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface PostService {

  String create(PostRequest postRequest);

  String update(String title, PostRequest postRequest);

  String delete(String title);

  List<PostListDto> getList(Boolean sortByLikes);

  PostDetailDto getDetail(String title, String nickname);

  List<PostListDto> getMyPost();

  void like(String title, String writerNickname);

  List<PostListDto> search(String keyword, String searchOption);
}
