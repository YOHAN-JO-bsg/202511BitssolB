package com.example.sideproject01.service;

import com.example.sideproject01.dto.CommentDto;
import com.example.sideproject01.dto.CommentListResponse;

public interface CommentService {

    // 댓글 목록
    CommentListResponse getComments(Long boardId, int pageNum, int pageSize, Long userId);

    // 댓글 등록 (원댓글 / 대댓글)
    void createComment(Long boardId, CommentDto dto, Long userId);

    // 댓글 수정 (작성자만)
    void updateComment(Long commentId, CommentDto dto, Long userId);

    // 댓글 삭제 (작성자만) ✅ 이게 반드시 있어야 함
    void deleteComment(Long commentId, Long userId);
}
