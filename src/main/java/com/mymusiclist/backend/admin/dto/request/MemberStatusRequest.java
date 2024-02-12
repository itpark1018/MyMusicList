package com.mymusiclist.backend.admin.dto.request;

import com.mymusiclist.backend.type.MemberStatus;
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
public class MemberStatusRequest {

  @NotBlank(message = "회원 식별자는 공백일 수 없습니다.")
  private Long memberId;

  @NotBlank(message = "회원상태는 공백일 수 없습니다.")
  private MemberStatus status;
}
