package com.mymusiclist.backend.exception.impl;

import com.mymusiclist.backend.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class NotFoundMusicListException extends AbstractException {

  @Override
  public HttpStatus getHttpStatus() {
    return HttpStatus.NOT_FOUND;
  }

  @Override
  public String getErrorCode() {
    return "LIST_NOT_FOUND";
  }

  @Override
  public String getMessage() {
    return "뮤직 리스트를 찾지 못했습니다.";
  }
}
