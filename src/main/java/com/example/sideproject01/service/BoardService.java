package com.example.sideproject01.service;

import com.example.sideproject01.dto.BoardDto;
import com.example.sideproject01.dto.BoardListResponse;

public interface BoardService {

    // ✅ 게시글 목록 (카테고리 + 페이징)
    BoardListResponse getBoardList(int pageNum, int pageSize, String category);

    // ✅ 게시글 등록
    Long addBoard(BoardDto boardDto, Long userId);

    // ✅ 게시글 수정 (수정된 DTO 반환)
    BoardDto updateBoard(Long id, BoardDto dto, Long userId);

    // ✅ 게시글 삭제 (결과 메시지 반환)
    String deleteBoard(Long id, Long userId);

    // ✅ 게시글 상세조회
    BoardDto getDetail(Long id, Long userId);
}
