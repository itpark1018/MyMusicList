package com.mymusiclist.backend.admin.dto.request;

import com.mymusiclist.backend.post.dto.request.CommentRequest;
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
public class CommentUpdateRequest {

  private Long commentId;
  private String comment;

  public static CommentRequest commentRequest(CommentUpdateRequest commentUpdateRequest) {

    return CommentRequest.builder()
        .comment(commentUpdateRequest.comment)
        .build();
  }
}
