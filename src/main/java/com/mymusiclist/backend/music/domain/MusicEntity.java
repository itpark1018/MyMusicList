package com.mymusiclist.backend.music.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "music")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MusicEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long musicId;

  @ManyToOne
  @JoinColumn(name = "list_id")
  private MyMusicListEntity listId;

  private String musicName;
  private String musicUrl;
}
