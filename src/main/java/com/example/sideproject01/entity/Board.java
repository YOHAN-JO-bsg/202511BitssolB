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
    // 💡 1. ORA-02000 해결: SEQUENCE 전략 사용 (ID는 DB 시퀀스 사용)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BOARD_SEQ_GENERATOR")
    @SequenceGenerator(
            name = "BOARD_SEQ_GENERATOR",
            sequenceName = "BOARD_SEQ", // DB에 생성된 시퀀스 이름
            allocationSize = 1
    )
    @Column(name = "board_id")
    private Long boardId;

    @Column(nullable = false, length = 100)
    private String title;

    // 💡 2. 테이블 생성 실패 해결: Oracle 11g는 TEXT 대신 CLOB을 사용해야 합니다.
    @Lob // Hibernate에게 이 필드를 CLOB 타입으로 매핑하도록 지시
    @Column(nullable = false)
    private String content; // columnDefinition="TEXT" 제거됨

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