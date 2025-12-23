package com.example.sideproject01.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vote_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteResults {

    // 투표 결과 PK
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vote_results_seq_gen")
    @SequenceGenerator(name = "vote_results_seq_gen", sequenceName = "vote_results_seq", allocationSize = 1)
    @Column(name = "result_id")
    private Long resultId;

    // 투표 항목 FK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_id", referencedColumnName = "vote_id", nullable = false)
    private Votes votes;

    // 투표한 사용자 FK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    // 투표일
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // ⭐ 투표가 속한 게시글 (중복 투표 체크용)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;
}
