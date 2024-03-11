package com.mymusiclist.backend.admin.service;

import com.mymusiclist.backend.admin.dto.AdminCommentListDto;
import com.mymusiclist.backend.admin.dto.AdminPostListDto;
import com.mymusiclist.backend.admin.dto.MemberDetailDto;
import com.mymusiclist.backend.admin.dto.request.CommentUpdateRequest;
import com.mymusiclist.backend.admin.dto.request.MemberUpdateRequest;
import com.mymusiclist.backend.admin.dto.request.PostUpdateRequest;
import com.mymusiclist.backend.type.MemberStatus;
import com.mymusiclist.backend.type.SearchOption;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface AdminService {

  String setMemberStatus(String accessToken, Long memberId, MemberStatus memberStatus);

  MemberDetailDto getMemberInfo(String accessToken, Long memberId);

  String memberUpdate(String accessToken, Long memberId, MemberUpdateRequest memberUpdateRequest);

  List<MemberDetailDto> searchMember(String accessToken, String keyword, SearchOption searchOption);

  String postDelete(String accessToken, Long postId);

  String postUpdate(String accessToken, PostUpdateRequest postUpdateRequest);

  String commentDelete(String accessToken, Long commentId);

  String commentUpdate(String accessToken, CommentUpdateRequest commentUpdateRequest);

  List<AdminPostListDto> searchPost(String accessToken, String keyword, SearchOption searchOption);

  List<AdminCommentListDto> searchComment(String accessToken, String keyword, SearchOption searchOption);
}
