package com.example.sideproject01.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "votes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Votes {

    // 투표 항목 PK
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "votes_seq_gen")
    @SequenceGenerator(name = "votes_seq_gen", sequenceName = "votes_seq", allocationSize = 1)
    @Column(name = "vote_id")
    private Long voteId;

    // 게시글 FK (Board)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", referencedColumnName = "board_id", nullable = false)
    private Board board;  // 하나의 게시글에 여러 투표 항목 가능

    // 투표 항목 내용
    @Column(name = "option_text", nullable = false, length = 255)
    private String optionText;

    // 작성일
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
