package com.example.sideproject01.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CommentListResponse {

	// 댓글 목록 (프론트엔드: comments)
	private List<CommentDto> comments;
	private int currentPage; // 현재 페이지 (프론트엔드: currentPage)
	private int totalPages; // 전체 페이지 수 (프론트엔드: totalPages)
	private long totalElements; // 전체 댓글 수 (프론트엔드: totalElements)
	private int startPageNum; // 시작 페이지 번호
	private int endPageNum; // 끝 페이지 번호
}
