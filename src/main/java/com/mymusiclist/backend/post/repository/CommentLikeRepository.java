package com.mymusiclist.backend.post.repository;

import com.mymusiclist.backend.member.domain.MemberEntity;
import com.mymusiclist.backend.post.domain.CommentEntity;
import com.mymusiclist.backend.post.domain.CommentLikeEntity;
import com.mymusiclist.backend.post.domain.PostEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLikeEntity, Long> {

  Optional<CommentLikeEntity> findByCommentIdAndMemberId(CommentEntity comment, MemberEntity member);

  List<CommentLikeEntity> findByPostId(PostEntity post);

  List<CommentLikeEntity> findByCommentId(CommentEntity comment);
}
