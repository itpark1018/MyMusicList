package com.mymusiclist.backend.admin.dto;

import com.mymusiclist.backend.post.domain.PostEntity;
import com.mymusiclist.backend.type.PostStatus;
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
public class AdminPostListDto {

  private Long postId;
  private String title;
  private String nickName;
  private LocalDateTime createDate;
  private Integer likeCnt;
  private Integer commentCnt;
  private PostStatus status;

  public static List<AdminPostListDto> listOf(List<PostEntity> postEntities) {
    return postEntities.stream()
        .map(AdminPostListDto::of)
        .collect(Collectors.toList());
  }

  public static AdminPostListDto of(PostEntity postEntity) {
    return AdminPostListDto.builder()
        .postId(postEntity.getPostId())
        .title(postEntity.getTitle())
        .nickName(postEntity.getNickname())
        .createDate(postEntity.getCreateDate())
        .likeCnt(postEntity.getLikeCnt())
        .commentCnt(postEntity.getCommentCnt())
        .status(postEntity.getStatus())
        .build();
  }
}
