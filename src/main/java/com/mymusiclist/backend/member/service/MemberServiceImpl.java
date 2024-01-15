package com.mymusiclist.backend.member.service;

import com.mymusiclist.backend.components.MailComponents;
import com.mymusiclist.backend.exception.impl.DuplicateEmailException;
import com.mymusiclist.backend.exception.impl.InvalidAuthCodeException;
import com.mymusiclist.backend.exception.impl.InvalidEmailException;
import com.mymusiclist.backend.exception.impl.InvalidPasswordConfirmationException;
import com.mymusiclist.backend.exception.impl.NotFoundMemberException;
import com.mymusiclist.backend.member.domain.MemberEntity;
import com.mymusiclist.backend.member.dto.MemberDto;
import com.mymusiclist.backend.member.dto.parameter.SignUpParameter;
import com.mymusiclist.backend.member.repository.MemberRepository;
import com.mymusiclist.backend.type.MemberStatus;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

  private final MemberRepository memberRepository;
  private final MailComponents mailComponents;

  @Override
  @Transactional
  public String signUp(SignUpParameter signUpParameter) {

    Optional<MemberEntity> byEmail = memberRepository.findByEmail(signUpParameter.getEmail());
    if (byEmail.isPresent()) {
      throw new DuplicateEmailException();
    }

    if (!signUpParameter.getPassword().equals(signUpParameter.getCheckPassword())) {
      throw new InvalidPasswordConfirmationException();
    }

    if (!isValidEmail(signUpParameter.getEmail())) {
      throw new InvalidEmailException();
    }

    MemberEntity memberEntity = MemberDto.signUpInput(signUpParameter);
    memberRepository.save(memberEntity);

    String email = signUpParameter.getEmail();
    String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
    String title = "MyMusicList 회원인증";
    String message = "<h3>MyMusicList 회원가입에 성공했습니다. 아래의 링크를 클릭하셔서 회원인증을 완료해주세요.</h3>" +
        "<div><a href='" + baseUrl + "/member/auth?email=" + email + "&code="
        + memberEntity.getAuthCode() + "'> 인증 링크 </a></div>";
    mailComponents.sendMail(email, title, message);

    return "가입한 이메일을 확인해 회원인증을 진행해주세요.";
  }

  @Override
  @Transactional
  public String auth(String email, String code) {

    Optional<MemberEntity> byEmail = memberRepository.findByEmail(email);
    if (byEmail.isEmpty()) {
      throw new NotFoundMemberException();
    }
    MemberEntity member = byEmail.get();

    if (!code.equals(member.getAuthCode())) {
      throw new InvalidAuthCodeException();
    }

    MemberEntity memberEntity = MemberEntity.builder()
        .memberId(member.getMemberId())
        .email(member.getEmail())
        .password(member.getPassword())
        .name(member.getName())
        .nickname(member.getNickname())
        .regDate(member.getRegDate())
        .auth(true)
        .authCode(member.getAuthCode())
        .imageUrl(member.getImageUrl())
        .introduction(member.getIntroduction())
        .status(MemberStatus.ACTIVE.getDescription())
        .build();
    memberRepository.save(memberEntity);

    return "인증을 완료 했습니다.";
  }

  private boolean isValidEmail(String email) {
    return EmailValidator.getInstance().isValid(email);
  }
}
