package com.example.sideproject01.dto;

import java.time.LocalDateTime;

import com.example.sideproject01.entity.Board;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardDto {

    private Long boardId;       // 게시글 번호
    private String writer;      // 작성자명 (User 엔티티에서 가져올 예정)
    private String title;       // 제목
    private String content;     // 내용
    private String category;    // 카테고리 (free, daily, question 등)
    private int viewCount;      // 조회수
    private String imageUrl;    // 이미지 URL

    // 좋아요 정보
    private int likeCount;
    private boolean likedByUser; // 현재 사용자의 좋아요 여부

    // 투표 정보
    private java.util.List<VotesDto> voteOptions; // 투표 항목 목록 (VotesDto는 voteCount, selectedByUser 포함)
    private boolean isVotedByUser; // 현재 사용자가 투표에 참여했는지 여부

    // 투표 항목 생성 요청용
    private java.util.List<String> voteOptionTexts;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime updatedAt;

    // 페이징 / 검색용
    private String keyword;
    private String search;

    // 이전글 / 다음글 이동용
    private Long prevId;
    private Long nextId;

    // 🔄 엔티티 → DTO 변환 생성자
    public BoardDto(Board board) {
        this.boardId = board.getBoardId();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.category = board.getCategory();
        this.viewCount = board.getViewCount();
        this.imageUrl = board.getImageUrl();
        this.createdAt = board.getCreatedAt();
        this.updatedAt = board.getUpdatedAt();

        // FK인 Users 엔티티의 값은 null 체크 후 이름 꺼내기
        if (board.getUser() != null) {
            this.writer = "user" + board.getUser().getId(); 
            // 👉 나중에 Users 엔티티에 name이나 nickname 필드 생기면 그걸로 교체
        } else {
            this.writer = "익명";
        }
    }
}
