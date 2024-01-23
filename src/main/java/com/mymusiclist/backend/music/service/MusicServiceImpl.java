package com.mymusiclist.backend.music.service;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.mymusiclist.backend.exception.impl.DuplicateListException;
import com.mymusiclist.backend.exception.impl.ListEmptyException;
import com.mymusiclist.backend.exception.impl.MemberIdMismatchException;
import com.mymusiclist.backend.exception.impl.NotFoundMemberException;
import com.mymusiclist.backend.exception.impl.NotFoundMusicException;
import com.mymusiclist.backend.exception.impl.NotFoundMusicListException;
import com.mymusiclist.backend.member.domain.MemberEntity;
import com.mymusiclist.backend.member.repository.MemberRepository;
import com.mymusiclist.backend.music.domain.MusicEntity;
import com.mymusiclist.backend.music.domain.MyMusicListEntity;
import com.mymusiclist.backend.music.dto.MyMusicListDto;
import com.mymusiclist.backend.music.dto.PlayListDto;
import com.mymusiclist.backend.music.dto.request.AddRequest;
import com.mymusiclist.backend.music.dto.request.UpdateRequest;
import com.mymusiclist.backend.music.repository.MusicRepository;
import com.mymusiclist.backend.music.repository.MyMusicListRepository;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MusicServiceImpl implements MusicService {

  private final MemberRepository memberRepository;
  private final MyMusicListRepository myMusicListRepository;
  private final MusicRepository musicRepository;

  @Value("${youtube.apiKey}")
  private String apiKey;

  @Override
  public List<Map<String, String>> search(String keyword) {

    try {
      YouTube youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), null)
          .setApplicationName("MyMusicList")
          .build();

      // YouTube API 검색 요청
      YouTube.Search.List search = youtube.search().list(Collections.singletonList("id,snippet"));
      search.setKey(apiKey);
      search.setQ(keyword);
      search.setType(Collections.singletonList("video"));
      search.setMaxResults(5L); // 가져올 결과의 최대 수

      // API 응답에서 비디오 ID 및 제목 추출
      SearchListResponse searchResponse = search.execute();
      List<SearchResult> searchResults = searchResponse.getItems();

      if (searchResults != null && !searchResults.isEmpty()) {
        List<Map<String, String>> resultsList = new ArrayList<>();

        for (SearchResult searchResult : searchResults) {
          Map<String, String> result = new HashMap<>();
          result.put("title", searchResult.getSnippet().getTitle());
          result.put("videoUrl",
              "https://www.youtube.com/watch?v=" + searchResult.getId().getVideoId());
          resultsList.add(result);
        }

        return resultsList;
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
    Optional<MemberEntity> byEmail = memberRepository.findByEmail(email);
    if (byEmail.isEmpty()) {
      throw new NotFoundMemberException();
    }
    MemberEntity member = byEmail.get();

    Optional<MyMusicListEntity> byListName = myMusicListRepository.findByMemberIdAndListName(member,
        listName);
    if (byListName.isPresent()) {
      throw new DuplicateListException();
    }

    MyMusicListEntity myMusicListEntity = MyMusicListEntity.builder()
        .listName(listName)
        .memberId(member)
        .numberOfMusic(0L)
        .repeatPlay(false)
        .randomPlay(false)
        .regDate(LocalDateTime.now())
        .build();
    myMusicListRepository.save(myMusicListEntity);

    return "리스트 생성 완료.";
  }

  @Override
  @Transactional
  public String deleteList(String listName) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();
    Optional<MemberEntity> byEmail = memberRepository.findByEmail(email);
    if (byEmail.isEmpty()) {
      throw new NotFoundMemberException();
    }
    MemberEntity member = byEmail.get();

    Optional<MyMusicListEntity> byMemberIdAndListName = myMusicListRepository.findByMemberIdAndListName(
        member, listName);
    if (byMemberIdAndListName.isEmpty()) {
      throw new NotFoundMusicListException();
    }
    MyMusicListEntity myMusicList = byMemberIdAndListName.get();

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

    return "리스트를 삭제했습니다.";
  }

  @Override
  @Transactional
  public MyMusicListDto updateList(String listName, UpdateRequest updateRequest) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();
    Optional<MemberEntity> byEmail = memberRepository.findByEmail(email);
    if (byEmail.isEmpty()) {
      throw new NotFoundMemberException();
    }
    MemberEntity member = byEmail.get();

    Optional<MyMusicListEntity> byMemberId = myMusicListRepository.findByMemberIdAndListName(member,
        listName);
    if (byMemberId.isEmpty()) {
      throw new NotFoundMusicListException();
    }
    MyMusicListEntity myMusicList = byMemberId.get();

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

    return MyMusicListDto.of(myMusicListEntity, musicRepository);
  }

  @Override
  public List<String> getMyList() {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();
    Optional<MemberEntity> byEmail = memberRepository.findByEmail(email);
    if (byEmail.isEmpty()) {
      throw new NotFoundMemberException();
    }
    MemberEntity member = byEmail.get();

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
    Optional<MemberEntity> byEmail = memberRepository.findByEmail(email);
    if (byEmail.isEmpty()) {
      throw new NotFoundMemberException();
    }
    MemberEntity member = byEmail.get();

    Optional<MyMusicListEntity> byMemberIdAndListName = myMusicListRepository.findByMemberIdAndListName(
        member, listName);
    if (byMemberIdAndListName.isEmpty()) {
      throw new NotFoundMusicListException();
    }
    MyMusicListEntity myMusicList = byMemberIdAndListName.get();

    return MyMusicListDto.of(myMusicList, musicRepository);
  }

  @Override
  @Transactional
  public String addMusic(String listName, AddRequest addRequest) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();
    Optional<MemberEntity> byEmail = memberRepository.findByEmail(email);
    if (byEmail.isEmpty()) {
      throw new NotFoundMemberException();
    }
    MemberEntity member = byEmail.get();

    Optional<MyMusicListEntity> byMemberIdAndListName = myMusicListRepository.findByMemberIdAndListName(
        member,
        listName);
    if (byMemberIdAndListName.isEmpty()) {
      throw new NotFoundMusicListException();
    }
    MyMusicListEntity myMusicList = byMemberIdAndListName.get();

    // List의 회원정보와 일치하지 않을 때
    if (!myMusicList.getMemberId().getMemberId().equals(member.getMemberId())) {
      throw new MemberIdMismatchException();
    }

    MusicEntity musicEntity = MusicEntity.builder()
        .listId(myMusicList)
        .musicName(addRequest.getMusicName())
        .musicUrl(addRequest.getMusicUrl())
        .build();
    musicRepository.save(musicEntity);;

    // 해당하는 뮤직 리스트의 노래 개수를 증가
    MyMusicListEntity myMusicListEntity = myMusicList.toBuilder()
        .numberOfMusic(myMusicList.getNumberOfMusic() + 1)
        .build();
    myMusicListRepository.save(myMusicListEntity);

    return "해당 리스트에 노래를 추가했습니다.";
  }

  @Override
  @Transactional
  public List<PlayListDto> playList(String listName, Boolean repeatPlay, Boolean randomPlay) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();
    Optional<MemberEntity> byEmail = memberRepository.findByEmail(email);
    if (byEmail.isEmpty()) {
      throw new NotFoundMemberException();
    }
    MemberEntity member = byEmail.get();

    Optional<MyMusicListEntity> byMemberIdAndListName = myMusicListRepository.findByMemberIdAndListName(
        member,
        listName);
    if (byMemberIdAndListName.isEmpty()) {
      throw new NotFoundMusicListException();
    }
    MyMusicListEntity myMusicList = byMemberIdAndListName.get();

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
