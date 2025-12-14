package com.example.sideproject01.controller;

import com.example.sideproject01.service.LikesService;
import com.example.sideproject01.service.VoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.sideproject01.dto.BoardDto;
import com.example.sideproject01.dto.BoardListResponse;
import com.example.sideproject01.dto.CommentDto;
import com.example.sideproject01.dto.CommentListResponse;
import com.example.sideproject01.dto.LikesDto;
import com.example.sideproject01.dto.VoteResultsDto;
import com.example.sideproject01.service.BoardService;
import com.example.sideproject01.service.CommentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.tags.Tag;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1")
@Tag(name = "Board", description = "Board and Comment API")
public class BoardController {

    private final BoardService boardService;
    private final CommentService commentService;
    private final LikesService likesService;
    private final VoteService voteService;

    /* =============================
     * ✅ [댓글 관련 API]
     * ============================= */

    // 댓글 등록
    @PostMapping("/board/{boardId}/comments")
    public ResponseEntity<Void> createComment(@PathVariable Long boardId, @RequestBody CommentDto dto) {
        commentService.createComment(boardId, dto);
        return ResponseEntity.noContent().build();
    }

    // 댓글 수정
    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<Void> updateComment(@PathVariable Long commentId, @RequestBody CommentDto dto,
                                            @RequestParam("userId") Long userId) {
        commentService.updateComment(commentId, dto, userId);
        return ResponseEntity.noContent().build();
    }

    // 댓글 삭제
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId,
                                            @RequestParam("userId") Long userId) { // <-- userId 추가
        commentService.deleteComment(commentId, userId); // <-- userId 전달
        return ResponseEntity.noContent().build();
    }

    // 댓글 삭제 테스트 이전 삭제 로직 위에거와 비교해서 쓰기
    /* @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    } */

    // 댓글 목록 (페이징 + 계층 구조)
    @GetMapping("/board/{boardId}/comments")
    public ResponseEntity<CommentListResponse> getComments(
            @PathVariable Long boardId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Long userId) {
        CommentListResponse response = commentService.getComments(boardId, pageNum, pageSize, userId);
        return ResponseEntity.ok(response);
    }

    /* =============================
     * ✅ [좋아요 관련 API]
     * ============================= */
    // 좋아요 추가/취소 (토글)
    @PostMapping("/likes")
    public ResponseEntity<Boolean> toggleLike(@RequestBody LikesDto likesDto) {
        // userId는 LikesDto에 포함되어 넘어옵니다.
        // 현재는 인증 로직이 없으므로, LikesDto에서 직접 userId를 사용합니다.
        if (likesDto.getUserId() == null) {
            throw new IllegalArgumentException("userId는 필수입니다.");
        }
        boolean isLiked = likesService.toggleLike(likesDto.getUserId(), likesDto.getTargetType(), likesDto.getTargetId());
        return ResponseEntity.ok(isLiked);
    }

    /* =============================
     * ✅ [투표 관련 API]
     * ============================= */
    // 투표 실행
    @PostMapping("/votes/cast")
    public ResponseEntity<Long> castVote(@RequestBody VoteResultsDto voteResultsDto) {
        // userId는 VoteResultsDto에 포함되어 넘어옵니다.
        // 현재는 인증 로직이 없으므로, VoteResultsDto에서 직접 userId를 사용합니다.
        if (voteResultsDto.getUserId() == null) {
            throw new IllegalArgumentException("userId는 필수입니다.");
        }
        Long resultId = voteService.castVote(voteResultsDto.getUserId(), voteResultsDto.getVoteId());
        return ResponseEntity.ok(resultId);
    }


    /* =============================
     * ✅ [게시판 관련 API]
     * ============================= */

    // ✅ 게시글 목록 (카테고리 + 페이징)
    @GetMapping("/board")
    public ResponseEntity<BoardListResponse> getBoardList(
            @RequestParam(defaultValue = "all") String category,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        BoardListResponse response = boardService.getBoardList(pageNum, pageSize, category);
        return ResponseEntity.ok(response);
    }

    // ✅ 게시글 상세 조회
    @GetMapping("/board/{id}")
    public ResponseEntity<BoardDto> getDetail(@PathVariable Long id,
                                            @RequestParam(required = false) Long userId) {
        BoardDto dto = boardService.getDetail(id, userId);
        return ResponseEntity.ok(dto);
    }

    // ✅ 게시글 등록
    @PostMapping("/board")
    public ResponseEntity<BoardDto> createBoard(@Valid @RequestBody BoardDto dto) {
        Long id = boardService.addBoard(dto);
        dto.setBoardId(id);
        return ResponseEntity.ok(dto);
    }

    // ✅ 게시글 수정
    @PutMapping("/board/{id}")
    public ResponseEntity<BoardDto> updateBoard(@PathVariable Long id, @RequestBody BoardDto dto) {
        BoardDto updated = boardService.updateBoard(id, dto);
        return ResponseEntity.ok(updated);
    }

    // ✅ 게시글 삭제
    @DeleteMapping("/board/{id}")
    public ResponseEntity<String> deleteBoard(@PathVariable Long id) {
        String result = boardService.deleteBoard(id);
        return ResponseEntity.ok(result);
    }
}
