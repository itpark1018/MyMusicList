package com.mymusiclist.backend.post.domain;

import com.mymusiclist.backend.member.domain.MemberEntity;
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
@Table(name = "comment")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CommentEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long commentId;

  @ManyToOne
  @JoinColumn(name = "post_id")
  private PostEntity postId;

  @ManyToOne
  @JoinColumn(name = "member_id")
  private MemberEntity memberId;

  private String nickname;
  private String comment;
  private Integer likeCnt;
  private String status;
  private LocalDateTime createDate;
  private LocalDateTime modDate;
  private LocalDateTime deleteDate;
}
