package com.mymusiclist.backend.exception.impl;

import com.mymusiclist.backend.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class DuplicateListException extends AbstractException {

  @Override
  public HttpStatus getHttpStatus() {
    return HttpStatus.BAD_REQUEST;
  }

  @Override
  public String getErrorCode() {
    return "LIST_DUPLICATE";
  }

  @Override
  public String getMessage() {
    return "동일한 뮤직 리스트 이름이 이미 있습니다.";
  }
}
