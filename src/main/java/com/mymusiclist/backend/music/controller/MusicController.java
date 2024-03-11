package com.mymusiclist.backend.music.controller;

import com.mymusiclist.backend.music.dto.MyMusicListDto;
import com.mymusiclist.backend.music.dto.PlayListDto;
import com.mymusiclist.backend.music.dto.YoutubeSearchDto;
import com.mymusiclist.backend.music.dto.request.AddRequest;
import com.mymusiclist.backend.music.dto.request.DeleteRequest;
import com.mymusiclist.backend.music.dto.request.UpdateRequest;
import com.mymusiclist.backend.music.service.MusicService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/music")
public class MusicController {

  private final MusicService musicService;

  @GetMapping("/search")
  public ResponseEntity<List<YoutubeSearchDto>> search(HttpServletRequest request,
      @Valid @NotNull(message = "검색어가 없으면 안됩니다.") @RequestParam(name = "keyword") String keyword) {
    String accessToken = getToken(request);
    List<YoutubeSearchDto> response = musicService.search(accessToken, keyword);
    log.info("Music searchKeyword: {}", keyword);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/lists")
  public ResponseEntity<String> createList(HttpServletRequest request,
      @Valid @NotBlank(message = "뮤직 리스트 이름은 공백일 수 없습니다.") @RequestParam(name = "listName") String listName) {
    String accessToken = getToken(request);
    String response = musicService.createList(accessToken, listName);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/lists")
  public ResponseEntity<String> deleteList(HttpServletRequest request,
      @Valid @NotBlank(message = "뮤직 리스트 이름은 공백일 수 없습니다.") @RequestParam(name = "listName") String listName) {
    String accessToken = getToken(request);
    String response = musicService.deleteList(accessToken, listName);
    return ResponseEntity.ok(response);
  }

  @PutMapping("/lists/{listName}")
  public ResponseEntity<MyMusicListDto> updateList(HttpServletRequest request,
      @PathVariable(name = "listName") String listName,
      @Valid @RequestBody UpdateRequest updateRequest) {
    String accessToken = getToken(request);
    MyMusicListDto response = musicService.updateList(accessToken, listName, updateRequest);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/lists/my-list")
  public ResponseEntity<List<String>> getMyList(HttpServletRequest request) {
    String accessToken = getToken(request);
    List<String> response = musicService.getMyList(accessToken);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/lists/{listName}")
  public ResponseEntity<MyMusicListDto> detail(HttpServletRequest request,
      @PathVariable(name = "listName") String listName) {
    String accessToken = getToken(request);
    MyMusicListDto response = musicService.detail(accessToken, listName);
    return ResponseEntity.ok(response);
  }

  @PostMapping("lists/{listName}/music")
  public ResponseEntity<String> addMusic(HttpServletRequest request,
      @PathVariable(name = "listName") String listName,
      @Valid @RequestBody List<AddRequest> addRequest) {
    String accessToken = getToken(request);
    String response = musicService.addMusic(accessToken, listName, addRequest);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("lists/{listName}/music")
  public ResponseEntity<String> deleteMusic(HttpServletRequest request,
      @PathVariable(name = "listName") String listName,
      @RequestBody @NotNull(message = "노래 식별자는 NULL 일 수 없습니다.") DeleteRequest deleteRequest) {
    String accessToken = getToken(request);
    String response = musicService.deleteMusic(accessToken, listName, deleteRequest);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/playlist/{listName}")
  public ResponseEntity<List<PlayListDto>> playList(HttpServletRequest request,
      @PathVariable(name = "listName") String listName,
      @RequestParam(name = "repeatPlay") Boolean repeatPlay,
      @RequestParam(name = "randomPlay") Boolean randomPlay) {
    String accessToken = getToken(request);
    List<PlayListDto> response = musicService.playList(accessToken, listName, repeatPlay, randomPlay);
    return ResponseEntity.ok(response);
  }

  private static String getToken(HttpServletRequest request) {
    String token = request.getHeader("Authorization");
    if (token != null && token.startsWith("Bearer ")) {
      token = token.substring(7); // "Bearer " 이후의 토큰 값만 추출
    }
    return token;
  }
}
