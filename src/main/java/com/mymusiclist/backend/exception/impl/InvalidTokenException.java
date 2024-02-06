package com.mymusiclist.backend.exception.impl;

import com.mymusiclist.backend.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class InvalidTokenException extends AbstractException {

  @Override
  public HttpStatus getHttpStatus() {
    return HttpStatus.BAD_REQUEST;
  }

  @Override
  public String getErrorCode() {
    return "TOKEN_INVALID";
  }

  @Override
  public String getMessage() {
    return "유효하지 않은 토큰입니다.";
  }
}
