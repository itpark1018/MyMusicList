package com.mymusiclist.backend.exception.impl;

import com.mymusiclist.backend.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class ExpiredException extends AbstractException {

  @Override
  public int getStatusCode() {
    return HttpStatus.BAD_REQUEST.value();
  }

  @Override
  public String getMessage() {
    return "변경기간이 만료되었습니다.";
  }
}
