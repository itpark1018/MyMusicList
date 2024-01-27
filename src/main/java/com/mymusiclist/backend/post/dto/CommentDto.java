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
public class CommentDto {

  private Long postId;
  private String nickname;
  private String comment;
  private Integer likeCnt;
  private LocalDateTime createDate;

  public static List<CommentDto> listOf(List<CommentEntity> commentEntities) {
    return commentEntities.stream()
        .map(CommentDto::of)
        .collect(Collectors.toList());
  }

  public static CommentDto of(CommentEntity commentEntity) {
    return CommentDto.builder()
        .postId(commentEntity.getPostId().getPostId())
        .nickname(commentEntity.getNickname())
        .comment(commentEntity.getComment())
        .likeCnt(commentEntity.getLikeCnt())
        .createDate(commentEntity.getCreateDate())
        .build();
  }

}
