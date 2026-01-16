package com.example.sideproject01.service;

import com.example.sideproject01.dto.BoardDto;
import com.example.sideproject01.dto.BoardListResponse;
import com.example.sideproject01.dto.VotesDto;
import com.example.sideproject01.entity.Board;
import com.example.sideproject01.entity.User;
import com.example.sideproject01.repository.BoardRepository;
import com.example.sideproject01.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepo;
    private final UserRepository userRepo;
    private final LikesService likesService;
    private final VoteService voteService;
    private final CommentService commentService;

    // 게시글 목록 조회
    @Transactional(readOnly = true)
    @Override
    public BoardListResponse getBoardList(int pageNum, int pageSize, String category) {

        // Oracle 11g ROWNUM 페이징을 위한 startRow, endRow 계산
        int startRow = (pageNum - 1) * pageSize;
        int endRow = pageNum * pageSize;

        List<Board> boardList;
        long totalRow;

        if (category == null || category.trim().isEmpty() || "all".equalsIgnoreCase(category)) {
            boardList = boardRepo.findAllWithPagination(startRow, endRow);
            totalRow = boardRepo.count();
            category = "all";
        } else {
            boardList = boardRepo.findByCategoryWithPagination(category.trim(), startRow, endRow);
            totalRow = boardRepo.countByCategory(category.trim());
        }

        // Entity → DTO 변환
        List<BoardDto> dtoList = boardList.stream()
                .map(BoardDto::new)
                .collect(Collectors.toList());

        // 페이지 정보 수동 계산
        int totalPageCount = (int) Math.ceil((double) totalRow / pageSize);
        int blockSize = 2;
        int currentBlock = (int) Math.ceil((double) pageNum / blockSize);
        int startPageNum = (currentBlock - 1) * blockSize + 1;
        int endPageNum = Math.min(currentBlock * blockSize, totalPageCount);

        return BoardListResponse.builder()
                .boards(dtoList)
                .currentPage(pageNum)
                .pageSize(pageSize)
                .totalElements(totalRow)
                .totalPages(totalPageCount)
                .startPageNum(startPageNum)
                .endPageNum(endPageNum)
                .category(category)
                .build();
    }

    /**
     * ✅ 게시글 등록 (변경)
     */
    @Transactional
    @Override
    public Long addBoard(BoardDto dto, Long userId) { // userId 파라미터 추가
        // userId로 User 엔티티 조회
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. id=" + userId));

        Board entity = Board.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .category(dto.getCategory())
                .imageUrl(dto.getImageUrl())
                .user(user) // <-- 작성자 정보 설정
                .build();

        Board saved = boardRepo.save(entity);

        // 투표 항목이 있다면 생성
        if (dto.getVoteOptionTexts() != null && !dto.getVoteOptionTexts().isEmpty()) {
            voteService.createVoteOptions(saved.getBoardId(), dto.getVoteOptionTexts());
        }

        return saved.getBoardId();
    }

    /**
     * ✅ 게시글 수정 (변경)
     */
    @Transactional
    @Override
    public BoardDto updateBoard(Long id, BoardDto dto, Long userId) { // userId 파라미터 추가
        Board entity = boardRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("수정할 게시글이 존재하지 않습니다. id=" + id));

        // 권한 확인
        if (entity.getUser() == null || !entity.getUser().getId().equals(userId)) {
            throw new SecurityException("수정 권한이 없습니다.");
        }

        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setCategory(dto.getCategory());
        entity.setImageUrl(dto.getImageUrl());

        return new BoardDto(entity); // 변경 감지 후 DTO로 반환
    }

    /**
     * ✅ 게시글 삭제 (변경)
     */
    @Transactional
    @Override
    public String deleteBoard(Long id, Long userId) {

        Board entity = boardRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("삭제할 게시글이 없습니다."));

        if (!entity.getUser().getId().equals(userId)) {
            throw new SecurityException("삭제 권한이 없습니다.");
        }

        // 1️⃣ 댓글 & 대댓글 삭제
        commentService.deleteByBoardId(id);

        // 2️⃣ 좋아요 삭제
        likesService.deleteByBoardId(id);

        // 3️⃣ 투표 삭제
        if ("vote".equals(entity.getCategory())) {
            voteService.deleteVotesByBoard(id);
        }

        // 4️⃣ 마지막에 게시글 삭제
        boardRepo.delete(entity);

        return "게시글이 성공적으로 삭제되었습니다.";
    }



    /**
     * ✅ 게시글 상세조회 (조회수 +1 포함)
     */
    @Transactional
    @Override
    public BoardDto getDetail(Long id, Long userId) {

        Board entity = boardRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. id=" + id));

        entity.setViewCount(entity.getViewCount() + 1);
        BoardDto dto = new BoardDto(entity);

        // ❤️ 좋아요
        dto.setLikeCount(likesService.getLikeCount("BOARD", id));
        if (userId != null) {
            dto.setLikedByUser(likesService.isLikedByUser(userId, "BOARD", id));
        } else {
            dto.setLikedByUser(false);
        }

        // 🗳 투표
        List<VotesDto> voteOptions = voteService.getVoteOptionsWithResults(id, userId);
        dto.setVoteOptions(voteOptions);
        dto.setVotedByUser(
                voteOptions.stream().anyMatch(VotesDto::isSelectedByUser)
        );

        // ✅🔥 댓글 수 (이 줄만 추가)
        long commentCount = commentService.getCommentCount(id);
        dto.setCommentCount(commentCount);

        return dto;
    }

}