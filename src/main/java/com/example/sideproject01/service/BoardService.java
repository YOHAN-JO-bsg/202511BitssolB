package com.example.sideproject01.service;

import com.example.sideproject01.dto.BoardDto;
import com.example.sideproject01.dto.BoardListResponse;

public interface BoardService {

    BoardListResponse getBoardList(int pageNum, int pageSize, String category);

    Long addBoard(BoardDto boardDto, Long userId);

    BoardDto updateBoard(Long id, BoardDto dto, Long userId);

    String deleteBoard(Long id, Long userId);

    BoardDto getDetail(Long id, Long userId);
}