package com.mymusiclist.backend.member.dto.request;

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
public class SignUpRequest {

  private String email;
  private String password;
  private String checkPassword;
  private String name;
  private String nickname;
  private String imageUrl;
  private String introduction;
}
