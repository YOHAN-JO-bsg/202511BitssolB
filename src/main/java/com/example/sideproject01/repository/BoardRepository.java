package com.example.sideproject01.repository;

import com.example.sideproject01.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    /**
     * Oracle 11g용 수동 페이징 쿼리 (ROWNUM 사용)
     * 전체 게시글 목록을 지정된 범위만큼 조회합니다.
     * @param startRow 시작 행 번호
     * @param endRow 종료 행 번호
     * @return 페이징 처리된 게시글 목록
     */
    @Query(value = "SELECT * FROM (" +
            "    SELECT inner_table.*, ROWNUM rn FROM (" +
            "        SELECT * FROM board ORDER BY board_id DESC" +
            "    ) inner_table WHERE ROWNUM <= :endRow" +
            ") WHERE rn > :startRow", nativeQuery = true)
    List<Board> findAllWithPagination(@Param("startRow") int startRow, @Param("endRow") int endRow);


    /**
     * Oracle 11g용 수동 페이징 쿼리 (ROWNUM 사용)
     * 카테고리별 게시글 목록을 지정된 범위만큼 조회합니다.
     * @param category 카테고리명
     * @param startRow 시작 행 번호
     * @param endRow 종료 행 번호
     * @return 페이징 처리된 게시글 목록
     */
    @Query(value = "SELECT * FROM (" +
                   "    SELECT inner_table.*, ROWNUM rn FROM (" +
                   "        SELECT * FROM board WHERE category = :category ORDER BY board_id DESC" +
                   "    ) inner_table WHERE ROWNUM <= :endRow" +
                   ") WHERE rn > :startRow", nativeQuery = true)
    List<Board> findByCategoryWithPagination(@Param("category") String category, @Param("startRow") int startRow, @Param("endRow") int endRow);

    /**
     * 카테고리별 전체 게시글 개수를 조회합니다.
     * @param category 카테고리명
     * @return 해당 카테고리의 전체 게시글 수
     */
    @Query(value = "SELECT COUNT(*) FROM board WHERE category = :category", nativeQuery = true)
    long countByCategory(@Param("category") String category);
}
