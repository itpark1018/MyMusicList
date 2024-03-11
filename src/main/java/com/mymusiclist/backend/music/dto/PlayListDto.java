package com.mymusiclist.backend.music.dto;

import com.mymusiclist.backend.music.domain.MusicEntity;
import java.util.List;
import java.util.stream.Collectors;
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
public class PlayListDto {

  private String musicName;
  private String musicUrl;

  public static List<PlayListDto> listOf(List<MusicEntity> musicEntities) {
    return musicEntities.stream()
        .map(PlayListDto::of)
        .collect(Collectors.toList());
  }

  public static PlayListDto of(MusicEntity musicEntity) {
    return PlayListDto.builder()
        .musicName(musicEntity.getMusicName())
        .musicUrl(musicEntity.getMusicUrl())
        .build();
  }
}
