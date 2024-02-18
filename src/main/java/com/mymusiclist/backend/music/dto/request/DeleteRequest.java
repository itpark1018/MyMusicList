package com.mymusiclist.backend.music.dto.request;

import jakarta.validation.constraints.NotNull;
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
public class DeleteRequest {

  @NotNull(message = "노래 식별자는 NULL 일 수 없습니다.")
  private List<Long> musicId;
}
