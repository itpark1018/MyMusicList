package com.mymusiclist.backend.music.repository;

import com.mymusiclist.backend.music.domain.MusicEntity;
import com.mymusiclist.backend.music.domain.MyMusicListEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MusicRepository extends JpaRepository<MusicEntity, Long> {

  Optional<MusicEntity> findByMusicName(String musicName);

  Optional<MusicEntity> findByMusicNameAndListId(String musicName, MyMusicListEntity listId);

  List<MusicEntity> findByListId(MyMusicListEntity myMusicList);

  Optional<MusicEntity> findByListIdAndMusicId(MyMusicListEntity myMusicList, Long id);
}
