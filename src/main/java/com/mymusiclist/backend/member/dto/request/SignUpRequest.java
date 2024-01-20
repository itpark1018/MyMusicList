package com.mymusiclist.backend.member.dto.request;

import com.mymusiclist.backend.member.domain.MemberEntity;
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
public class SignUpRequest {

  private String email;
  private String password;
  private String checkPassword;
  private String name;
  private String nickname;
  private String imageUrl;
  private String introduction;

  public static MemberEntity signUpInput(SignUpRequest signUpRequest) {
    String uuid = UUID.randomUUID().toString();
    String encPassword = BCrypt.hashpw(signUpRequest.getPassword(), BCrypt.gensalt());

    return MemberEntity.builder()
        .email(signUpRequest.getEmail())
        .password(encPassword)
        .name(signUpRequest.getName())
        .nickname(signUpRequest.getNickname())
        .regDate(LocalDateTime.now())
        .auth(false).authCode(uuid)
        .imageUrl(signUpRequest.getImageUrl())
        .introduction(signUpRequest.getIntroduction())
        .status(MemberStatus.WAITING_FOR_APPROVAL.getDescription())
        .adminYn(false)
        .build();
  }
}
