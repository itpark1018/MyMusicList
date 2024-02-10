package com.mymusiclist.backend.admin.dto.request;

import com.mymusiclist.backend.post.dto.request.PostRequest;
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

  private Long postId;
  private String title;
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
