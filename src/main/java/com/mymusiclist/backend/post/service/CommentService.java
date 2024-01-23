package com.mymusiclist.backend.post.service;


import com.mymusiclist.backend.post.domain.PostEntity;
import com.mymusiclist.backend.post.dto.CommentDto;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface CommentService {

  List<CommentDto> getList(PostEntity postEntity);

}
