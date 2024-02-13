package com.mymusiclist.backend.music.controller;

import com.mymusiclist.backend.music.dto.MyMusicListDto;
import com.mymusiclist.backend.music.dto.PlayListDto;
import com.mymusiclist.backend.music.dto.YoutubeSearchDto;
import com.mymusiclist.backend.music.dto.request.AddRequest;
import com.mymusiclist.backend.music.dto.request.UpdateRequest;
import com.mymusiclist.backend.music.service.MusicService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/music")
public class MusicController {

  private final MusicService musicService;

  @GetMapping("/search")
  public ResponseEntity<List<YoutubeSearchDto>> search(
      @Valid @NotNull(message = "검색어가 없으면 안됩니다.") @RequestParam(name = "keyword") String keyword) {
    List<YoutubeSearchDto> response = musicService.search(keyword);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/lists")
  public ResponseEntity<String> createList(
      @Valid @NotBlank(message = "뮤직 리스트 이름은 공백일 수 없습니다.") @RequestParam(name = "listName") String listName) {
    String response = musicService.createList(listName);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/lists")
  public ResponseEntity<String> deleteList(
      @Valid @NotBlank(message = "뮤직 리스트 이름은 공백일 수 없습니다.") @RequestParam(name = "listName") String listName) {
    String response = musicService.deleteList(listName);
    return ResponseEntity.ok(response);
  }

  @PutMapping("/lists/{listName}")
  public ResponseEntity<MyMusicListDto> updateList(@PathVariable(name = "listName") String listName,
      @Valid @RequestBody UpdateRequest updateRequest) {
    MyMusicListDto response = musicService.updateList(listName, updateRequest);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/lists/myList")
  public ResponseEntity<List<String>> getMyList() {
    List<String> response = musicService.getMyList();
    return ResponseEntity.ok(response);
  }

  @GetMapping("/lists/{listName}/detail")
  public ResponseEntity<MyMusicListDto> detail(@PathVariable(name = "listName") String listName) {
    MyMusicListDto response = musicService.detail(listName);
    return ResponseEntity.ok(response);
  }

  @PostMapping("lists/{listName}/add")
  public ResponseEntity<String> addMusic(@PathVariable(name = "listName") String listName,
      @Valid @RequestBody AddRequest addRequest) {
    String response = musicService.addMusic(listName, addRequest);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/playlist/{listName}")
  public ResponseEntity<List<PlayListDto>> playList(
      @PathVariable(name = "listName") String listName,
      @RequestParam(name = "repeatPlay") Boolean repeatPlay,
      @RequestParam(name = "randomPlay") Boolean randomPlay) {
    List<PlayListDto> response = musicService.playList(listName, repeatPlay, randomPlay);
    return ResponseEntity.ok(response);
  }
}
