package com.mymusiclist.backend.post.service;

import com.mymusiclist.backend.exception.impl.DeletePostException;
import com.mymusiclist.backend.exception.impl.InvalidAuthException;
import com.mymusiclist.backend.exception.impl.NotFoundMemberException;
import com.mymusiclist.backend.exception.impl.NotFoundMusicListException;
import com.mymusiclist.backend.exception.impl.NotFoundPostException;
import com.mymusiclist.backend.member.domain.MemberEntity;
import com.mymusiclist.backend.member.repository.MemberRepository;
import com.mymusiclist.backend.music.domain.MusicEntity;
import com.mymusiclist.backend.music.domain.MyMusicListEntity;
import com.mymusiclist.backend.music.dto.PlayListDto;
import com.mymusiclist.backend.music.repository.MusicRepository;
import com.mymusiclist.backend.music.repository.MyMusicListRepository;
import com.mymusiclist.backend.post.domain.CommentEntity;
import com.mymusiclist.backend.post.domain.PostEntity;
import com.mymusiclist.backend.post.domain.PostLikeEntity;
import com.mymusiclist.backend.post.dto.CommentDto;
import com.mymusiclist.backend.post.dto.PostDetailDto;
import com.mymusiclist.backend.post.dto.PostListDto;
import com.mymusiclist.backend.post.dto.request.PostRequest;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

  @Override
  @Transactional
  public String create(PostRequest postRequest) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();
    Optional<MemberEntity> byEmail = memberRepository.findByEmail(email);
    if (byEmail.isEmpty()) {
      throw new NotFoundMemberException();
    }
    MemberEntity member = byEmail.get();

    Optional<MyMusicListEntity> byMemberIdAndListName = myMusicListRepository.findByMemberIdAndListName(
        member, postRequest.getListName());
    if (byMemberIdAndListName.isEmpty()) {
      throw new NotFoundMusicListException();
    }
    MyMusicListEntity myMusicList = byMemberIdAndListName.get();

    PostEntity postEntity = PostEntity.builder()
        .title(postRequest.getTitle())
        .memberId(member)
        .nickname(member.getNickname())
        .content(postRequest.getContent())
        .listId(myMusicList)
        .likeCnt(0)
        .commentCnt(0)
        .status(PostStatus.ACTIVE.getDescription())
        .createDate(LocalDateTime.now())
        .build();
    postRepository.save(postEntity);

    return "게시글이 작성되었습니다.";
  }

  @Override
  @Transactional
  public String update(Long postId, PostRequest postRequest) {

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

    // 게시글 수정을 요청한 사용자 본인이 작성한 게시글인지 확인
    Optional<PostEntity> byPostIdAndMemberId = postRepository.findByPostIdAndMemberId(postId, member);
    if (byPostIdAndMemberId.isEmpty()) {
      throw new InvalidAuthException();
    }
    PostEntity post = byPostIdAndMemberId.get();

    // 삭제되어있는 게시글일 경우
    if (post.getStatus().equals(PostStatus.DELETED.getDescription())) {
      throw new DeletePostException();
    }

    Optional<MyMusicListEntity> byMemberIdAndListName = myMusicListRepository.findByMemberIdAndListName(
        member, postRequest.getListName());
    if (byMemberIdAndListName.isEmpty()) {
      throw new NotFoundMusicListException();
    }
    MyMusicListEntity myMusicList = byMemberIdAndListName.get();

    PostEntity postEntity = post.toBuilder()
        .title(postRequest.getTitle())
        .content(postRequest.getContent())
        .listId(myMusicList)
        .modDate(LocalDateTime.now())
        .build();
    postRepository.save(postEntity);

    return "게시글을 수정했습니다.";
  }

  @Override
  @Transactional
  public String delete(Long postId) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();
    Optional<MemberEntity> byEmail = memberRepository.findByEmail(email);
    if (byEmail.isEmpty()) {
      throw new NotFoundMemberException();
    }
    MemberEntity member = byEmail.get();

    Optional<PostEntity> byPostIdAndMemberId = postRepository.findByPostIdAndMemberId(postId, member);
    if (byPostIdAndMemberId.isEmpty()) {
      throw new NotFoundPostException();
    }
    PostEntity post = byPostIdAndMemberId.get();

    // 이미 삭제되어있는 게시글일 경우
    if (post.getStatus().equals(PostStatus.DELETED.getDescription())) {
      throw new DeletePostException();
    }

    PostEntity deletePost = post.toBuilder()
        .status(PostStatus.DELETED.getDescription())
        .build();
    postRepository.save(deletePost);

    // 게시글이 삭제 처리되었으면 해당 게시글의 댓글들도 모두 삭제 처리해줘야한다.
    List<CommentEntity> commentEntityList = commentRepository.findByPostId(post);
    List<CommentEntity> deleteComments = commentEntityList.stream()
        .map(commentEntity -> {
          CommentEntity updatedComment = commentEntity.toBuilder()
              .status(CommentStatus.DELETED.getDescription())
              .build();
          return updatedComment;
        })
        .collect(Collectors.toList());
    commentRepository.saveAll(deleteComments);

    return "해당 게시글이 삭제되었습니다.";
  }

  @Override
  public List<PostListDto> getList(Boolean sortByLikes) {
    List<PostEntity> posts;

    if (sortByLikes) {
      posts = postRepository.findByStatusOrderByLikeCntDesc(PostStatus.ACTIVE.getDescription());
    } else {
      posts = postRepository.findByStatusOrderByCreateDateDesc(PostStatus.ACTIVE.getDescription());
    }

    List<PostListDto> postList = posts.stream()
        .map(post -> {
          PostListDto postListDto = new PostListDto();
          postListDto.setPostId(post.getPostId());
          postListDto.setTitle(post.getTitle());
          postListDto.setNickName(post.getNickname());
          postListDto.setCreateDate(post.getCreateDate());
          postListDto.setLikeCnt(post.getLikeCnt());
          postListDto.setCommentCnt(post.getCommentCnt());
          return postListDto;
        })
        .collect(Collectors.toList());

    return postList;
  }

  @Override
  public PostDetailDto getDetail(Long postId) {

    Optional<PostEntity> byPostId = postRepository.findByPostId(postId);
    if (byPostId.isEmpty()) {
      throw new NotFoundPostException();
    }
    PostEntity post = byPostId.get();
    if (post.getStatus().equals(PostStatus.DELETED.getDescription())) {
      throw new DeletePostException();
    }

    List<MusicEntity> musicList = musicRepository.findByListId(post.getListId());
    List<CommentDto> commentList = commentService.getList(post);

    PostDetailDto postDetailDto = PostDetailDto.builder()
        .title(post.getTitle())
        .nickname(post.getNickname())
        .createDate(post.getCreateDate())
        .likeCnt(post.getLikeCnt())
        .commentCnt(post.getCommentCnt())
        .listName(post.getListId().getListName())
        .musicList(PlayListDto.listOf(musicList))
        .comment(commentList)
        .build();

    return postDetailDto;
  }

  @Override
  public List<PostListDto> getMyPost() {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();
    Optional<MemberEntity> byEmail = memberRepository.findByEmail(email);
    if (byEmail.isEmpty()) {
      throw new NotFoundMemberException();
    }
    MemberEntity member = byEmail.get();

    List<PostEntity> posts = postRepository.findByMemberIdAndStatus(member,
        PostStatus.ACTIVE.getDescription());
    List<PostListDto> postList = posts.stream()
        .map(post -> {
          PostListDto postListDto = new PostListDto();
          postListDto.setTitle(post.getTitle());
          postListDto.setNickName(post.getNickname());
          postListDto.setCreateDate(post.getCreateDate());
          postListDto.setLikeCnt(post.getLikeCnt());
          postListDto.setCommentCnt(post.getCommentCnt());
          return postListDto;
        })
        .collect(Collectors.toList());

    return postList;
  }

  @Override
  @Transactional
  public void like(Long postId) {

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
    } else {
      PostEntity postEntity = post.toBuilder()
          .likeCnt(post.getLikeCnt() - 1)
          .build();
      postRepository.save(postEntity);

      PostLikeEntity postLikeEntity = byPostIdAndMemberId.get();
      postLikeRepository.delete(postLikeEntity);
    }
  }

  @Override
  public List<PostListDto> search(String keyword, String searchOption) {

    List<PostEntity> posts = new ArrayList<>();

    // 검색옵션에 따라 검색되는 게시물이 달라지게 설정
    if (searchOption.equals(SearchOption.TITLE.getValue())) {
      // 제목에서 키워드를 포함하는 게시물 검색
      posts = postRepository.findByTitleContainingAndStatus(keyword,
          PostStatus.ACTIVE.getDescription());
    } else if (searchOption.equals(SearchOption.CONTENT.getValue())) {
      // 내용에서 키워드를 포함하는 게시물 검색
      posts = postRepository.findByContentContainingAndStatus(keyword,
          PostStatus.ACTIVE.getDescription());
    } else if (searchOption.equals(SearchOption.TITLE_AND_CONTENT.getValue())) {
      // 제목이나 내용에서 키워드를 포함하는 게시물 검색
      posts = postRepository.findByTitleContainingOrContentContainingAndStatus(keyword, keyword,
          PostStatus.ACTIVE.getDescription());
    } else if (searchOption.equals(SearchOption.NICKNAME.getValue())) {
      // 닉네임으로 게시물 검색, 닉네임 검색은 닉네임이 정확해야 검색 가능.
      posts = postRepository.findByNicknameAndStatus(keyword, PostStatus.ACTIVE.getDescription());
    }

    List<PostListDto> postList = posts.stream()
        .map(post -> {
          PostListDto postListDto = new PostListDto();
          postListDto.setTitle(post.getTitle());
          postListDto.setNickName(post.getNickname());
          postListDto.setCreateDate(post.getCreateDate());
          postListDto.setLikeCnt(post.getLikeCnt());
          postListDto.setCommentCnt(post.getCommentCnt());
          return postListDto;
        })
        .collect(Collectors.toList());

    return postList;
  }

}
