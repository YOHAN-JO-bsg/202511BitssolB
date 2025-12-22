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
	private List<CommentDto> list;
	private int startPageNum;
	private int endPageNum;
	private int totalPageNum;
	private int pageNum;
}
