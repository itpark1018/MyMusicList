package com.mymusiclist.backend.admin.dto;

import com.mymusiclist.backend.member.domain.MemberEntity;
import com.mymusiclist.backend.type.MemberStatus;
import java.time.LocalDateTime;
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
public class MemberDetailDto {

  private Long memberId;
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
  private MemberStatus status;
  private Boolean adminYn;
  private String passwordAuthCode;
  private LocalDateTime passwordDate;

  public static MemberDetailDto of(MemberEntity memberEntity) {

    return MemberDetailDto.builder()
        .memberId(memberEntity.getMemberId())
        .email(memberEntity.getEmail())
        .password(memberEntity.getPassword())
        .name(memberEntity.getName())
        .nickname(memberEntity.getNickname())
        .regDate(memberEntity.getRegDate())
        .modDate(memberEntity.getModDate())
        .auth(memberEntity.getAuth())
        .authCode(memberEntity.getAuthCode())
        .imageUrl(memberEntity.getImageUrl())
        .introduction(memberEntity.getIntroduction())
        .status(memberEntity.getStatus())
        .adminYn(memberEntity.getAdminYn())
        .passwordAuthCode(memberEntity.getPasswordAuthCode())
        .passwordDate(memberEntity.getPasswordDate())
        .build();
  }
}
