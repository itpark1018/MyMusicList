package com.mymusiclist.backend.exception.impl;

import com.mymusiclist.backend.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class NotFoundMusicException extends AbstractException {

  @Override
  public HttpStatus getHttpStatus() {
    return HttpStatus.NOT_FOUND;
  }

  @Override
  public String getErrorCode() {
    return "MUSIC_NOT_FOUND";
  }

  @Override
  public String getMessage() {
    return "리스트에 해당하는 음악이 없습니다.";
  }
}
