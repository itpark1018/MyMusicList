package com.mymusiclist.backend.music.service;

import com.mymusiclist.backend.music.dto.MyMusicListDto;
import com.mymusiclist.backend.music.dto.PlayListDto;
import com.mymusiclist.backend.music.dto.request.AddRequest;
import com.mymusiclist.backend.music.dto.request.UpdateRequest;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public interface MusicService {

  List<Map<String, String>> search(String keyword);

  String createList(String listName);

  String deleteList(String listName);

  MyMusicListDto updateList(String listName, UpdateRequest updateRequest);

  List<String> getMyList();

  MyMusicListDto detail(String listName);

  String addMusic(String listName, AddRequest addRequest);

  List<PlayListDto> playList(String listName, Boolean repeatPlay, Boolean randomPlay);
}
