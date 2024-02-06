package com.mymusiclist.backend.post.repository;

import com.mymusiclist.backend.member.domain.MemberEntity;
import com.mymusiclist.backend.post.domain.PostEntity;
import com.mymusiclist.backend.post.domain.PostLikeEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLikeEntity, Long> {

  Optional<PostLikeEntity> findByPostIdAndMemberId(PostEntity post, MemberEntity member);
}
