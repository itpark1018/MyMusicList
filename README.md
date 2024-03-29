# 🎧 MyMusicList
나의 뮤직 리스트를 만들고 사용자들과 공유하고 소통하는 커뮤니티 프로젝트 입니다.
현재는 백엔드 부분만 개발 중입니다.

# 프로젝트 기획 배경
- 음원 사이트 등에서 들을 수 없는 노래(음악)도 나만의 리스트를 만들어서 듣고 싶다.
- 내가 좋아하는 노래를 공유하고 추천하는 커뮤니티 사이트가 있으면 좋을 것 같다.
- 텍스트로만 공유하는 것이 아니라 공유하고 싶은 노래를 같이 들을 수 있으면 좋을 것 같다.

# 프로젝트 기능 및 설계
- 회원가입 기능
  - 회원가입은 이메일과 비밀번호 입력으로 가입 가능(이메일 인증이 필요함)합니다.
  - 동일한 정보로는 중복 가입이 불가능(이메일은 unique 해야한다.)합니다.
  
- 로그인 기능
  - 사용자는 회원가입시 사용한 이메일과 비밀번호를 통해 로그인 가능합니다.
    
- 로그아웃 기능
  - 사용자는 언제든지 로그아웃이 가능합니다.

- 비밀번호 찾기(비밀번호 변경) 기능
  - 회원가입시 작성한 정보를 바탕으로 비밀번호 찾기(비밀번호 변경)가 가능합니다.
 
- 회원정보 수정 기능
  - 본인의 회원정보의 수정이 가능합니다.(닉네임, 비밀번호, 대표이미지 변경 등)
  - 비밀번호를 변경은 비밀번호 찾기(비밀번호 변경) 기능을 제공합니다.(프론트에서 비밀번호 찾기(비밀번호 변경) 기능으로 링크)
  - 닉네임 변경시 해당 회원의 게시글, 댓글에 등록된 닉네임을 일괄적으로 변경해주는 작업이 필요합니다.

- 내 정보 보기
  - 회원 본인의 회원정보를 볼 수 있습니다.
  - 볼 수 있는 본인의 회원정보는 이메일, 닉네임, 프로필 이미지, 소개글, 가입일 입니다.
 
- 다른 회원 정보 보기
  - 본인이 아닌 다른 회원의 정보를 볼 수 있습니다.
  - 볼 수 있는 다른 회원의 정보는 닉네임, 프로필 이미지, 소개글 입니다.
    
- 회원탈퇴 기능
  - 사용자는 본인이 원할 시 회원탈퇴가 가능합니다.
 
- 노래 검색 기능
  - 유튜브 크롤링을 통해 노래 검색이 가능합니다.
  - YouTube Data API v3 라는 API를 사용해서 키워드의 해당하는 영상을 최대 5개까지 검색하고 제목과 링크를 반환합니다.

- 뮤직 리스트 기능
  - 뮤직 리스트를 생성 가능합니다.
  - 뮤직 리스트를 삭제 가능합니다.
  - 뮤직 리스트를 수정 가능합니다.
  - 내 뮤직 리스트 목록 보기가 가능합니다.
  - 뮤직 리스트 상세보기가 가능합니다.
  - 검색한 노래를 생성되어있는 뮤직 리스트에 추가가 가능합니다.
  - 리스트에 추가된 노래를 리스트 단위로 재생(반복, 램덤, 일반)이 가능합니다.(백엔드에서는 플레이 리스트를 반환하고 프론트에서 재생기능을 제공합니다.)

- 게시글 작성 기능
  - 로그인 한 사용자에 한에서 게시글을 작성할 수 있습니다.
  - 게시글은 제목(텍스트), 내용(텍스트) 작성할 수 있으며 내 뮤직 리스트 공유가 가능합니다.
  
- 게시글 수정 기능
  - 게시글을 작성한 본인에 한에서 게시글을 수정할 수 있습니다.
    
- 게시글 삭제 기능
  - 게시글을 작성한 본인에 한에서 게시글을 삭제할 수 있습니다.
  - 게시글이 삭제되면 게시글에 작성된 댓글도 같이 삭제가 됩니다.
    
- 게시글 목록 조회 기능
  - 로그인 하지않은 사용자를 포함에 모든 사용자가 조회 가능합니다.
  - 게시글은 기본적으로 최신순으로 정렬되며, 추천 개수가 많은 순으로도 정렬이 가능합니다.
  - 게시글 목록 조회시 응답에는 게시글 제목과 작성자의 닉네임, 작성일, 추천(좋아요) 수, 댓글 수의 정보가 필요하다.
 
- 특정 게시글 조회 기능
  - 로그인 하지않은 사용자를 포함에 모든 사용자가 조회 가능합니다.
  - 게시글 제목, 게시글 내용, 공유된 뮤직 리스트, 작성자, 작성일, 추천개수, 댓글개수, 댓글이 조회됩니다.
 
- 내 게시글 조회
  - 자신이 작성한 게시글 목록을 조회 가능합니다.
  
- 게시글 추천(좋아요) 기능
  - 로그인한 회원만 추천이 가능합니다.
  - 추천은 게시글 한개당 한번만 가능합니다.
  
- 게시글 검색 기능
  - 특정 키워드를 통한 게시글 검색이 가능합니다.
  
- 댓글 작성 기능
  - 로그인 한 사용자에 한에서 댓글(텍스트)을 작성 할 수 있습니다.
  
- 댓글 삭제 기능
  - 본인이 작성한 댓글에 한에서만 삭제가 가능합니다.
  
- 댓글 수정 기능
  -본인이 작성한 댓글에 한에서만 수정이 가능합니다.
  
- 댓글 추천(좋아요) 기능
  - 로그인 한 사용자에 한에서 댓글 추천이 가능합니다.
  - 특정 댓글에 한번만 추천 할 수 있습니다.

- 댓글 목록 조회 기능
  - 특정 게시글 조회시 댓글목록도 함께 조회가 된다. 다만 댓글은 많을 수 있기 때문에 별도의 API로 구성한다. 이 또한 로그인하지 않은 사용자를 포함한 모든 사용자가 댓글을 조회할 수 있다.
  - 댓글은 최신순으로 정렬됩니다.
  - 댓글 목록 조회시에는 댓글 작성자와 댓글 내용, 댓글 작성일, 추천개수의 정보가 필요합니다.
 
- 내 댓글 조회 기능
  - 본인이 작성한 모든 댓글을 조회할 수 있습니다.
  
- 관리자 기능
  - 관리자 계정에 한에서 모든 회원의 상태에 대한 설정이 가능합니다.(회원 차단 기능)
  - 관리자 계정에 한에서 모든 회원의 회원정보의 변경이 가능하고 회원정보를 열람 할 수 있습니다.
  - 관리자 계정에 한에서 회원의 이름, 닉네임을 통해 회원을 검색할 수 있습니다.
  - 관리자 계정에 한에서 모든 게시글 삭제, 수정이 가능합니다.
  - 관리자 계정에 한에서 모든 댓글 삭제, 수정이 가능합니다.
  - 관리자 계정에 한에서 삭제된 게시글, 댓글을 검색, 확인할 수 있습니다.

# ERD
![MyMusicList](https://github.com/itpark1018/MyMusicList/assets/117416583/83e0231d-3962-4b8c-8ec5-6f1895ef18bb)

# 기술스택
- SpringBoot
- JAVA
- JPA
- Spring Security
- Redis

# Notion
https://blossom-radon-00c.notion.site/MyMusicList-1f22f27301cf4ba087fb09efdd1af071?pvs=4
