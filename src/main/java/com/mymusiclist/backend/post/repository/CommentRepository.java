package com.mymusiclist.backend.post.repository;

import com.mymusiclist.backend.member.domain.MemberEntity;
import com.mymusiclist.backend.post.domain.CommentEntity;
import com.mymusiclist.backend.post.domain.PostEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

  Integer countByPostId(PostEntity post);

  List<CommentEntity> findAllByMemberId(MemberEntity member);

  List<CommentEntity> findByPostIdAndStatus(PostEntity postEntity, String status);

  List<CommentEntity> findByPostId(PostEntity post);
}
