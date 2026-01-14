package com.example.sideproject01.repository;

import com.example.sideproject01.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    // 전체 게시글 전체조회 & 필터 조회
    @Query(value = "SELECT * FROM (" +
            "    SELECT inner_table.*, ROWNUM rn FROM (" +
            "        SELECT * FROM board ORDER BY board_id DESC" +
            "    ) inner_table WHERE ROWNUM <= :endRow" +
            ") WHERE rn > :startRow", nativeQuery = true)
    List<Board> findAllWithPagination(@Param("startRow") int startRow, @Param("endRow") int endRow);

    // 카테고리별 필터 조회
    @Query(value = "SELECT * FROM (" +
            "    SELECT inner_table.*, ROWNUM rn FROM (" +
            "        SELECT * FROM board " +
            "        WHERE UPPER(TRIM(category)) = UPPER(TRIM(:category)) " +
            "        ORDER BY board_id DESC" +
            "    ) inner_table WHERE ROWNUM <= :endRow" +
            ") WHERE rn > :startRow", nativeQuery = true)
    List<Board> findByCategoryWithPagination(@Param("category") String category, @Param("startRow") int startRow,
            @Param("endRow") int endRow);

    // 카테고리별 전체 게시글 갯수 조회
    @Query(value = "SELECT COUNT(*) FROM board WHERE UPPER(TRIM(category)) = UPPER(TRIM(:category))", nativeQuery = true)
    long countByCategory(@Param("category") String category);
}
