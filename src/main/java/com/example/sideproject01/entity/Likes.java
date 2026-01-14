package com.example.sideproject01.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "likes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "target_type", "target_id"})
})

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Likes {

    // 좋아요 PK
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "likes_seq_gen")
    @SequenceGenerator(name = "likes_seq_gen", sequenceName = "likes_seq", allocationSize = 1)
    @Column(name = "like_id")
    private Long likeId;

    // 누른 사용자 FK (users.id)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    // 대상 타입 (POST / COMMENT)
    @Column(name = "target_type", length = 20)
    private String targetType;

    // 대상 ID (게시글이나 댓글 ID)
    @Column(name = "target_id")
    private Long targetId;

    // 좋아요 일자
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
