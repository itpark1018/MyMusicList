package com.mymusiclist.backend.post.service;

import com.mymusiclist.backend.post.domain.CommentEntity;
import com.mymusiclist.backend.post.domain.PostEntity;
import com.mymusiclist.backend.post.dto.CommentDto;
import com.mymusiclist.backend.post.repository.CommentRepository;
import com.mymusiclist.backend.type.CommentStatus;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

  private final CommentRepository commentRepository;

  @Override
  public List<CommentDto> getList(PostEntity postEntity) {

    List<CommentEntity> commentList = commentRepository.findByPostIdAndStatusOrderByCreateDateDesc(
        postEntity, CommentStatus.ACTIVE.getDescription());

    return CommentDto.listOf(commentList);
  }
}
