package com.mymusiclist.backend.music.domain;

import com.mymusiclist.backend.member.domain.MemberEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "my_music_list")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyMusicListEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long listId;

  private String listName;

  @ManyToOne
  @JoinColumn(name = "member_id")
  private MemberEntity memberId;

  private Long numberOfMusic;
  private Boolean repeatPlay;
  private Boolean randomPlay;
  private LocalDateTime regDate;

  public void updatePlayOptions(Boolean repeatPlay, Boolean randomPlay) {
    if (!this.repeatPlay.equals(repeatPlay) || !this.randomPlay.equals(randomPlay)) {
      this.repeatPlay = repeatPlay;
      this.randomPlay = randomPlay;
    }
  }
}
