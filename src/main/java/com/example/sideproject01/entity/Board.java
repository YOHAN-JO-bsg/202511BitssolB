package com.example.sideproject01.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BOARD_SEQ_GENERATOR")
    @SequenceGenerator(
            name = "BOARD_SEQ_GENERATOR",
            sequenceName = "BOARD_SEQ",
            allocationSize = 1
    )

    @Column(name = "board_id")
    private Long boardId;

    @Column(nullable = false, length = 100)
    private String title;

    @Lob // CLOB 타입
    @Column(nullable = false)
    private String content;

    @Column(length = 50)
    private String category;

    @Column(columnDefinition = "integer default 0")
    private int viewCount = 0;

    private String imageUrl;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}