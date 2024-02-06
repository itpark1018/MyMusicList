package com.mymusiclist.backend.music.controller;

import com.mymusiclist.backend.music.dto.MyMusicListDto;
import com.mymusiclist.backend.music.dto.PlayListDto;
import com.mymusiclist.backend.music.dto.request.AddRequest;
import com.mymusiclist.backend.music.dto.request.UpdateRequest;
import com.mymusiclist.backend.music.service.MusicService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
  public ResponseEntity<List<Map<String, String>>> search(
      @RequestParam(name = "keyword") String keyword) {
    List<Map<String, String>> response = musicService.search(keyword);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/list/create")
  public ResponseEntity<String> createList(@RequestParam(name = "listName") String listName) {
    String response = musicService.createList(listName);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/list/delete")
  public ResponseEntity<String> deleteList(@RequestParam(name = "listName") String listName) {
    String response = musicService.deleteList(listName);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/list/{listName}/update")
  public ResponseEntity<MyMusicListDto> updateList(@PathVariable(name = "listName") String listName,
      @RequestBody UpdateRequest updateRequest) {
    MyMusicListDto response = musicService.updateList(listName, updateRequest);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/list/myList")
  public ResponseEntity<List<String>> getMyList() {
    List<String> response = musicService.getMyList();
    return ResponseEntity.ok(response);
  }

  @GetMapping("/list/{listName}/detail")
  public ResponseEntity<MyMusicListDto> detail(@PathVariable(name = "listName") String listName) {
    MyMusicListDto response = musicService.detail(listName);
    return ResponseEntity.ok(response);
  }

  @PostMapping("list/{listName}/add")
  public ResponseEntity<String> addMusic(@PathVariable(name = "listName") String listName,
      @RequestBody AddRequest addRequest) {
    String response = musicService.addMusic(listName, addRequest);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/playList/{listName}")
  public ResponseEntity<List<PlayListDto>> playList(
      @PathVariable(name = "listName") String listName,
      @RequestParam(name = "repeatPlay") Boolean repeatPlay,
      @RequestParam(name = "randomPlay") Boolean randomPlay) {
    List<PlayListDto> response = musicService.playList(listName, repeatPlay, randomPlay);
    return ResponseEntity.ok(response);
  }
}
