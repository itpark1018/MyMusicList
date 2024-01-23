package com.mymusiclist.backend.post.dto;

import java.time.LocalDateTime;
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
}
