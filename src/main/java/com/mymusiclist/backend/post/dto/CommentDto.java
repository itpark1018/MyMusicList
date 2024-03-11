package com.mymusiclist.backend.post.dto;

import com.mymusiclist.backend.post.domain.CommentEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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

  private Long commentId;
  private String nickname;
  private String comment;
  private Integer likeCnt;
  private LocalDateTime createDate;
  private Boolean commentYn;

  public static List<CommentDto> listOf(List<CommentEntity> commentEntities, List<Boolean> commentYnList) {
    return IntStream.range(0, commentEntities.size())
        .mapToObj(i -> of(commentEntities.get(i), commentYnList.get(i)))
        .collect(Collectors.toList());
  }

  public static CommentDto of(CommentEntity commentEntity, Boolean commentYn) {
    return CommentDto.builder()
        .commentId(commentEntity.getCommentId())
        .nickname(commentEntity.getNickname())
        .comment(commentEntity.getComment())
        .likeCnt(commentEntity.getLikeCnt())
        .createDate(commentEntity.getCreateDate())
        .commentYn(commentYn)
        .build();
  }

}
