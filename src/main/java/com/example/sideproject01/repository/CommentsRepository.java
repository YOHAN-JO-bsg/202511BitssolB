package com.example.sideproject01.repository;

import com.example.sideproject01.entity.Comments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentsRepository extends JpaRepository<Comments, Long> {

    /**
     * Oracle 11g용 수동 페이징 쿼리 (ROWNUM 사용)
     * 게시글에 해당하는 최상위 댓글 목록을 지정된 범위만큼 조회합니다.
     * @param boardId  게시글 ID
     * @param isHidden 숨김 여부
     * @param startRow 시작 행
     * @param endRow   종료 행
     * @return 페이징 처리된 최상위 댓글 목록
     */
    @Query(value = "SELECT * FROM (" +
            "    SELECT inner_table.*, ROWNUM rn FROM (" +
            "        SELECT * FROM comments WHERE board_id = :boardId AND parent_id IS NULL AND is_hidden = :isHidden ORDER BY created_at DESC" +
            "    ) inner_table WHERE ROWNUM <= :endRow" +
            ") WHERE rn > :startRow", nativeQuery = true)
    List<Comments> findRootCommentsByBoardWithPagination(@Param("boardId") Long boardId, @Param("isHidden") Integer isHidden, @Param("startRow") int startRow, @Param("endRow") int endRow);

    /**
     * 게시글에 해당하는 최상위 댓글의 전체 개수를 조회합니다.
     * @param boardId  게시글 ID
     * @param isHidden 숨김 여부
     * @return 최상위 댓글의 전체 개수
     */
    @Query(value = "SELECT COUNT(*) FROM comments WHERE board_id = :boardId AND parent_id IS NULL AND is_hidden = :isHidden", nativeQuery = true)
    long countRootCommentsByBoard(@Param("boardId") Long boardId, @Param("isHidden") Integer isHidden);


    List<Comments> findByParentAndIsHiddenOrderByCreatedAtAsc(Comments parent, Integer isHidden);
}
