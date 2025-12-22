package com.example.sideproject01.service;

import com.example.sideproject01.dto.BoardDto;
import com.example.sideproject01.dto.BoardListResponse;
import com.example.sideproject01.entity.Board;
import com.example.sideproject01.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepo;
    private final LikesService likesService;
    private final VoteService voteService;

    /**
     * ✅ 게시글 목록 조회 (카테고리 + Oracle 11g 수동 페이징)
     */
    @Transactional(readOnly = true)
    @Override
    public BoardListResponse getBoardList(int pageNum, int pageSize, String category) {

        // Oracle 11g ROWNUM 페이징을 위한 startRow, endRow 계산
        int startRow = (pageNum - 1) * pageSize;
        int endRow = pageNum * pageSize;

        List<Board> boardList;
        long totalRow;

        // 카테고리 필터
        if ("all".equalsIgnoreCase(category)) {
            boardList = boardRepo.findAllWithPagination(startRow, endRow);
            totalRow = boardRepo.count();
        } else {
            boardList = boardRepo.findByCategoryWithPagination(category, startRow, endRow);
            totalRow = boardRepo.countByCategory(category);
        }

        // Entity → DTO 변환
        List<BoardDto> dtoList = boardList.stream()
                .map(BoardDto::new)
                .collect(Collectors.toList());

        // ✅ 페이지 정보 수동 계산
        int totalPageCount = (int) Math.ceil((double) totalRow / pageSize);
        int blockSize = 2; // ⭐ 한 번에 보여줄 페이지 수를 2로 설정
        int currentBlock = (int) Math.ceil((double) pageNum / blockSize);
        int startPageNum = (currentBlock - 1) * blockSize + 1;
        int endPageNum = Math.min(currentBlock * blockSize, totalPageCount);

        return BoardListResponse.builder()
                .list(dtoList)
                .pageNum(pageNum)
                .pageSize(pageSize)
                .totalRow(totalRow)
                .totalPageCount(totalPageCount)
                .startPageNum(startPageNum)
                .endPageNum(endPageNum)
                .category(category)
                .build();
    }


    /**
     * ✅ 게시글 등록
     */
    @Transactional
    @Override
    public Long addBoard(BoardDto dto) {
        Board entity = Board.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .category(dto.getCategory())
                .imageUrl(dto.getImageUrl())
                .build();

        Board saved = boardRepo.save(entity);

        // 투표 항목이 있다면 생성
        if (dto.getVoteOptionTexts() != null && !dto.getVoteOptionTexts().isEmpty()) {
            voteService.createVoteOptions(saved.getBoardId(), dto.getVoteOptionTexts());
        }

        return saved.getBoardId();
    }

    /**
     * ✅ 게시글 수정
     */
    @Transactional
    @Override
    public BoardDto updateBoard(Long id, BoardDto dto) {
        Board entity = boardRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("수정할 게시글이 존재하지 않습니다. id=" + id));

        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setCategory(dto.getCategory());
        entity.setImageUrl(dto.getImageUrl());

        return new BoardDto(entity); // 변경 감지 후 DTO로 반환
    }

    /**
     * ✅ 게시글 삭제
     */
    @Transactional
    @Override
    public String deleteBoard(Long id) {
        if (!boardRepo.existsById(id)) {
            throw new IllegalArgumentException("삭제할 게시글이 존재하지 않습니다. id=" + id);
        }
        boardRepo.deleteById(id);
        return "게시글이 성공적으로 삭제되었습니다.";
    }

    /**
     * ✅ 게시글 상세조회 (조회수 +1 포함)
     */
    @Transactional
    @Override
    public BoardDto getDetail(Long id, Long userId) { // userId 파라미터 추가
        Board entity = boardRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. id=" + id));

        entity.setViewCount(entity.getViewCount() + 1);
        BoardDto dto = new BoardDto(entity); // DTO 생성

        // LikesService를 사용하여 좋아요 정보 추가
        dto.setLikeCount(likesService.getLikeCount("BOARD", id));
        if (userId != null) { // userId가 제공된 경우에만 좋아요 여부 확인
            dto.setLikedByUser(likesService.isLikedByUser(userId, "BOARD", id));
        } else {
            dto.setLikedByUser(false); // userId가 없으면 좋아요 안 누른 것으로 처리
        }

        // VoteService를 사용하여 투표 정보 추가
        java.util.List<com.example.sideproject01.dto.VotesDto> voteOptions = voteService.getVoteOptionsWithResults(id, userId);
        dto.setVoteOptions(voteOptions);
        // 사용자가 해당 게시글의 투표에 참여했는지 여부 (어떤 항목이든 하나라도 선택했다면 true)
        dto.setVotedByUser(voteOptions.stream().anyMatch(com.example.sideproject01.dto.VotesDto::isSelectedByUser));

        return dto;
    }
}
