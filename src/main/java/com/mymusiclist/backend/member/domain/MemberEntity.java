package com.mymusiclist.backend.member.domain;

import com.mymusiclist.backend.type.MemberStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class MemberEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long memberId;

  @Column(unique = true)
  private String email;

  private String password;
  private String name;
  private String nickname;
  private LocalDateTime regDate;
  private LocalDateTime modDate;
  private Boolean auth;
  private String authCode;
  private String imageUrl;
  private String introduction;

  @Enumerated(EnumType.STRING)
  private MemberStatus status;

  private Boolean adminYn;
  private String passwordAuthCode;
  private LocalDateTime passwordDate;
}
