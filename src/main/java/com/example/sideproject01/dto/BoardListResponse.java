package com.example.sideproject01.dto;

import java.util.List;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardListResponse {

    private List<BoardDto> list;     // 게시글 목록
    private int pageNum;             // 현재 페이지 번호
    private int pageSize;            // 한 페이지당 게시글 수
    private int totalPageCount;      // 전체 페이지 수
    private long totalRow;           // 전체 게시글 수
    private int startPageNum;        // 시작 페이지 번호
    private int endPageNum;          // 끝 페이지 번호

    private String keyword;          // 검색 키워드
    private String search;           // 검색 조건(title, writer 등)
    private String category;         // 카테고리 (전체, 자유, 일상 등)
}
