package com.mymusiclist.backend.admin.dto.request;

import com.mymusiclist.backend.post.dto.request.CommentRequest;
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
public class CommentUpdateRequest {

  @NotBlank(message = "댓글 식별자는 공백일 수 없습니다.")
  private Long commentId;

  @NotNull(message = "댓글내용이 없으면 안됩니다.")
  private String comment;

  public static CommentRequest commentRequest(CommentUpdateRequest commentUpdateRequest) {

    return CommentRequest.builder()
        .comment(commentUpdateRequest.comment)
        .build();
  }
}
