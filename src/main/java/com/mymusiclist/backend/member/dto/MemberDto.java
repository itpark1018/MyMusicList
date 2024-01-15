package com.mymusiclist.backend.member.dto;

import com.mymusiclist.backend.member.domain.MemberEntity;
import com.mymusiclist.backend.member.dto.parameter.SignUpParameter;
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
  private String status;
  private Boolean adminYn;
  private String passwordAuthCode;
  private LocalDateTime passwordDate;

  public static MemberEntity signUpInput(SignUpParameter signUpParameter) {
    String uuid = UUID.randomUUID().toString();
    String encPassword = BCrypt.hashpw(signUpParameter.getPassword(), BCrypt.gensalt());

    return MemberEntity.builder()
        .email(signUpParameter.getEmail())
        .password(encPassword)
        .name(signUpParameter.getName())
        .nickname(signUpParameter.getNickname())
        .regDate(LocalDateTime.now())
        .auth(false).authCode(uuid)
        .imageUrl(signUpParameter.getImageUrl())
        .introduction(signUpParameter.getIntroduction())
        .status(MemberStatus.WAITING_FOR_APPROVAL.getDescription())
        .build();
  }
}
