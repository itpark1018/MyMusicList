package com.mymusiclist.backend.admin.dto.request;

import jakarta.validation.constraints.NotBlank;
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
public class MemberUpdateRequest {

  @NotBlank(message = "회원식별자는 공백일 수 없습니다.")
  private Long memberId;

  @NotBlank(message = "닉네임은 공백일 수 없습니다.")
  private String nickname;
  private String imageUrl;
  private String introduction;
}
