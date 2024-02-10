package com.mymusiclist.backend.music.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
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
public class UpdateRequest {

  @NotBlank(message = "뮤직 리스트는 공백일 수 없습니다.")
  private String listName;
  private List<String> musicName;
}
