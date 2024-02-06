package com.mymusiclist.backend.post.repository;

import com.mymusiclist.backend.member.domain.MemberEntity;
import com.mymusiclist.backend.post.domain.PostEntity;
import com.mymusiclist.backend.type.PostStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {

  List<PostEntity> findAllByMemberId(MemberEntity member);

  List<PostEntity> findByStatusOrderByCreateDateDesc(PostStatus status);

  List<PostEntity> findByStatusOrderByLikeCntDesc(PostStatus status);

  Optional<PostEntity> findByPostId(Long postId);

  Optional<PostEntity> findByPostIdAndMemberId(Long postId, MemberEntity member);

  List<PostEntity> findByMemberIdAndStatus(MemberEntity member, PostStatus status);

  List<PostEntity> findByTitleContainingAndStatus(String title, PostStatus status);

  List<PostEntity> findByContentContainingAndStatus(String content, PostStatus status);

  List<PostEntity> findByTitleContainingOrContentContainingAndStatus(String title, String content, PostStatus status);

  List<PostEntity> findByNicknameAndStatus(String nickname, PostStatus status);

  Optional<PostEntity> findByPostIdAndStatus(Long postId, PostStatus description);
}
