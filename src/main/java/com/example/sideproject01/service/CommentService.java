package com.example.sideproject01.service;

import com.example.sideproject01.dto.CommentDto;
import com.example.sideproject01.dto.CommentListResponse;

public interface CommentService {

    // 게시글에 달린 댓글 목록 (페이징 처리 및 계층 구조 포함)
    // boardId 에 해당하는 모든 댓글을 가져오되, 페이징은 최상위 댓글에 적용
    CommentListResponse getComments(Long boardId, int pageNum, int pageSize, Long userId);

    // 댓글 저장 (원댓글 또는 대댓글)
    void createComment(Long boardId, CommentDto dto);

    // 댓글 수정
    void updateComment(Long commentId, CommentDto dto, Long userId);

    // 댓글 삭제
    // 기존 삭제 메소드 void deleteComment(Long commentId);

    void deleteComment(Long commentId, Long userId); // 테스트용 메소드
}

