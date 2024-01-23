package com.mymusiclist.backend.post.repository;

import com.mymusiclist.backend.member.domain.MemberEntity;
import com.mymusiclist.backend.post.domain.PostEntity;
import com.mymusiclist.backend.post.dto.PostListDto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {

  List<PostEntity> findAllByMemberId(MemberEntity member);

  List<PostEntity> findByStatusOrderByCreateDateDesc(String status);

  List<PostEntity> findByStatusOrderByLikeCntDesc(String status);

  Optional<PostEntity> findByPostId(Long postId);

  Optional<PostEntity> findByPostIdAndMemberId(Long postId, MemberEntity member);

  List<PostEntity> findByMemberIdAndStatus(MemberEntity member, String status);

  List<PostEntity> findByTitleContainingAndStatus(String title, String status);

  List<PostEntity> findByContentContainingAndStatus(String content, String status);

  List<PostEntity> findByTitleContainingOrContentContainingAndStatus(String title, String content, String status);

  List<PostEntity> findByNicknameAndStatus(String nickname, String status);
}
