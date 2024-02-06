package com.mymusiclist.backend.post.service;

import com.mymusiclist.backend.exception.impl.DeleteCommentException;
import com.mymusiclist.backend.exception.impl.DeletePostException;
import com.mymusiclist.backend.exception.impl.NotFoundCommentException;
import com.mymusiclist.backend.exception.impl.NotFoundMemberException;
import com.mymusiclist.backend.exception.impl.NotFoundPostException;
import com.mymusiclist.backend.member.domain.MemberEntity;
import com.mymusiclist.backend.member.repository.MemberRepository;
import com.mymusiclist.backend.post.domain.CommentEntity;
import com.mymusiclist.backend.post.domain.CommentLikeEntity;
import com.mymusiclist.backend.post.domain.PostEntity;
import com.mymusiclist.backend.post.dto.CommentDto;
import com.mymusiclist.backend.post.dto.MyCommentDto;
import com.mymusiclist.backend.post.dto.request.CommentRequest;
import com.mymusiclist.backend.post.repository.CommentLikeRepository;
import com.mymusiclist.backend.post.repository.CommentRepository;
import com.mymusiclist.backend.post.repository.PostRepository;
import com.mymusiclist.backend.type.CommentStatus;
import com.mymusiclist.backend.type.PostStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

  private final CommentRepository commentRepository;
  private final MemberRepository memberRepository;
  private final PostRepository postRepository;
  private final CommentLikeRepository commentLikeRepository;

  @Override
  public List<CommentDto> getList(PostEntity postEntity) {

    List<CommentEntity> commentList = commentRepository.findByPostIdAndStatusOrderByCreateDateDesc(
        postEntity, CommentStatus.ACTIVE);

    return CommentDto.listOf(commentList);
  }

  @Override
  @Transactional
  public String create(Long postId, CommentRequest commentRequest) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();
    Optional<MemberEntity> byEmail = memberRepository.findByEmail(email);
    if (byEmail.isEmpty()) {
      throw new NotFoundMemberException();
    }
    MemberEntity member = byEmail.get();

    Optional<PostEntity> byPostId = postRepository.findByPostId(postId);
    if (byPostId.isEmpty()) {
      throw new NotFoundPostException();
    }
    PostEntity post = byPostId.get();

    if (post.getStatus().equals(PostStatus.DELETED)) {
      throw new DeletePostException();
    }

    CommentEntity commentEntity = CommentEntity.builder()
        .postId(post)
        .memberId(member)
        .nickname(member.getNickname())
        .comment(commentRequest.getComment())
        .likeCnt(0)
        .status(CommentStatus.ACTIVE)
        .createDate(LocalDateTime.now())
        .build();
    commentRepository.save(commentEntity);

    PostEntity postEntity = post.toBuilder()
        .commentCnt(post.getCommentCnt() + 1)
        .build();
    postRepository.save(postEntity);

    return "댓글 작성완료.";
  }

  @Override
  @Transactional
  public String delete(Long postId, Long commentId) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();
    Optional<MemberEntity> byEmail = memberRepository.findByEmail(email);
    if (byEmail.isEmpty()) {
      throw new NotFoundMemberException();
    }
    MemberEntity member = byEmail.get();

    Optional<PostEntity> byPostIdAndStatus = postRepository.findByPostId(postId);
    if (byPostIdAndStatus.isEmpty()) {
      throw new NotFoundPostException();
    }
    PostEntity post = byPostIdAndStatus.get();

    if (post.getStatus().equals(PostStatus.DELETED)) {
      throw new DeletePostException();
    }

    Optional<CommentEntity> byComment = commentRepository.findByMemberIdAndPostIdAndCommentId(
        member, post, commentId);
    if (byComment.isEmpty()) {
      throw new NotFoundCommentException();
    }
    CommentEntity comment = byComment.get();

    if (comment.getStatus().equals(CommentStatus.DELETED)) {
      throw new DeleteCommentException();
    }

    CommentEntity commentEntity = comment.toBuilder()
        .status(CommentStatus.DELETED)
        .build();
    commentRepository.save(commentEntity);

    // 댓글 삭제 시 해당 댓글의 추천(좋아요)도 같이 삭제
    List<CommentLikeEntity> deleteLikes = commentLikeRepository.findByCommentId(comment);
    commentLikeRepository.deleteAll(deleteLikes);

    PostEntity postEntity = post.toBuilder()
        .commentCnt(post.getCommentCnt() - 1)
        .build();
    postRepository.save(postEntity);

    return "댓글 삭제완료";
  }

  @Override
  @Transactional
  public String update(Long postId, Long commentId, CommentRequest commentRequest) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();
    Optional<MemberEntity> byEmail = memberRepository.findByEmail(email);
    if (byEmail.isEmpty()) {
      throw new NotFoundMemberException();
    }
    MemberEntity member = byEmail.get();

    Optional<PostEntity> byPostI = postRepository.findByPostId(postId);
    if (byPostI.isEmpty()) {
      throw new NotFoundPostException();
    }
    PostEntity post = byPostI.get();

    if (post.getStatus().equals(PostStatus.DELETED)) {
      throw new DeletePostException();
    }

    Optional<CommentEntity> byComment = commentRepository.findByMemberIdAndPostIdAndCommentId(
        member, post, commentId);
    if (byComment.isEmpty()) {
      throw new NotFoundCommentException();
    }
    CommentEntity comment = byComment.get();

    if (comment.getStatus().equals(CommentStatus.DELETED)) {
      throw new DeleteCommentException();
    }

    CommentEntity commentEntity = comment.toBuilder()
        .comment(commentRequest.getComment())
        .modDate(LocalDateTime.now())
        .build();
    commentRepository.save(commentEntity);

    return "댓글 수정완료";
  }

  @Override
  @Transactional
  public void like(Long postId, Long commentId) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();
    Optional<MemberEntity> byEmail = memberRepository.findByEmail(email);
    if (byEmail.isEmpty()) {
      throw new NotFoundMemberException();
    }
    MemberEntity member = byEmail.get();

    Optional<PostEntity> byPostIdAndStatus = postRepository.findByPostId(postId);
    if (byPostIdAndStatus.isEmpty()) {
      throw new NotFoundPostException();
    }
    PostEntity post = byPostIdAndStatus.get();

    if (post.getStatus().equals(PostStatus.DELETED)) {
      throw new DeletePostException();
    }

    Optional<CommentEntity> byPostIdAndCommentId = commentRepository.findByPostIdAndCommentId(post,
        commentId);
    if (byPostIdAndCommentId.isEmpty()) {
      throw new NotFoundCommentException();
    }
    CommentEntity comment = byPostIdAndCommentId.get();

    if (comment.getStatus().equals(CommentStatus.DELETED)) {
      throw new DeleteCommentException();
    }

    // 댓글 추천 요청이 왔을 때 추천한 적이 없으면 해당 댓들에 추천개수가 +1이 되고 DB에 저장
    // 댓글 추천 요청이 왔을 때 이미 추천한 적이 있으면 해당 댓들에 추천개수가 -1이 되고 DB에 저장
    Optional<CommentLikeEntity> byCommentIdAndMemberId = commentLikeRepository.findByCommentIdAndMemberId(
        comment, member);
    if (byCommentIdAndMemberId.isEmpty()) {
      CommentEntity commentEntity = comment.toBuilder()
          .likeCnt(comment.getLikeCnt() + 1)
          .build();
      commentRepository.save(commentEntity);

      CommentLikeEntity commentLikeEntity = CommentLikeEntity.builder()
          .postId(post)
          .commentId(comment)
          .memberId(member)
          .build();
      commentLikeRepository.save(commentLikeEntity);
    } else {
      CommentEntity commentEntity = comment.toBuilder()
          .likeCnt(comment.getLikeCnt() - 1)
          .build();
      commentRepository.save(commentEntity);

      CommentLikeEntity commentLikeEntity = byCommentIdAndMemberId.get();
      commentLikeRepository.delete(commentLikeEntity);
    }
  }

  @Override
  public List<MyCommentDto> getMyComment() {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();
    Optional<MemberEntity> byEmail = memberRepository.findByEmail(email);
    if (byEmail.isEmpty()) {
      throw new NotFoundMemberException();
    }
    MemberEntity member = byEmail.get();

    List<CommentEntity> commentList = commentRepository.findAllByMemberIdAndStatus(member,
        CommentStatus.ACTIVE);

    return MyCommentDto.listOf(commentList);
  }
}
