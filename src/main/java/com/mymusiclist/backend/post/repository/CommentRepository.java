package com.mymusiclist.backend.post.repository;

import com.mymusiclist.backend.member.domain.MemberEntity;
import com.mymusiclist.backend.post.domain.CommentEntity;
import com.mymusiclist.backend.post.domain.PostEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

  List<CommentEntity> findAllByMemberId(MemberEntity member);

  List<CommentEntity> findByPostId(PostEntity post);

  List<CommentEntity> findByPostIdAndStatusOrderByCreateDateDesc(PostEntity postEntity,
      String status);

  List<CommentEntity> findAllByMemberIdAndStatus(MemberEntity member, String description);

  Optional<CommentEntity> findByMemberIdAndPostIdAndCommentIdAndStatus(MemberEntity member,
      PostEntity post, Long commentId, String description);

  Optional<CommentEntity> findByPostIdAndCommentId(PostEntity post, Long commentId);
}
