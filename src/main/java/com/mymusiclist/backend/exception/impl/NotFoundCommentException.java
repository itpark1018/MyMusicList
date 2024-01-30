package com.mymusiclist.backend.exception.impl;

import com.mymusiclist.backend.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class NotFoundCommentException extends AbstractException {

  @Override
  public HttpStatus getHttpStatus() {
    return HttpStatus.NOT_FOUND;
  }

  @Override
  public String getErrorCode() {
    return "COMMENT_NOT_FOUND";
  }

  @Override
  public String getMessage() {
    return "댓글을 찾을 수 없습니다.";
  }
}
