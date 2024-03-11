package com.mymusiclist.backend.music.service;

import com.mymusiclist.backend.music.dto.MyMusicListDto;
import com.mymusiclist.backend.music.dto.PlayListDto;
import com.mymusiclist.backend.music.dto.YoutubeSearchDto;
import com.mymusiclist.backend.music.dto.request.AddRequest;
import com.mymusiclist.backend.music.dto.request.DeleteRequest;
import com.mymusiclist.backend.music.dto.request.UpdateRequest;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface MusicService {

  List<YoutubeSearchDto> search(String accessToken, String keyword);

  String createList(String accessToken, String listName);

  String deleteList(String accessToken, String listName);

  MyMusicListDto updateList(String accessToken, String listName, UpdateRequest updateRequest);

  List<String> getMyList(String accessToken);

  MyMusicListDto detail(String accessToken, String listName);

  String addMusic(String accessToken, String listName, List<AddRequest> addRequest);

  String deleteMusic(String accessToken, String listName, DeleteRequest deleteRequest);

  List<PlayListDto> playList(String accessToken, String listName, Boolean repeatPlay, Boolean randomPlay);
}
