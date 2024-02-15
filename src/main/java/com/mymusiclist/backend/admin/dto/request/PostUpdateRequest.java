package com.mymusiclist.backend.admin.dto.request;

import com.mymusiclist.backend.post.dto.request.PostRequest;
import jakarta.validation.constraints.NotBlank;
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
public class PostUpdateRequest {

  @NotNull(message = "게시글 식별자는 NULL 일 수 없습니다.")
  private Long postId;

  @NotBlank(message = "게시글의 제목은 공백일 수 없습니다.")
  private String title;

  @NotNull(message = "게시글 내용이 없으면 안됩니다.")
  private String content;
  private String listName;

  public static PostRequest postRequest(PostUpdateRequest postUpdateRequest) {

    return PostRequest.builder()
        .title(postUpdateRequest.getTitle())
        .content(postUpdateRequest.content)
        .listName(postUpdateRequest.listName)
        .build();
  }
}
