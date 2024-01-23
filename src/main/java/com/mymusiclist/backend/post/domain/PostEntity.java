package com.mymusiclist.backend.post.domain;

import com.mymusiclist.backend.member.domain.MemberEntity;
import com.mymusiclist.backend.music.domain.MyMusicListEntity;
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
@Table(name = "post")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class PostEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long postId;

  private String title;

  @ManyToOne
  @JoinColumn(name = "member_id")
  private MemberEntity memberId;

  private String nickname;
  private String content;

  @ManyToOne
  @JoinColumn(name = "list_id")
  private MyMusicListEntity listId;

  private Integer likeCnt;
  private Integer commentCnt;
  private String status;
  private LocalDateTime createDate;
  private LocalDateTime modDate;
  private LocalDateTime deleteDate;
}
