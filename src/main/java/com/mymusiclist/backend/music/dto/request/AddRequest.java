package com.mymusiclist.backend.music.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddRequest {

  @NotBlank(message = "노래이름은 공백일 수 없습니다.")
  private String musicName;

  @NotBlank(message = "노래링크는 공백일 수 없습니다.")
  private String musicUrl;
}
