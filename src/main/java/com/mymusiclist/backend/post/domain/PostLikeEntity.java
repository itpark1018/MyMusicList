package com.mymusiclist.backend.post.domain;

import com.mymusiclist.backend.member.domain.MemberEntity;
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
@Table(name = "post_like")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class PostLikeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long postLikeId;

  @ManyToOne
  @JoinColumn(name = "post_id")
  private PostEntity postId;

  @ManyToOne
  @JoinColumn(name = "member_id")
  private MemberEntity memberId;
}
