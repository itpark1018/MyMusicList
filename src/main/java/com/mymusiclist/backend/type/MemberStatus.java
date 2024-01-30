package com.mymusiclist.backend.type;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum MemberStatus {

  WAITING_FOR_APPROVAL,
  ACTIVE,
  SUSPENDED,
  WITHDRAWN
}