package com.mymusiclist.backend.exception.impl;

import com.mymusiclist.backend.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class NotFoundPostException extends AbstractException {

  @Override
  public HttpStatus getHttpStatus() {
    return HttpStatus.NOT_FOUND;
  }

  @Override
  public String getErrorCode() {
    return "POST_NOT_FOUND";
  }

  @Override
  public String getMessage() {
    return "해당하는 게시글이 없습니다.";
  }
}
