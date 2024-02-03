package com.mymusiclist.backend.member.dto;

import com.mymusiclist.backend.member.domain.MemberEntity;
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
public class MemberInfoDto {

  private String nickname;
  private String imageUrl;
  private String introduction;

  public static MemberInfoDto of(MemberEntity memberEntity) {

    return MemberInfoDto.builder()
        .nickname(memberEntity.getNickname())
        .imageUrl(memberEntity.getImageUrl())
        .introduction(memberEntity.getIntroduction())
        .build();
  }
}
