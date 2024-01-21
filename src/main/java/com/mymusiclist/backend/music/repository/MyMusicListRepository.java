package com.mymusiclist.backend.music.repository;

import com.mymusiclist.backend.member.domain.MemberEntity;
import com.mymusiclist.backend.music.domain.MyMusicListEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MyMusicListRepository extends JpaRepository<MyMusicListEntity, Long> {

  Optional<MyMusicListEntity> findByMemberIdAndListName(MemberEntity member, String listName);

  List<MyMusicListEntity> findByMemberId(MemberEntity member);
}
