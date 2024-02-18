package com.mymusiclist.backend.admin.service;

import com.mymusiclist.backend.admin.dto.AdminCommentListDto;
import com.mymusiclist.backend.admin.dto.AdminPostListDto;
import com.mymusiclist.backend.admin.dto.MemberDetailDto;
import com.mymusiclist.backend.admin.dto.request.CommentUpdateRequest;
import com.mymusiclist.backend.admin.dto.request.MemberUpdateRequest;
import com.mymusiclist.backend.admin.dto.request.PostUpdateRequest;
import com.mymusiclist.backend.exception.impl.DuplicateNicknameException;
import com.mymusiclist.backend.exception.impl.InvalidSearchOptionException;
import com.mymusiclist.backend.exception.impl.NotFoundMemberException;
import com.mymusiclist.backend.member.domain.MemberEntity;
import com.mymusiclist.backend.member.repository.MemberRepository;
import com.mymusiclist.backend.post.domain.CommentEntity;
import com.mymusiclist.backend.post.domain.PostEntity;
import com.mymusiclist.backend.post.repository.CommentRepository;
import com.mymusiclist.backend.post.repository.PostRepository;
import com.mymusiclist.backend.post.service.CommentService;
import com.mymusiclist.backend.post.service.PostService;
import com.mymusiclist.backend.type.MemberStatus;
import com.mymusiclist.backend.type.SearchOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

  private final MemberRepository memberRepository;
  private final PostRepository postRepository;
  private final CommentRepository commentRepository;
  private final PostService postService;
  private final CommentService commentService;

  @Override
  @Transactional
  public String setMemberStatus(Long memberId, MemberStatus memberStatus) {

    MemberEntity member = memberRepository.findByMemberId(memberId)
        .orElseThrow(() -> new NotFoundMemberException());

    // 회원의 계정을 정지시킬때와 활성화 시킬때에 대한 구분이 필요
    if (memberStatus.equals(MemberStatus.SUSPENDED)) {
      MemberEntity memberEntity = member.toBuilder()
          .nickname("정지된 회원 " + member.getNickname())
          .status(memberStatus)
          .build();
      memberRepository.save(memberEntity);

      // 정지된 회원이 작성한 게시글, 댓글의 닉네임 변경
      updateNicknameInPostsAndComments(memberEntity, "정지된 회원");
    } else if (memberStatus.equals(MemberStatus.ACTIVE)) {
      MemberEntity memberEntity = member.toBuilder()
          .nickname(randomNickname())
          .status(memberStatus)
          .build();
      memberRepository.save(memberEntity);

      // 활성화 된 회원이 작성한 게시글, 댓글의 닉네임 변경
      updateNicknameInPostsAndComments(memberEntity, memberEntity.getNickname());
    }

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();

    log.info("memberStatus update admin: {}, memberId: {}, memberStatus: {}", email, memberId,
        memberStatus);

    return "memberId: " + member.getMemberId() + " 회원의 상태가 " + memberStatus + "로 변경되었습니다.";
  }

  @Override
  public MemberDetailDto getMemberInfo(Long memberId) {

    MemberEntity member = memberRepository.findByMemberId(memberId)
        .orElseThrow(() -> new NotFoundMemberException());

    return MemberDetailDto.of(member);
  }

  @Override
  @Transactional
  public String memberUpdate(Long memberId, MemberUpdateRequest memberUpdateRequest) {

    MemberEntity member = memberRepository.findByMemberId(memberId)
        .orElseThrow(() -> new NotFoundMemberException());

    if (!memberUpdateRequest.getNickname().isEmpty()) {
      memberRepository.findByNicknameAndStatus(memberUpdateRequest.getNickname(),
          MemberStatus.ACTIVE).ifPresent(item -> {
        throw new DuplicateNicknameException();
      });
    }

    MemberEntity memberEntity = member.toBuilder()
        .nickname(memberUpdateRequest.getNickname())
        .imageUrl(memberUpdateRequest.getImageUrl())
        .introduction(memberUpdateRequest.getIntroduction())
        .modDate(LocalDateTime.now())
        .build();
    memberRepository.save(memberEntity);

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();

    log.info(
        "memberUpdate admin: {}, memberId: {}, update content - nickname: {}, imageUrl: {}, introduction: {}",
        email, memberId, memberUpdateRequest.getNickname(),
        memberUpdateRequest.getImageUrl(), memberUpdateRequest.getImageUrl(),
        memberUpdateRequest.getIntroduction());

    return "memberId: " + memberId + " 회원 정보 수정완료.";
  }

  @Override
  public List<MemberDetailDto> searchMember(String keyword, String searchOption) {

    if (searchOption.equals(SearchOption.NAME.getValue())) {
      List<MemberEntity> byName = memberRepository.findByName(keyword);
      if (byName.isEmpty()) {
        throw new NotFoundMemberException();
      }

      return MemberDetailDto.listOf(byName);
    } else if (searchOption.equals(SearchOption.NICKNAME.getValue())) {
      MemberEntity member = memberRepository.findByNickname(keyword)
          .orElseThrow(() -> new NotFoundMemberException());

      List<MemberDetailDto> list = new ArrayList<>();
      list.add(MemberDetailDto.of(member));
      return list;
    }

    throw new InvalidSearchOptionException();
  }

  @Override
  public String postDelete(Long postId) {

    return postService.delete(postId);
  }

  @Override
  public String postUpdate(PostUpdateRequest postUpdateRequest) {

    return postService.update(postUpdateRequest.getPostId(),
        PostUpdateRequest.postRequest(postUpdateRequest));
  }

  @Override
  public String commentDelete(Long commentId) {

    return commentService.delete(null, commentId);
  }

  @Override
  public String commentUpdate(CommentUpdateRequest commentUpdateRequest) {

    return commentService.update(null, commentUpdateRequest.getCommentId(),
        CommentUpdateRequest.commentRequest(commentUpdateRequest));
  }

  @Override
  public List<AdminPostListDto> searchPost(String keyword, String searchOption) {

    List<PostEntity> posts = new ArrayList<>();

    // 검색옵션에 따라 검색되는 게시물이 달라지게 설정
    if (searchOption.equals(SearchOption.TITLE.getValue())) {
      // 제목에서 키워드를 포함하는 게시물 검색
      posts = postRepository.findByTitleContaining(keyword);
    } else if (searchOption.equals(SearchOption.CONTENT.getValue())) {
      // 내용에서 키워드를 포함하는 게시물 검색
      posts = postRepository.findByContentContaining(keyword);
    } else if (searchOption.equals(SearchOption.TITLE_AND_CONTENT.getValue())) {
      // 제목이나 내용에서 키워드를 포함하는 게시물 검색
      posts = postRepository.findByTitleContainingOrContentContaining(keyword, keyword);
    } else if (searchOption.equals(SearchOption.NICKNAME.getValue())) {
      // 닉네임으로 게시물 검색, 닉네임 검색은 닉네임이 정확해야 검색 가능.
      posts = postRepository.findByNickname(keyword);
    }

    return AdminPostListDto.listOf(posts);
  }

  @Override
  public List<AdminCommentListDto> searchComment(String keyword, String searchOption) {

    List<CommentEntity> comments = new ArrayList<>();

    // 검색옵션에 따라 검색되는 게시물이 달라지게 설정
    if (searchOption.equals(SearchOption.COMMENT.getValue())) {
      // 댓글 내용에서 키워드를 포함하는 댓글 검색
      comments = commentRepository.findByCommentContaining(keyword);
    } else if (searchOption.equals(SearchOption.NICKNAME.getValue())) {
      // 닉네임으로 게시물 검색, 닉네임 검색은 닉네임이 정확해야 검색 가능.
      comments = commentRepository.findByNickname(keyword);
    }

    return AdminCommentListDto.listOf(comments);
  }

  private String randomNickname() {
    Random random = new Random();
    int randomNumber = random.nextInt(1000); // 0부터 999까지의 랜덤한 숫자

    String newNickname = "임시" + randomNumber;
    Optional<MemberEntity> byNickname = memberRepository.findByNickname(newNickname);
    if (byNickname.isPresent()) {
      newNickname = randomNickname();
    }

    return newNickname;
  }

  @Transactional
  public void updateNicknameInPostsAndComments(MemberEntity member, String newNickname) {
    // 사용자가 작성한 게시글의 닉네임 변경
    List<PostEntity> userPosts = postRepository.findAllByMemberId(member);
    List<PostEntity> updatedPosts = userPosts.stream()
        .map(post -> post.toBuilder().nickname(newNickname).build())
        .collect(Collectors.toList());
    postRepository.saveAll(updatedPosts);

    // 사용자가 작성한 댓글의 닉네임 변경
    List<CommentEntity> userComments = commentRepository.findAllByMemberId(member);
    List<CommentEntity> updatedComments = userComments.stream()
        .map(comment -> comment.toBuilder().nickname(newNickname).build())
        .collect(Collectors.toList());
    commentRepository.saveAll(updatedComments);
  }

}
