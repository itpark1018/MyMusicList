package com.mymusiclist.backend.exception.impl;

import com.mymusiclist.backend.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class MemberIdMismatchException extends AbstractException {

  @Override
  public HttpStatus getHttpStatus() {
    return HttpStatus.BAD_REQUEST;
  }

  @Override
  public String getErrorCode() {
    return "MEMBER_MISMATCH";
  }

  @Override
  public String getMessage() {
    return "리스트의 회원정보와 회원님의 정보가 일치하지않습니다.";
  }
}
