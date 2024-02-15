package com.mymusiclist.backend.exception.impl;

import com.mymusiclist.backend.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class InvalidSearchOptionException extends AbstractException {

  @Override
  public HttpStatus getHttpStatus() {
    return HttpStatus.BAD_REQUEST;
  }

  @Override
  public String getErrorCode() {
    return "INVALID_SEARCH_OPTION";
  }

  @Override
  public String getMessage() {
    return "잘못된 검색 옵션입니다.";
  }
}
