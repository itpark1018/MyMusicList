package com.mymusiclist.backend.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SearchOption {

  TITLE("title"),
  CONTENT("content"),
  TITLE_AND_CONTENT("titleAndContent"),
  NICKNAME("nickname");

  private final String value;
}
