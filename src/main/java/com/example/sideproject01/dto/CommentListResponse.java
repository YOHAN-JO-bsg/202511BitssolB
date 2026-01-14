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

	// 댓글 목록
	private List<CommentDto> comments;
	private int currentPage;
	private int totalPages;
	private long totalElements;
	private int startPageNum;
	private int endPageNum;
}
