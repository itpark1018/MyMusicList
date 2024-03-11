package com.mymusiclist.backend.post.service;

import com.mymusiclist.backend.exception.impl.DeletePostException;
import com.mymusiclist.backend.exception.impl.InvalidAuthException;
import com.mymusiclist.backend.exception.impl.InvalidSearchOptionException;
import com.mymusiclist.backend.exception.impl.InvalidTokenException;
import com.mymusiclist.backend.exception.impl.NotFoundMemberException;
import com.mymusiclist.backend.exception.impl.NotFoundMusicListException;
import com.mymusiclist.backend.exception.impl.NotFoundPostException;
import com.mymusiclist.backend.member.domain.MemberEntity;
import com.mymusiclist.backend.member.jwt.JwtTokenProvider;
import com.mymusiclist.backend.member.repository.MemberRepository;
import com.mymusiclist.backend.music.domain.MusicEntity;
import com.mymusiclist.backend.music.domain.MyMusicListEntity;
import com.mymusiclist.backend.music.dto.PlayListDto;
import com.mymusiclist.backend.music.repository.MusicRepository;
import com.mymusiclist.backend.music.repository.MyMusicListRepository;
import com.mymusiclist.backend.post.domain.CommentEntity;
import com.mymusiclist.backend.post.domain.CommentLikeEntity;
import com.mymusiclist.backend.post.domain.PostEntity;
import com.mymusiclist.backend.post.domain.PostLikeEntity;
import com.mymusiclist.backend.post.dto.CommentDto;
import com.mymusiclist.backend.post.dto.PostDetailDto;
import com.mymusiclist.backend.post.dto.PostListDto;
import com.mymusiclist.backend.post.dto.request.PostRequest;
import com.mymusiclist.backend.post.repository.CommentLikeRepository;
import com.mymusiclist.backend.post.repository.CommentRepository;
import com.mymusiclist.backend.post.repository.PostLikeRepository;
import com.mymusiclist.backend.post.repository.PostRepository;
import com.mymusiclist.backend.type.CommentStatus;
import com.mymusiclist.backend.type.PostStatus;
import com.mymusiclist.backend.type.SearchOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
public class PostServiceImpl implements PostService {

  private final MemberRepository memberRepository;
  private final MyMusicListRepository myMusicListRepository;
  private final PostRepository postRepository;
  private final CommentRepository commentRepository;
  private final MusicRepository musicRepository;
  private final CommentService commentService;
  private final PostLikeRepository postLikeRepository;
  private final CommentLikeRepository commentLikeRepository;
  private final JwtTokenProvider jwtTokenProvider;

  @Override
  @Transactional
  public String create(String accessToken, PostRequest postRequest) {

    if (!jwtTokenProvider.validateToken(accessToken)) {
      throw new InvalidTokenException();
    }

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();

    MemberEntity member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new NotFoundMemberException());

    MyMusicListEntity myMusicList = null;
    if (postRequest.getListName() != null && !postRequest.getListName().isEmpty()) {
      myMusicList = myMusicListRepository.findByMemberIdAndListName(member,
          postRequest.getListName()).orElseThrow(() -> new NotFoundMusicListException());
    }

    PostEntity postEntity = PostEntity.builder()
        .title(postRequest.getTitle())
        .memberId(member)
        .nickname(member.getNickname())
        .content(postRequest.getContent())
        .listId(myMusicList)
        .likeCnt(0)
        .commentCnt(0)
        .status(PostStatus.ACTIVE)
        .createDate(LocalDateTime.now())
        .build();
    postRepository.save(postEntity);

    log.info(
        "post write admin: {}, post content - title: {}, content: {}, listName: {}",
        email, postRequest.getTitle(), postRequest.getContent(), postRequest.getListName());

