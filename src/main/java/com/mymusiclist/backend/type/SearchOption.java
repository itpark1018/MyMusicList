package com.mymusiclist.backend.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SearchOption {

  TITLE("title"), // 게시글 제목
  CONTENT("content"), // 게시글 내용
  TITLE_AND_CONTENT("titleAndContent"), // 게시글 제목 또는 게시글 내용
  NICKNAME("nickname"), // 닉네임
  COMMENT("comment"),
  NAME("name"); // 댓글 내용

  private final String value;
}
