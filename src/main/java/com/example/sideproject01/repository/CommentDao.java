package com.example.sideproject01.repository;

import java.util.List;

import com.example.sideproject01.dto.CommentDto;

public interface CommentDao {
	
	public List<CommentDto> selectList(CommentDto dto);
	public int delete(int num);
	public int update(CommentDto dto);
	public void insert(CommentDto dto);
	public int getSequence();
	public CommentDto getByNum(int num);
	// 원글에 달린 댓글의 갯수를 리턴해주는 메소드
	public int getCount(int parentNum);
	
	
}
