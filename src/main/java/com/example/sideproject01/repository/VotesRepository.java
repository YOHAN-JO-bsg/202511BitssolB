package com.example.sideproject01.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.sideproject01.entity.Votes; // ✅ 엔티티 import 추가

public interface VotesRepository extends JpaRepository<Votes, Long> {
    java.util.List<Votes> findByBoard(com.example.sideproject01.entity.Board board);
}
