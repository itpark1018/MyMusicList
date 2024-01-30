package com.mymusiclist.backend.post.dto;

import com.mymusiclist.backend.post.domain.CommentEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyCommentDto {

  private Long postId;
  private String title;
  private String comment;
  private Integer likeCnt;
  private LocalDateTime createDate;

  public static List<MyCommentDto> listOf(List<CommentEntity> commentEntities) {
    return commentEntities.stream()
        .map(MyCommentDto::of)
        .collect(Collectors.toList());
  }

  public static MyCommentDto of(CommentEntity commentEntity) {
    return MyCommentDto.builder()
        .postId(commentEntity.getPostId().getPostId())
        .title(commentEntity.getPostId().getTitle())
        .comment(commentEntity.getComment())
        .likeCnt(commentEntity.getLikeCnt())
        .createDate(commentEntity.getCreateDate())
        .build();
  }
}
