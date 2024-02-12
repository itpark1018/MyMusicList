package com.mymusiclist.backend.music.youtube;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class YoutubeClient {

  @Value("${youtube.apiKey}")
  private String apiKey;
  public List<SearchResult> youtubeSearch(String keyword) throws IOException {

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

    return searchResults;
  }

}
