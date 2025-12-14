package com.example.sideproject01.dto;

import com.example.sideproject01.entity.Board;
import com.example.sideproject01.entity.Comments;
import com.example.sideproject01.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CommentDto {

    private Long commentId;
    private Long userId;
    private Long boardId;
    private String content;
    private String writer;
    private int isHidden;
    private LocalDateTime createdAt;
    private Long parentId;
    private List<CommentDto> children = new ArrayList<>();

    // 좋아요 정보
    private int likeCount;
    private boolean likedByUser;

    /**
     * Entity -> DTO 변환 (JPA에서 사용)
     */
    public static CommentDto fromEntity(Comments entity) {
        return CommentDto.builder()
                .commentId(entity.getCommentsId())
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .boardId(entity.getBoard() != null ? entity.getBoard().getBoardId() : null)
                .parentId(entity.getParent() != null ? entity.getParent().getCommentsId() : null)
                .content(entity.getContent())
                .writer(entity.getUser() != null ? entity.getUser().getUsername() : "익명")
                .isHidden(entity.getIsHidden())
                .createdAt(entity.getCreatedAt())
                .children(new ArrayList<>()) // 초기화
                .build();
    }

    /**
     * DTO -> Entity 변환 (JPA에서 사용)
     */
    public Comments toEntity() {
        return Comments.builder()
                .commentsId(this.commentId)
                .user(this.userId != null ? Users.builder().id(this.userId).build() : null)
                .board(this.boardId != null ? Board.builder().boardId(this.boardId).build() : null)
                .parent(this.parentId != null ? Comments.builder().commentsId(this.parentId).build() : null)
                .content(this.content)
                .isHidden(this.isHidden)
                .build();
    }
}