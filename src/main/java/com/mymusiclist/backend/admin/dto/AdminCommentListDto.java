package com.mymusiclist.backend.admin.dto;

import com.mymusiclist.backend.post.domain.CommentEntity;
import com.mymusiclist.backend.type.CommentStatus;
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
public class AdminCommentListDto {

  private Long commentId;
  private Long postId;
  private Long memberId;
  private String nickname;
  private String comment;
  private Integer likeCnt;
  private CommentStatus status;
  private LocalDateTime createDate;
  private LocalDateTime modDate;
  private LocalDateTime deleteDate;

  public static List<AdminCommentListDto> listOf(List<CommentEntity> commentEntities) {
    return commentEntities.stream()
        .map(AdminCommentListDto::of)
        .collect(Collectors.toList());
  }

  public static AdminCommentListDto of(CommentEntity commentEntity) {
    return AdminCommentListDto.builder()
        .commentId(commentEntity.getCommentId())
        .postId(commentEntity.getPostId().getPostId())
        .memberId(commentEntity.getMemberId().getMemberId())
        .nickname(commentEntity.getNickname())
        .comment(commentEntity.getComment())
        .likeCnt(commentEntity.getLikeCnt())
        .status(commentEntity.getStatus())
        .createDate(commentEntity.getCreateDate())
        .modDate(commentEntity.getModDate())
        .deleteDate(commentEntity.getDeleteDate())
        .build();
  }
}
