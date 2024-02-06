package com.mymusiclist.backend.admin.dto.request;

import com.mymusiclist.backend.type.MemberStatus;
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

  private Long memberId;
  private MemberStatus status;
}