    return "게시글이 작성되었습니다.";
  }

  @Override
  @Transactional
  public String update(String accessToken, Long postId, PostRequest postRequest) {

    if (!jwtTokenProvider.validateToken(accessToken)) {
      throw new InvalidTokenException();
    }

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    MemberEntity member = new MemberEntity();
    PostEntity post = new PostEntity();
    MyMusicListEntity myMusicList = null;

    String email = null;

    if (authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .anyMatch(role -> role.equals("ROLE_ADMIN"))) {
      // admin 권한이 있을 때
      post = postRepository.findByPostId(postId).orElseThrow(() -> new NotFoundPostException());

      // musicList 확인
      Long memberId = post.getMemberId().getMemberId();

      member = memberRepository.findByMemberId(memberId)
          .orElseThrow(() -> new NotFoundMemberException());

      if (postRequest.getListName() != null && !postRequest.getListName().isEmpty()) {
        myMusicList = myMusicListRepository.findByMemberIdAndListName(member,
            postRequest.getListName()).orElseThrow(() -> new NotFoundMusicListException());
      }
      email = authentication.getName();
    } else {
      // admin 권한이 없을 때
      email = authentication.getName();
      member = memberRepository.findByEmail(email).orElseThrow(() -> new InvalidTokenException());

      // 게시글이 존재하는지 확인
      PostEntity postEntity = postRepository.findByPostId(postId)
          .orElseThrow(() -> new NotFoundPostException());

      // 삭제되어 있는 게시글일 경우
      if (postEntity.getStatus().equals(PostStatus.DELETED)) {
        throw new DeletePostException();
      }

      // 게시글 수정을 요청한 사용자 본인이 작성한 게시글인지 확인
      post = postRepository.findByPostIdAndMemberId(postId, member)
          .orElseThrow(() -> new InvalidAuthException());

      // musicList가 해당 회원에게 있는지 확인
      if (postRequest.getListName() != null && !postRequest.getListName().isEmpty()) {
        myMusicList = myMusicListRepository.findByMemberIdAndListName(member,
            postRequest.getListName()).orElseThrow(() -> new NotFoundMusicListException());
      }
    }

    PostEntity postEntity = post.toBuilder()
        .title(postRequest.getTitle())
        .content(postRequest.getContent())
        .listId(myMusicList)
        .modDate(LocalDateTime.now())
        .build();
    postRepository.save(postEntity);

    if (authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .anyMatch(role -> role.equals("ROLE_ADMIN"))) {
      log.info(
          "post update admin: {}, postId: {}, post content - title: {}, content: {}, listName: {}",
          email, postId, postRequest.getTitle(), postRequest.getContent(),
          postRequest.getListName());
    } else {
      log.info(
          "post update user: {}, postId: {}, post content - title: {}, content: {}, listName: {}",
          email, postId, postRequest.getTitle(), postRequest.getContent(),
          postRequest.getListName());
    }

    return "게시글을 수정했습니다.";
  }

  @Override
  @Transactional
  public String delete(String accessToken, Long postId) {

    if (!jwtTokenProvider.validateToken(accessToken)) {
      throw new InvalidTokenException();
    }

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    MemberEntity member = new MemberEntity();
    PostEntity post = new PostEntity();

    String email = null;

    if (authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .anyMatch(role -> role.equals("ROLE_ADMIN"))) {
      // admin 권한이 있을 때
      email = authentication.getName();
      post = postRepository.findByPostId(postId).orElseThrow(() -> new NotFoundPostException());
    } else {
      // admin 권한이 없을 때
      email = authentication.getName();
      member = memberRepository.findByEmail(email).orElseThrow(() -> new InvalidTokenException());

      post = postRepository.findByPostIdAndMemberId(postId, member)
          .orElseThrow(() -> new NotFoundPostException());
    }

    // 이미 삭제되어있는 게시글일 경우
    if (post.getStatus().equals(PostStatus.DELETED)) {
      throw new DeletePostException();
    }

    PostEntity deletePost = post.toBuilder()
        .status(PostStatus.DELETED)
        .deleteDate(LocalDateTime.now())
        .build();
    postRepository.save(deletePost);

    // 게시글이 삭제 처리되었으면 해당 게시글의 댓글들도 모두 삭제 처리해줘야한다.
    List<CommentEntity> commentEntityList = commentRepository.findByPostId(post);
    List<CommentEntity> deleteComments = commentEntityList.stream()
        .map(commentEntity -> {
          CommentEntity updatedComment = commentEntity.toBuilder()
              .status(CommentStatus.DELETED)
              .deleteDate(LocalDateTime.now())
              .build();
          return updatedComment;
        })
        .collect(Collectors.toList());
    commentRepository.saveAll(deleteComments);

    // 게시글이 처리되었으면 해당 게시글의 추천(좋아요)도 모두 삭제 처리해줘야한다.
    List<PostLikeEntity> deletePostLikes = postLikeRepository.findByPostId(post);
    postLikeRepository.deleteAll(deletePostLikes);

    // 게시글이 처리되었으면 해당 게시글 모든 댓글의 추천(좋아요)도 모두 삭제 처리해줘야한다.
    List<CommentLikeEntity> deleteCommentLikes = commentLikeRepository.findByPostId(post);
    commentLikeRepository.deleteAll(deleteCommentLikes);

    if (authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .anyMatch(role -> role.equals("ROLE_ADMIN"))) {
      log.info("post delete admin: {}, postId: {}", email, postId);
    } else {
      log.info("post delete user: {}, postId: {}", email, postId);
    }

    return "해당 게시글이 삭제되었습니다.";
  }

  @Override
  public List<PostListDto> getList(Boolean sortByLikes) {

    List<PostEntity> posts;

    if (sortByLikes) {
      posts = postRepository.findByStatusOrderByLikeCntDesc(PostStatus.ACTIVE);
    } else {
      posts = postRepository.findByStatusOrderByCreateDateDesc(PostStatus.ACTIVE);
    }

    return PostListDto.listOf(posts);
  }

  @Override
  public PostDetailDto getDetail(Long postId) {

    PostEntity post = postRepository.findByPostId(postId)
        .orElseThrow(() -> new NotFoundPostException());

    if (post.getStatus().equals(PostStatus.DELETED)) {
      throw new DeletePostException();
    }

    List<MusicEntity> musicList = musicRepository.findByListId(post.getListId());
    List<CommentDto> commentList = commentService.getList(post);

    Boolean likeYn = null;

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
      String email = authentication.getName();
      MemberEntity member = memberRepository.findByEmail(email)
          .orElseThrow(() -> new NotFoundMemberException());

      Optional<PostLikeEntity> byPostIdAndMemberId = postLikeRepository.findByPostIdAndMemberId(
          post, member);
      if (byPostIdAndMemberId.isPresent()) {
        likeYn = true;
      }
    }

    PostDetailDto postDetailDto = PostDetailDto.builder()
        .postId(post.getPostId())
        .title(post.getTitle())
        .nickname(post.getNickname())
        .createDate(post.getCreateDate())
        .likeCnt(post.getLikeCnt())
        .commentCnt(post.getCommentCnt())
        .listName(post.getListId().getListName())
        .musicList(PlayListDto.listOf(musicList))
        .comment(commentList)
        .likeYn(likeYn)
        .build();

    return postDetailDto;
  }

  @Override
  public List<PostListDto> getMyPost(String accessToken) {

    if (!jwtTokenProvider.validateToken(accessToken)) {
      throw new InvalidTokenException();
    }

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();

    MemberEntity member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new InvalidTokenException());

    List<PostEntity> posts = postRepository.findByMemberIdAndStatus(member,
        PostStatus.ACTIVE);

    return PostListDto.listOf(posts);
  }

  @Override
  @Transactional
  public void like(String accessToken, Long postId) {

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

    // 게시글 추천 요청이 왔을 때 추천한 적이 없으면 게시글의 추천개수가 1개 늘어나고 게시글 추천 DB에 데이터가 저장
    // 반대로 이미 추천한 게시글일 경우 추천이 취소되면서 게시글의 추천개수가 1개 줄어들고 게시글 추천 DB에서 데이터가 삭제
    Optional<PostLikeEntity> byPostIdAndMemberId = postLikeRepository.findByPostIdAndMemberId(post,
        member);
    if (byPostIdAndMemberId.isEmpty()) {
      PostEntity postEntity = post.toBuilder()
          .likeCnt(post.getLikeCnt() + 1)
          .build();
      postRepository.save(postEntity);

      PostLikeEntity postLikeEntity = PostLikeEntity.builder()
          .postId(post)
          .memberId(member)
          .build();
      postLikeRepository.save(postLikeEntity);

      log.info("post like user: {}, postId: {}", email, postId);
    } else {
      PostEntity postEntity = post.toBuilder()
          .likeCnt(post.getLikeCnt() - 1)
          .build();
      postRepository.save(postEntity);

      PostLikeEntity postLikeEntity = byPostIdAndMemberId.get();
      postLikeRepository.delete(postLikeEntity);

      log.info("post like cancel user: {}, postId: {}", email, postId);
    }
  }

  @Override
  public List<PostListDto> search(String accessToken, String keyword, SearchOption searchOption) {

    if (!jwtTokenProvider.validateToken(accessToken)) {
      throw new InvalidTokenException();
    }

    List<PostEntity> posts = new ArrayList<>();

    // 검색옵션에 따라 검색되는 게시물이 달라지게 설정
    if (searchOption.equals(SearchOption.TITLE)) {
      // 제목에서 키워드를 포함하는 게시물 검색
      posts = postRepository.findByTitleContainingAndStatus(keyword,
          PostStatus.ACTIVE);
    } else if (searchOption.equals(SearchOption.CONTENT)) {
      // 내용에서 키워드를 포함하는 게시물 검색
      posts = postRepository.findByContentContainingAndStatus(keyword,
          PostStatus.ACTIVE);
    } else if (searchOption.equals(SearchOption.TITLE_AND_CONTENT)) {
      // 제목이나 내용에서 키워드를 포함하는 게시물 검색
      posts = postRepository.findByTitleContainingOrContentContainingAndStatus(keyword, keyword,
          PostStatus.ACTIVE);
    } else if (searchOption.equals(SearchOption.NICKNAME)) {
      // 닉네임으로 게시물 검색, 닉네임 검색은 닉네임이 정확해야 검색 가능.
      posts = postRepository.findByNicknameAndStatus(keyword, PostStatus.ACTIVE);
    } else {
      // 검색옵션이 잘못되었을 때
      throw new InvalidSearchOptionException();
    }

    return PostListDto.listOf(posts);
  }
}
