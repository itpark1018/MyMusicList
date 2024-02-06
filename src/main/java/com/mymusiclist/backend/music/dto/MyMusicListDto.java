package com.mymusiclist.backend.music.dto;

import com.mymusiclist.backend.music.domain.MusicEntity;
import com.mymusiclist.backend.music.domain.MyMusicListEntity;
import com.mymusiclist.backend.music.repository.MusicRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
public class MyMusicListDto {

  private String listName;
  private Long numberOfMusic;
  private List<String> musicName;
  private Boolean repeatPlay;
  private Boolean randomPlay;
  private LocalDateTime regDate;

  public static MyMusicListDto of(MyMusicListEntity myMusicListEntity, MusicRepository musicRepository) {

    List<MusicEntity> musicEntities  = musicRepository.findByListId(myMusicListEntity);

    List<String> musicName = new ArrayList<>();
    for (MusicEntity musicEntity : musicEntities) {
      musicName.add(musicEntity.getMusicName());
    }

    return MyMusicListDto.builder()
        .listName(myMusicListEntity.getListName())
        .numberOfMusic(myMusicListEntity.getNumberOfMusic())
        .musicName(musicName)
        .repeatPlay(myMusicListEntity.getRepeatPlay())
        .randomPlay(myMusicListEntity.getRandomPlay())
        .regDate(myMusicListEntity.getRegDate())
        .build();
  }
}

