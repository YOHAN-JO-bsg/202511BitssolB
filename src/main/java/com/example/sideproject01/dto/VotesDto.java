package com.example.sideproject01.dto;

import java.time.LocalDateTime;

import com.example.sideproject01.entity.Votes;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VotesDto {

    private Long voteId;           // 투표 항목 PK
    private Long boardId;          // 게시글 FK (Board의 board_id)
    private String optionText;     // 투표 항목 내용
    private LocalDateTime createdAt; // 작성일
    private int voteCount;          // 투표 개수
    private boolean selectedByUser; // 현재 사용자가 투표했는지 여부
    
    public static VotesDto fromEntity(Votes entity) {
        return VotesDto.builder()
                .voteId(entity.getVoteId())
                .boardId(entity.getBoard().getBoardId())
                .optionText(entity.getOptionText())
                .createdAt(entity.getCreatedAt())
                .build();
    }

}
