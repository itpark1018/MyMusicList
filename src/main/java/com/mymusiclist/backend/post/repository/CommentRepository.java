package com.mymusiclist.backend.post.repository;

import com.mymusiclist.backend.member.domain.MemberEntity;
import com.mymusiclist.backend.post.domain.CommentEntity;
import com.mymusiclist.backend.post.domain.PostEntity;
import com.mymusiclist.backend.type.CommentStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

  List<CommentEntity> findAllByMemberId(MemberEntity member);

  List<CommentEntity> findByPostId(PostEntity post);

  List<CommentEntity> findByPostIdAndStatusOrderByCreateDateDesc(PostEntity postEntity,
      CommentStatus status);

  List<CommentEntity> findAllByMemberIdAndStatus(MemberEntity member, CommentStatus description);

  Optional<CommentEntity> findByMemberIdAndPostIdAndCommentIdAndStatus(MemberEntity member,
      PostEntity post, Long commentId, CommentStatus description);

  Optional<CommentEntity> findByPostIdAndCommentId(PostEntity post, Long commentId);

  Optional<CommentEntity> findByMemberIdAndPostIdAndCommentId(MemberEntity member, PostEntity post, Long commentId);
}
