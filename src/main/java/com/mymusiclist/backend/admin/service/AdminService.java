package com.mymusiclist.backend.admin.service;

import com.mymusiclist.backend.admin.dto.AdminCommentListDto;
import com.mymusiclist.backend.admin.dto.AdminPostListDto;
import com.mymusiclist.backend.admin.dto.MemberDetailDto;
import com.mymusiclist.backend.admin.dto.request.CommentUpdateRequest;
import com.mymusiclist.backend.admin.dto.request.MemberStatusRequest;
import com.mymusiclist.backend.admin.dto.request.MemberUpdateRequest;
import com.mymusiclist.backend.admin.dto.request.PostUpdateRequest;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface AdminService {

  String setMemberStatus(MemberStatusRequest memberStatusRequest);

  MemberDetailDto getMemberInfo(Long memberId);

  String memberUpdate(MemberUpdateRequest memberUpdateRequest);

  List<MemberDetailDto> searchMember(String keyword, String searchOption);

  String postDelete(Long postId);

  String postUpdate(PostUpdateRequest postUpdateRequest);

  String commentDelete(Long commentId);

  String commentUpdate(CommentUpdateRequest commentUpdateRequest);

  List<AdminPostListDto> searchPost(String keyword, String searchOption);

  List<AdminCommentListDto> searchComment(String keyword, String searchOption);
}
