package com.example.sideproject01.dto;

import java.time.LocalDateTime;

import com.example.sideproject01.entity.VoteResults;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteResultsDto {

    private Long resultId;       // 투표 결과 PK
    private Long voteId;         // 투표 항목 FK (Votes의 vote_id)
    private Long userId;         // 투표한 사용자 FK (Users의 id)
    private LocalDateTime createdAt; // 투표일
    
    public static VoteResultsDto fromEntity(VoteResults entity) {
        return VoteResultsDto.builder()
                .resultId(entity.getResultId())
                .voteId(entity.getVotes().getVoteId())
                .userId(entity.getUser().getId())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    
    
}




