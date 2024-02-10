package com.mymusiclist.backend.post.dto;

import com.mymusiclist.backend.admin.dto.AdminPostListDto;
import com.mymusiclist.backend.post.domain.PostEntity;
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
public class PostListDto {

  private Long postId;
  private String title;
  private String nickName;
  private LocalDateTime createDate;
  private Integer likeCnt;
  private Integer commentCnt;

  public static List<PostListDto> listOf(List<PostEntity> postEntities) {
    return postEntities.stream()
        .map(PostListDto::of)
        .collect(Collectors.toList());
  }

  public static PostListDto of(PostEntity postEntity) {
    return PostListDto.builder()
        .postId(postEntity.getPostId())
        .title(postEntity.getTitle())
        .nickName(postEntity.getNickname())
        .createDate(postEntity.getCreateDate())
        .likeCnt(postEntity.getLikeCnt())
        .commentCnt(postEntity.getCommentCnt())
        .build();
  }
}
