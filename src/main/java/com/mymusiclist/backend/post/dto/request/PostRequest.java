package com.mymusiclist.backend.post.dto.request;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
public class PostRequest {

  @NotEmpty(message = "게시글의 제목은 공백일 수 없습니다.")
  private String title;

  @NotNull(message = "게시글 내용이 없으면 안됩니다.")
  private String content;
  private String listName;
}
