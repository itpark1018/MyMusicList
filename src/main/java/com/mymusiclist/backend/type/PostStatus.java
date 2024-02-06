package com.mymusiclist.backend.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PostStatus {

  ACTIVE("게시"),
  DELETED("삭제");

  private final String description;
}