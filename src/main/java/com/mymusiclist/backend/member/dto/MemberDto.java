package com.mymusiclist.backend.member.dto;

import com.mymusiclist.backend.member.domain.MemberEntity;
import com.mymusiclist.backend.member.dto.request.SignUpRequest;
import com.mymusiclist.backend.type.MemberStatus;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCrypt;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDto {

  private String email;
  private String name;
  private String nickname;
  private LocalDateTime regDate;
  private String imageUrl;
  private String introduction;

  public static MemberDto of(MemberEntity memberEntity) {

    return MemberDto.builder()
        .email(memberEntity.getEmail())
        .name(memberEntity.getName())
        .nickname(memberEntity.getNickname())
        .regDate(memberEntity.getRegDate())
        .imageUrl(memberEntity.getImageUrl())
        .introduction(memberEntity.getIntroduction())
        .build();
  }
}
