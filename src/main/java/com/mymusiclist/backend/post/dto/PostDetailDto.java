package com.mymusiclist.backend.post.dto;

import com.mymusiclist.backend.music.dto.PlayListDto;
import java.time.LocalDateTime;
import java.util.List;
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
public class PostDetailDto {

  private Long postId;
  private String title;
  private String nickname;
  private LocalDateTime createDate;
  private Integer likeCnt;
  private Integer commentCnt;
  private String listName;
  private List<PlayListDto> musicList;
  private List<CommentDto> comment;
  private Boolean likeYn;
}
