package com.mymusiclist.backend.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberStatus {

  WAITING_FOR_APPROVAL("가입대기"),
  ACTIVE("이용중"),
  SUSPENDED("계정정지");

  private final String description;
}