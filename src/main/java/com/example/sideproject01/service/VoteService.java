package com.example.sideproject01.service;

import com.example.sideproject01.dto.VotesDto;
import com.example.sideproject01.entity.Board;
import com.example.sideproject01.entity.Users;
import com.example.sideproject01.entity.VoteResults;
import com.example.sideproject01.entity.Votes;
import com.example.sideproject01.repository.BoardRepository;
import com.example.sideproject01.repository.UsersRepository;
import com.example.sideproject01.repository.VoteResultsRepository;
import com.example.sideproject01.repository.VotesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class VoteService {

    private final VotesRepository votesRepository;
    private final VoteResultsRepository voteResultsRepository;
    private final UsersRepository usersRepository;
    private final BoardRepository boardRepository;

    /**
     * 투표 항목들 생성 (게시글 생성 시 함께 호출)
     * @param boardId 투표가 속할 게시글 ID
     * @param optionTexts 투표 항목 텍스트 목록
     * @return 생성된 투표 항목 DTO 목록
     */
    @Transactional
    public List<VotesDto> createVoteOptions(Long boardId, List<String> optionTexts) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new NoSuchElementException("게시글을 찾을 수 없습니다. ID: " + boardId));

        if (optionTexts == null || optionTexts.isEmpty()) {
            throw new IllegalArgumentException("투표 항목은 최소 하나 이상이어야 합니다.");
        }

        List<Votes> createdVotes = optionTexts.stream()
                .map(text -> Votes.builder()
                        .board(board)
                        .optionText(text)
                        .build())
                .collect(Collectors.toList());

        return votesRepository.saveAll(createdVotes).stream()
                .map(VotesDto::fromEntity)
                .collect(Collectors.toList());
    }


    /**
     * 사용자가 투표 항목에 투표
     * @param userId 투표하는 사용자 ID
     * @param optionId 투표할 항목 ID
     * @return 투표 결과 ID
     */
    @Transactional
    public Long castVote(Long userId, Long optionId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다. ID: " + userId));

        Votes voteOption = votesRepository.findById(optionId)
                .orElseThrow(() -> new NoSuchElementException("투표 항목을 찾을 수 없습니다. ID: " + optionId));

        Board board = voteOption.getBoard(); // 투표 항목이 속한 게시글

        // 이미 해당 게시글의 투표에 참여했는지 확인 (중복 투표 방지)
        if (voteResultsRepository.existsByUserAndBoard(user, board)) {
            throw new IllegalStateException("사용자는 이 게시글의 투표에 이미 참여했습니다.");
        }

        VoteResults voteResult = VoteResults.builder()
                .user(user)
                .votes(voteOption)
                .build();

        return voteResultsRepository.save(voteResult).getResultId();
    }

    /**
     * 특정 게시글의 투표 항목들과 결과 조회
     * @param boardId 게시글 ID
     * @param userId 조회하는 사용자 ID (선택 사항)
     * @return 투표 항목 DTO 목록 (투표 수 및 사용자 투표 여부 포함)
     */
    @Transactional(readOnly = true)
    public List<VotesDto> getVoteOptionsWithResults(Long boardId, Long userId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new NoSuchElementException("게시글을 찾을 수 없습니다. ID: " + boardId));

        List<Votes> options = votesRepository.findByBoard(board);

        Users user = (userId != null) ? usersRepository.findById(userId).orElse(null) : null;

        return options.stream().map(option -> {
            VotesDto dto = VotesDto.fromEntity(option);
            dto.setVoteCount(voteResultsRepository.countByVotes(option)); // 각 항목의 투표 수 설정

            // 현재 사용자가 이 항목에 투표했는지 여부 확인
            if (user != null) {
                // 특정 사용자가 해당 게시글의 투표에 참여했고, 그 항목이 현재 항목인지 확인해야 함
                Optional<VoteResults> userVote = voteResultsRepository.findByUserAndVotes(user, option);
                dto.setSelectedByUser(userVote.isPresent());
            } else {
                dto.setSelectedByUser(false);
            }
            return dto;
        }).collect(Collectors.toList());
    }
}
