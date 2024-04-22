package com.mymusiclist.backend.post.service.impl;

import com.mymusiclist.backend.exception.impl.DeleteCommentException;
import com.mymusiclist.backend.exception.impl.DeletePostException;
import com.mymusiclist.backend.exception.impl.InvalidTokenException;
import com.mymusiclist.backend.exception.impl.NotFoundCommentException;
import com.mymusiclist.backend.exception.impl.NotFoundMemberException;
import com.mymusiclist.backend.exception.impl.NotFoundPostException;
import com.mymusiclist.backend.member.domain.MemberEntity;
import com.mymusiclist.backend.member.jwt.JwtTokenProvider;
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
import com.mymusiclist.backend.post.service.CommentService;
import com.mymusiclist.backend.type.CommentStatus;
import com.mymusiclist.backend.type.PostStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.xml.stream.events.Comment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

  private final CommentRepository commentRepository;
  private final MemberRepository memberRepository;
  private final PostRepository postRepository;
  private final CommentLikeRepository commentLikeRepository;
  private final JwtTokenProvider jwtTokenProvider;

  @Override
  public List<CommentDto> getList(PostEntity postEntity) {

    List<CommentEntity> commentList = commentRepository.findByPostIdAndStatusOrderByCreateDateDesc(
        postEntity, CommentStatus.ACTIVE);

    List<Boolean> commentYnList = new ArrayList<>();

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
      String email = authentication.getName();
      MemberEntity member = memberRepository.findByEmail(email)
          .orElseThrow(() -> new NotFoundMemberException());

      for (CommentEntity comment : commentList) {
        Optional<CommentLikeEntity> byCommentIdAndMemberId = commentLikeRepository.findByCommentIdAndMemberId(
            comment, member);
        if (byCommentIdAndMemberId.isPresent()) {
          commentYnList.add(true);
        } else {
          commentYnList.add(false);
        }
      }

      return CommentDto.listOf(commentList, commentYnList);
    }

    for (int i = 0; i < commentList.size(); i++) {
      commentYnList.add(false);
    }
    return CommentDto.listOf(commentList, commentYnList);
  }

  @Override
  @Transactional
  public String create(String accessToken, Long postId, CommentRequest commentRequest) {

    if (!jwtTokenProvider.validateToken(accessToken)) {
      throw new InvalidTokenException();
    }

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();

    MemberEntity member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new InvalidTokenException());

    PostEntity post = postRepository.findByPostId(postId)
        .orElseThrow(() -> new NotFoundPostException());
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

    log.info("comment write user: {}, postId: {}, comment: {}", email, postId,
        commentRequest.getComment());
    return "댓글 작성완료.";
  }

  @Override
  @Transactional
  public String delete(String accessToken, Long postId, Long commentId) {

    if (!jwtTokenProvider.validateToken(accessToken)) {
      throw new InvalidTokenException();
    }

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    CommentEntity comment = new CommentEntity();
    PostEntity post = new PostEntity();

    String email = null;

    if (authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .anyMatch(role -> role.equals("ROLE_ADMIN"))) {
      // admin 권한이 있을 때
      comment = commentRepository.findByCommentId(commentId)
          .orElseThrow(() -> new NotFoundCommentException());

      post = postRepository.findByPostId(comment.getPostId().getPostId())
          .orElseThrow(() -> new NotFoundPostException());

      email = authentication.getName();
    } else {
      // admin 권한이 없을 때
      email = authentication.getName();
      MemberEntity member = memberRepository.findByEmail(email)
          .orElseThrow(() -> new InvalidTokenException());

      post = postRepository.findByPostId(postId)
          .orElseThrow(() -> new NotFoundPostException());
      if (post.getStatus().equals(PostStatus.DELETED)) {
        throw new DeletePostException();
      }

      comment = commentRepository.findByMemberIdAndPostIdAndCommentId(
          member, post, commentId).orElseThrow(() -> new NotFoundCommentException());
      if (comment.getStatus().equals(CommentStatus.DELETED)) {
        throw new DeleteCommentException();
      }
    }

    CommentEntity commentEntity = comment.toBuilder()
        .status(CommentStatus.DELETED)
        .deleteDate(LocalDateTime.now())
        .build();
    commentRepository.save(commentEntity);

    // 댓글 삭제 시 해당 댓글의 추천(좋아요)도 같이 삭제
    List<CommentLikeEntity> deleteLikes = commentLikeRepository.findByCommentId(comment);
    commentLikeRepository.deleteAll(deleteLikes);

    PostEntity postEntity = post.toBuilder()
        .commentCnt(post.getCommentCnt() - 1)
        .build();
    postRepository.save(postEntity);

    if (authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .anyMatch(role -> role.equals("ROLE_ADMIN"))) {
      log.info("comment delete admin: {}, postId: {}, commentId: {}", email, postId, commentId);
    } else {
      log.info("comment delete user: {}, postId: {}, commentId: {}", email, postId, commentId);
    }

    return "댓글 삭제완료";
  }

  @Override
  @Transactional
  public String update(String accessToken, Long postId, Long commentId, CommentRequest commentRequest) {

    if (!jwtTokenProvider.validateToken(accessToken)) {
      throw new InvalidTokenException();
    }

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    CommentEntity comment = new CommentEntity();

    String email = null;

    if (authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .anyMatch(role -> role.equals("ROLE_ADMIN"))) {
      // admin 권한이 있을 때
      comment = commentRepository.findByCommentId(commentId)
          .orElseThrow(() -> new NotFoundCommentException());

      email = authentication.getName();
    } else {
      // admin 권한이 없을 때
      email = authentication.getName();
      MemberEntity member = memberRepository.findByEmail(email)
          .orElseThrow(() -> new InvalidTokenException());

      PostEntity post = postRepository.findByPostId(postId)
          .orElseThrow(() -> new NotFoundPostException());
      if (post.getStatus().equals(PostStatus.DELETED)) {
        throw new DeletePostException();
      }

      comment = commentRepository.findByMemberIdAndPostIdAndCommentId(
          member, post, commentId).orElseThrow(() -> new NotFoundCommentException());
      if (comment.getStatus().equals(CommentStatus.DELETED)) {
        throw new DeleteCommentException();
      }
    }

    CommentEntity commentEntity = comment.toBuilder()
        .comment(commentRequest.getComment())
        .modDate(LocalDateTime.now())
        .build();
    commentRepository.save(commentEntity);

    if (authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .anyMatch(role -> role.equals("ROLE_ADMIN"))) {
      log.info("comment update admin: {}, postId: {}, commentId: {}, update comment: {}", email,
          postId, commentId, commentRequest.getComment());
    } else {
      log.info("comment update user: {}, postId: {}, commentId: {}, update comment: {}", email,
          postId, commentId, commentRequest.getComment());
    }

    return "댓글 수정완료";
  }

  @Override
  @Transactional
  public void like(String accessToken, Long postId, Long commentId) {

    if (!jwtTokenProvider.validateToken(accessToken)) {
      throw new InvalidTokenException();
    }

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();

    MemberEntity member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new InvalidTokenException());

    PostEntity post = postRepository.findByPostId(postId)
        .orElseThrow(() -> new NotFoundPostException());
    if (post.getStatus().equals(PostStatus.DELETED)) {
      throw new DeletePostException();
    }

    CommentEntity comment = commentRepository.findByPostIdAndCommentId(post,
        commentId).orElseThrow(() -> new NotFoundCommentException());
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

      log.info("post like user: {}, postId: {}, commentId{}", email, postId, commentId);
    } else {
      CommentEntity commentEntity = comment.toBuilder()
          .likeCnt(comment.getLikeCnt() - 1)
          .build();
      commentRepository.save(commentEntity);

      CommentLikeEntity commentLikeEntity = byCommentIdAndMemberId.get();
      commentLikeRepository.delete(commentLikeEntity);

      log.info("post like cancel user: {}, postId: {}, commentId{}", email, postId, commentId);
    }
  }

  @Override
  public List<MyCommentDto> getMyComment(String accessToken) {

    if (!jwtTokenProvider.validateToken(accessToken)) {
      throw new InvalidTokenException();
    }

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();

    MemberEntity member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new InvalidTokenException());

    List<CommentEntity> commentList = commentRepository.findAllByMemberIdAndStatus(member,
        CommentStatus.ACTIVE);

    return MyCommentDto.listOf(commentList);
  }
}
