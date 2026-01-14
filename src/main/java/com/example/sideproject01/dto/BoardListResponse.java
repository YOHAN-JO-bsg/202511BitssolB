package com.example.sideproject01.dto;

import java.util.List;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardListResponse {

    private List<BoardDto> boards;
    private int currentPage;
    private int pageSize;
    private int totalPages;
    private long totalElements;
    private int startPageNum;
    private int endPageNum;

    private String keyword;
    private String search;
    private String category;
}
