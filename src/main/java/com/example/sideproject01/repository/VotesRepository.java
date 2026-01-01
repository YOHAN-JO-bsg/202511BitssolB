package com.example.sideproject01.repository;

import com.example.sideproject01.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.sideproject01.entity.Votes; // ✅ 엔티티 import 추가

public interface VotesRepository extends JpaRepository<Votes, Long> {
    java.util.List<Votes> findByBoard(com.example.sideproject01.entity.Board board);

    // ✅ 게시글(Board)에 속한 모든 투표 삭제
    void deleteByBoard(Board board);
}
