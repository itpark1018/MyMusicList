package com.mymusiclist.backend.exception.impl;

import com.mymusiclist.backend.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class ListEmptyException extends AbstractException {

  @Override
  public HttpStatus getHttpStatus() {
    return HttpStatus.BAD_REQUEST;
  }

  @Override
  public String getErrorCode() {
    return "LIST_EMPTY";
  }

  @Override
  public String getMessage() {
    return "뮤직 리스트가 비어있습니다. 노래를 추가해주세요.";
  }
}
