package com.example.sideproject01.dto;

import java.time.LocalDateTime;

import com.example.sideproject01.entity.Likes;
import com.example.sideproject01.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikesDto {

    private Long likeId;         // 좋아요 PK
    private Long userId;         // 누른 사용자 FK
    private String targetType;   // 대상 타입 (POST / COMMENT)
    private Long targetId;       // 대상 ID (게시글 or 댓글)
    private LocalDateTime createdAt; // 좋아요 일자

    // ✅ Entity → DTO 변환
    public static LikesDto toDto(Likes entity) {
        return LikesDto.builder()
                .likeId(entity.getLikeId())
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .targetType(entity.getTargetType())
                .targetId(entity.getTargetId())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    // ✅ DTO → Entity 변환
    public Likes toEntity() {
        return Likes.builder()
                .likeId(this.likeId)
                .user(this.userId != null ? User.builder().id(this.userId).build() : null)
                .targetType(this.targetType)
                .targetId(this.targetId)
                .createdAt(this.createdAt)
                .build();
    }
}
