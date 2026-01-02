package com.example.sideproject01.controller;

import com.example.sideproject01.security.CustomUserDetails;
import com.example.sideproject01.service.LikesService;
import com.example.sideproject01.service.VoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal; // ✅ 추가
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
@CrossOrigin(origins = "http://localhost:5173") // 프론트엔드 CORS 허용
@Tag(name = "Board", description = "Board and Comment API")
public class BoardController {

    private final BoardService boardService;
    private final CommentService commentService;
    private final LikesService likesService;
    private final VoteService voteService;

    /**
     * ✅ [댓글 관련 API]
     */

    // 댓글 등록
    @PostMapping("/board/{boardId}/comments")
    public ResponseEntity<Void> createComment(
            @PathVariable Long boardId,
            @RequestBody CommentDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails // ✅ 로그인 사용자 주입
    ) {
        // 🔧 수정: userId를 토큰에서 직접 추출
        commentService.createComment(boardId, dto, userDetails.getUserId());
        return ResponseEntity.noContent().build();
    }

    // 댓글 수정 (수동 userId 제거 -> 보안 적용)
    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<Void> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails // ✅ 변경
    ) {
        commentService.updateComment(commentId, dto, userDetails.getUserId());
        return ResponseEntity.noContent().build();
    }

    // 댓글 삭제 (수동 userId 제거 -> 보안 적용)
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserDetails userDetails // ✅ 변경
    ) {
        commentService.deleteComment(commentId, userDetails.getUserId());
        return ResponseEntity.noContent().build();
    }

    // 댓글 목록 (비회원 가능)
    @GetMapping("/board/{boardId}/comments")
    public ResponseEntity<CommentListResponse> getComments(
            @PathVariable Long boardId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @AuthenticationPrincipal CustomUserDetails userDetails // ✅ nullable
    ) {
        // 🔧 수정: 비로그인 시 null 처리
        Long userId = (userDetails != null) ? userDetails.getUserId() : null;
        CommentListResponse response = commentService.getComments(boardId, pageNum, pageSize, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * ✅ [좋아요 관련 API] (보안 적용)
     */
    @PostMapping("/likes")
    public ResponseEntity<Boolean> toggleLike(
            @RequestBody LikesDto likesDto,
            @AuthenticationPrincipal CustomUserDetails userDetails // ✅ 변경
    ) {
        boolean isLiked = likesService.toggleLike(
                userDetails.getUserId(),
                likesDto.getTargetType(),
                likesDto.getTargetId()
        );
        return ResponseEntity.ok(isLiked);
    }

    /**
     * ✅ [투표 관련 API] (보안 적용)
     */
    @PostMapping("/votes/cast")
    public ResponseEntity<Long> castVote(
            @RequestBody VoteResultsDto voteResultsDto,
            @AuthenticationPrincipal CustomUserDetails userDetails // ✅ 변경
    ) {
        Long resultId = voteService.castVote(
                userDetails.getUserId(),
                voteResultsDto.getVoteId()
        );
        return ResponseEntity.ok(resultId);
    }

    /**
     * ✅ [게시글 관련 API]
     */

    // 게시글 목록 (비회원 가능)
    @GetMapping("/board")
    public ResponseEntity<BoardListResponse> getBoardList(
            @RequestParam(defaultValue = "all") String category,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        BoardListResponse response = boardService.getBoardList(pageNum, pageSize, category);
        return ResponseEntity.ok(response);
    }

    // 게시글 상세조회 (비회원 가능 -> 읽기 전용)
    @GetMapping("/board/{id}")
    public ResponseEntity<BoardDto> getDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails // ✅ 변경
    ) {
        Long userId = (userDetails != null) ? userDetails.getUserId() : null;
        BoardDto dto = boardService.getDetail(id, userId);
        return ResponseEntity.ok(dto);
    }

    // 게시글 등록
    @PostMapping("/board")
    public ResponseEntity<BoardDto> createBoard(
            @Valid @RequestBody BoardDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails // ✅ 변경
    ) {
        // 🔧 수정: Service 시그니처에 맞게 userId 전달
        Long id = boardService.addBoard(dto, userDetails.getUserId());
        dto.setBoardId(id);
        return ResponseEntity.ok(dto);
    }

    // 게시글 수정
    @PutMapping("/board/{id}")
    public ResponseEntity<BoardDto> updateBoard(
            @PathVariable Long id,
            @RequestBody BoardDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails // ✅ 변경
    ) {
        BoardDto updated = boardService.updateBoard(id, dto, userDetails.getUserId());
        return ResponseEntity.ok(updated);
    }

    // 게시글 삭제
    @DeleteMapping("/board/{id}")
    public ResponseEntity<String> deleteBoard(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails // ✅ 변경
    ) {
        String result = boardService.deleteBoard(id, userDetails.getUserId());
        return ResponseEntity.ok(result);
    }
}
