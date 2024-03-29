package com.mymusiclist.backend.member.repository;

import com.mymusiclist.backend.member.domain.MemberEntity;
import com.mymusiclist.backend.type.MemberStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Long> {

  Optional<MemberEntity> findByEmail(String email);

  Optional<MemberEntity> findByNickname(String nickname);

  Optional<MemberEntity> findByMemberId(Long memberId);

  List<MemberEntity> findByName(String name);

  Optional<MemberEntity> findByNicknameAndStatus(String nickname, MemberStatus status);
}
