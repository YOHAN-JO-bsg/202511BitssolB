package com.example.sideproject01.repository;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.example.sideproject01.dto.CommentDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class CommentDaoImpl implements CommentDao{
	
	private final SqlSession session;

	// 원글의 글번호를 이용해서 원글에 달린 댓글 목록을 리턴하는 메소드
	@Override
	public List<CommentDto> selectList(CommentDto dto) {
		// 페이징처리에 맞는 row 만 select 하기 위해 CommentDto 를 파라미터로 전달한다
		return session.selectList("comment.selectList", dto);
	}

	@Override
	public int delete(int num) {

		return session.update("comment.delete", num);
	}

	@Override
	public int update(CommentDto dto) {

		return session.update("comment.update", dto);
	}

	@Override
	public void insert(CommentDto dto) {
		session.insert("comment.insert", dto);
		
	}

	@Override
	public int getSequence() {

		return session.selectOne("comment.getSequence");
	}

	@Override
	public CommentDto getByNum(int num) {
			
		return session.selectOne("comment.getByNum", num);
	}

	@Override
	public int getCount(int parentNum) {

		return session.selectOne("comment.getCount", parentNum);
	}

	
	
}








