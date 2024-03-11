package com.mymusiclist.backend.post.service;


import com.mymusiclist.backend.post.domain.PostEntity;
import com.mymusiclist.backend.post.dto.CommentDto;
import com.mymusiclist.backend.post.dto.MyCommentDto;
import com.mymusiclist.backend.post.dto.request.CommentRequest;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface CommentService {

  List<CommentDto> getList(PostEntity postEntity);

  String create(String accessToken, Long postId, CommentRequest commentRequest);

  String delete(String accessToken, Long postId, Long commentId);

  String update(String accessToken, Long postId, Long commentId, CommentRequest commentRequest);

  void like(String accessToken, Long postId, Long commentId);

  List<MyCommentDto> getMyComment(String accessToken);
}
