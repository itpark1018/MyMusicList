package com.mymusiclist.backend.music.service;

import com.google.api.services.youtube.model.SearchResult;
import com.mymusiclist.backend.exception.impl.DuplicateListException;
import com.mymusiclist.backend.exception.impl.InvalidTokenException;
import com.mymusiclist.backend.exception.impl.ListEmptyException;
import com.mymusiclist.backend.exception.impl.MemberIdMismatchException;
import com.mymusiclist.backend.exception.impl.NotFoundMusicException;
import com.mymusiclist.backend.exception.impl.NotFoundMusicListException;
import com.mymusiclist.backend.member.domain.MemberEntity;
import com.mymusiclist.backend.member.repository.MemberRepository;
import com.mymusiclist.backend.music.domain.MusicEntity;
import com.mymusiclist.backend.music.domain.MyMusicListEntity;
import com.mymusiclist.backend.music.dto.MyMusicListDto;
import com.mymusiclist.backend.music.dto.PlayListDto;
import com.mymusiclist.backend.music.dto.YoutubeSearchDto;
import com.mymusiclist.backend.music.dto.request.AddRequest;
import com.mymusiclist.backend.music.dto.request.DeleteRequest;
import com.mymusiclist.backend.music.dto.request.UpdateRequest;
import com.mymusiclist.backend.music.repository.MusicRepository;
import com.mymusiclist.backend.music.repository.MyMusicListRepository;
import com.mymusiclist.backend.music.youtube.YoutubeClient;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MusicServiceImpl implements MusicService {

  private final MemberRepository memberRepository;
  private final MyMusicListRepository myMusicListRepository;
  private final MusicRepository musicRepository;
  private final YoutubeClient youtubeClient;

  @Override
  public List<YoutubeSearchDto> search(String keyword) {

    try {
      List<SearchResult> searchResults = youtubeClient.youtubeSearch(keyword);

      if (searchResults != null && !searchResults.isEmpty()) {
        List<YoutubeSearchDto> searchDtoList = new ArrayList<>();

        for (SearchResult searchResult : searchResults) {
          YoutubeSearchDto searchDto = YoutubeSearchDto.builder()
              .title(searchResult.getSnippet().getTitle())
              .videoUrl("https://www.youtube.com/watch?v=" + searchResult.getId().getVideoId())
              .build();
          searchDtoList.add(searchDto);
        }

        return searchDtoList;
      }

    } catch (IOException e) {
      e.printStackTrace();
    }

    return null;
  }

  @Override
  @Transactional
  public String createList(String listName) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();

    MemberEntity member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new InvalidTokenException());

    myMusicListRepository.findByMemberIdAndListName(member, listName)
        .ifPresentOrElse(value -> {
          throw new DuplicateListException();
        }, () -> {
          MyMusicListEntity myMusicListEntity = MyMusicListEntity.builder()
              .listName(listName)
              .memberId(member)
              .numberOfMusic(0L)
              .repeatPlay(false)
              .randomPlay(false)
              .regDate(LocalDateTime.now())
              .build();
          myMusicListRepository.save(myMusicListEntity);
        });

    log.info("musicList create user: {}, listName: {}", email, listName);
    return "리스트 생성 완료.";
  }

  @Override
  @Transactional
  public String deleteList(String listName) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();

    MemberEntity member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new InvalidTokenException());

    MyMusicListEntity myMusicList = myMusicListRepository.findByMemberIdAndListName(member,
        listName).orElseThrow(() -> new NotFoundMusicListException());

    List<MusicEntity> byListId = musicRepository.findByListId(myMusicList);
    if (!byListId.isEmpty()) {
      for (MusicEntity music : byListId) {
        String musicName = music.getMusicName();
        Optional<MusicEntity> byMusicNamesByListId = musicRepository.findByMusicNameAndListId(
            musicName, myMusicList);
        if (byMusicNamesByListId.isEmpty()) {
          throw new NotFoundMusicException();
        }
        MusicEntity musicEntity = byMusicNamesByListId.get();
        musicRepository.delete(musicEntity);
      }
    }
    myMusicListRepository.delete(myMusicList);

    log.info("musicList delete user: {}, listName: {}", email, listName);
    return "리스트를 삭제했습니다.";
  }

  @Override
  @Transactional
  public MyMusicListDto updateList(String listName, UpdateRequest updateRequest) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();

    MemberEntity member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new InvalidTokenException());

    MyMusicListEntity myMusicList = myMusicListRepository.findByMemberIdAndListName(member,
        listName).orElseThrow(() -> new NotFoundMusicListException());

    MyMusicListEntity myMusicListEntity = new MyMusicListEntity();
    if (updateRequest.getMusicName().isEmpty()) {
      myMusicListEntity = myMusicList.toBuilder()
          .listName(updateRequest.getListName())
          .build();
      myMusicListRepository.save(myMusicListEntity);
    } else {
      for (String musicName : updateRequest.getMusicName()) {
        Optional<MusicEntity> byMusicName = musicRepository.findByMusicName(musicName);
        if (byMusicName.isEmpty()) {
          throw new NotFoundMusicException();
        }
        MusicEntity music = byMusicName.get();
        musicRepository.delete(music);

        myMusicListEntity = myMusicList.toBuilder()
            .listName(updateRequest.getListName())
            .numberOfMusic(myMusicList.getNumberOfMusic() - 1)
            .build();
        myMusicListRepository.save(myMusicListEntity);
      }
    }

    log.info("musicList update user: {}, update content - listName: {}, musicName: {}", email,
        updateRequest.getListName(), updateRequest.getMusicName());
    return MyMusicListDto.of(myMusicListEntity, musicRepository);
  }

  @Override
  public List<String> getMyList() {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();

    MemberEntity member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new InvalidTokenException());

    List<MyMusicListEntity> byMemberId = myMusicListRepository.findByMemberId(member);
    if (byMemberId.isEmpty()) {
      throw new NotFoundMusicListException();
    }

    List<String> listNames = new ArrayList<>();
    for (MyMusicListEntity myMusicList : byMemberId) {
      listNames.add(myMusicList.getListName());
    }

    return listNames;
  }

  @Override
  public MyMusicListDto detail(String listName) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();

    MemberEntity member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new InvalidTokenException());

    MyMusicListEntity myMusicList = myMusicListRepository.findByMemberIdAndListName(member,
        listName).orElseThrow(() -> new NotFoundMusicListException());

    return MyMusicListDto.of(myMusicList, musicRepository);
  }

  @Override
  @Transactional
  public String addMusic(String listName, AddRequest addRequest) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();

    MemberEntity member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new InvalidTokenException());

    MyMusicListEntity myMusicList = myMusicListRepository.findByMemberIdAndListName(member,
        listName).orElseThrow(() -> new NotFoundMusicListException());

    // List의 회원정보와 일치하지 않을 때
    if (!myMusicList.getMemberId().getMemberId().equals(member.getMemberId())) {
      throw new MemberIdMismatchException();
    }

    MusicEntity musicEntity = MusicEntity.builder()
        .listId(myMusicList)
        .musicName(addRequest.getMusicName())
        .musicUrl(addRequest.getMusicUrl())
        .build();
    musicRepository.save(musicEntity);
    ;

    // 해당하는 뮤직 리스트의 노래 개수를 증가
    MyMusicListEntity myMusicListEntity = myMusicList.toBuilder()
        .numberOfMusic(myMusicList.getNumberOfMusic() + 1)
        .build();
    myMusicListRepository.save(myMusicListEntity);

    return "해당 리스트에 노래를 추가했습니다.";
  }

  @Override
  public String deleteMusic(String listName, DeleteRequest deleteRequest) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();

    MemberEntity member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new InvalidTokenException());

    MyMusicListEntity myMusicList = myMusicListRepository.findByMemberIdAndListName(member,
        listName).orElseThrow(() -> new NotFoundMusicListException());

    // List의 회원정보와 일치하지 않을 때
    if (!myMusicList.getMemberId().getMemberId().equals(member.getMemberId())) {
      throw new MemberIdMismatchException();
    }

    for (Long id : deleteRequest.getMusicId()) {
      MusicEntity music = musicRepository.findByListIdAndMusicId(myMusicList, id)
          .orElseThrow(() -> new NotFoundMusicException());

      musicRepository.delete(music);

      MyMusicListEntity myMusicListEntity = myMusicList.toBuilder()
          .numberOfMusic(myMusicList.getNumberOfMusic() - 1)
          .build();
      myMusicListRepository.save(myMusicListEntity);
    }

    return "해당 리스트에 노래를 제거했습니다.";
  }

  @Override
  @Transactional
  public List<PlayListDto> playList(String listName, Boolean repeatPlay, Boolean randomPlay) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();

    MemberEntity member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new InvalidTokenException());

    MyMusicListEntity myMusicList = myMusicListRepository.findByMemberIdAndListName(member,
        listName).orElseThrow(() -> new NotFoundMusicListException());

    // 뮤직 리스트의 기존 재생옵션과 다를 때 재생옵션을 업데이트
    myMusicList.updatePlayOptions(repeatPlay, randomPlay);
    myMusicListRepository.save(myMusicList);

    List<MusicEntity> musicList = musicRepository.findByListId(myMusicList);
    if (musicList.isEmpty()) {
      throw new ListEmptyException();
    }

    // 랜덤재생 옵션이 활성화되어 있으면 뮤직 리스트를 섞음
    if (randomPlay) {
      shufflePlaylist(musicList);
    }

    return PlayListDto.listOf(musicList);
  }

  // 뮤직 리스트를 섞는 메서드
  private void shufflePlaylist(List<MusicEntity> musicList) {
    Random random = new Random();
    for (int i = musicList.size() - 1; i > 0; i--) {
      int index = random.nextInt(i + 1);
      MusicEntity temp = musicList.get(index);
      musicList.set(index, musicList.get(i));
      musicList.set(i, temp);
    }
  }
}
