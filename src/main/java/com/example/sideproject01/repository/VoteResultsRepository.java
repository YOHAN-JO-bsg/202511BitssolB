package com.example.sideproject01.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.sideproject01.entity.VoteResults;

import java.util.Optional;

public interface VoteResultsRepository extends JpaRepository<VoteResults, Long> {

    // 특정 투표 항목(option)에 몇 명이 투표했는지 수를 셈
    int countByVotes(com.example.sideproject01.entity.Votes votes);

    // 특정 사용자(user)가 특정 게시글(board)의 투표에 참여했는지 여부를 확인 (JPQL 사용)
    @org.springframework.data.jpa.repository.Query("SELECT COUNT(vr) > 0 FROM VoteResults vr WHERE vr.user = :user AND vr.votes.board = :board")
    boolean existsByUserAndBoard(@org.springframework.data.repository.query.Param("user") com.example.sideproject01.entity.Users user, @org.springframework.data.repository.query.Param("board") com.example.sideproject01.entity.Board board);

    // 특정 사용자가 특정 투표 항목에 투표했는지 조회
    Optional<VoteResults> findByUserAndVotes(com.example.sideproject01.entity.Users user, com.example.sideproject01.entity.Votes votes);
}
