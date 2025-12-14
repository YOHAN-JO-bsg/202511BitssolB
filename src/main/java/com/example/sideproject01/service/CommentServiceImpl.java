package com.example.sideproject01.service;

import com.example.sideproject01.dto.CommentDto;
import com.example.sideproject01.dto.CommentListResponse;
import com.example.sideproject01.entity.Board;
import com.example.sideproject01.entity.Comments;
import com.example.sideproject01.entity.Users;
import com.example.sideproject01.repository.BoardRepository;
import com.example.sideproject01.repository.CommentsRepository;
import com.example.sideproject01.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentsRepository commentsRepository;
    private final BoardRepository boardRepository;
    private final UsersRepository usersRepository;
    private final LikesService likesService;

    // 게시글에 달린 댓글 목록 (페이징 처리 및 계층 구조 포함)
    @Override
    @Transactional(readOnly = true)
    public CommentListResponse getComments(Long boardId, int pageNum, int pageSize, Long userId) { // userId 파라미터 추가
        // boardId 에 해당하는 Board 엔티티를 찾거나 예외 발생
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new NoSuchElementException("게시글을 찾을 수 없습니다."));

        // Oracle 11g 수동 페이징 계산
        int startRow = (pageNum - 1) * pageSize;
        int endRow = pageNum * pageSize;
        int isHidden = 0;

        // 최상위 댓글 (parent가 null인 댓글)만 페이징 처리하여 조회
        long totalRowCount = commentsRepository.countRootCommentsByBoard(board.getBoardId(), isHidden);
        List<Comments> rootCommentsList = commentsRepository.findRootCommentsByBoardWithPagination(board.getBoardId(), isHidden, startRow, endRow);

        // 최상위 댓글 DTO로 변환 및 자식 댓글 계층 구조 구축
        List<CommentDto> rootCommentDtos = rootCommentsList.stream()
                .map(rootComment -> {
                    CommentDto rootDto = CommentDto.fromEntity(rootComment);
                    // 좋아요 정보 설정
                    rootDto.setLikeCount(likesService.getLikeCount("COMMENT", rootDto.getCommentId()));
                    if (userId != null) {
                        rootDto.setLikedByUser(likesService.isLikedByUser(userId, "COMMENT", rootDto.getCommentId()));
                    } else {
                        rootDto.setLikedByUser(false);
                    }
                    rootDto.setChildren(getChildrenComments(rootComment, userId)); // userId 전달
                    return rootDto;
                })
                .collect(Collectors.toList());
        
        // 페이지 정보 수동 계산
        int totalPageNum = (int) Math.ceil((double) totalRowCount / pageSize);

        // CommentListResponse 객체 구성
        return CommentListResponse.builder()
                .list(rootCommentDtos)
                .pageNum(pageNum)
                .startPageNum(1) // 페이지 블록 계산 로직은 일단 단순화
                .endPageNum(totalPageNum)
                .totalPageNum(totalPageNum)
                .build();
    }

    // 자식 댓글을 재귀적으로 가져오는 헬퍼 메소드
    private List<CommentDto> getChildrenComments(Comments parentComment, Long userId) { // userId 파라미터 추가
        // parentComment를 부모로 가지는 모든 댓글을 가져와서 정렬
        List<Comments> childrenEntities = commentsRepository.findByParentAndIsHiddenOrderByCreatedAtAsc(parentComment, 0);

        return childrenEntities.stream()
                .map(childComment -> {
                    CommentDto childDto = CommentDto.fromEntity(childComment);
                    // 좋아요 정보 설정
                    childDto.setLikeCount(likesService.getLikeCount("COMMENT", childDto.getCommentId()));
                    if (userId != null) {
                        childDto.setLikedByUser(likesService.isLikedByUser(userId, "COMMENT", childDto.getCommentId()));
                    } else {
                        childDto.setLikedByUser(false);
                    }
                    childDto.setChildren(getChildrenComments(childComment, userId)); // 재귀 호출 시 userId 전달
                    return childDto;
                })
                .collect(Collectors.toList());
    }

    // 댓글 저장 (원댓글 또는 대댓글)
    @Override
    @Transactional
    public void createComment(Long boardId, CommentDto dto) {
        // boardId 에 해당하는 Board 엔티티를 찾거나 예외 발생
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new NoSuchElementException("게시글을 찾을 수 없습니다."));

        Users user;
        org.springframework.security.core.Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // If user is properly authenticated (not anonymous)
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
            String username = authentication.getName();
            user = usersRepository.findByUsername(username)
                    .orElseThrow(() -> new NoSuchElementException("인증된 사용자를 찾을 수 없습니다: " + username));
        }
        // For testing via Swagger, fallback to userId from DTO
        else if (dto.getUserId() != null) {
            user = usersRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다. ID: " + dto.getUserId()));
        } else {
            throw new IllegalStateException("사용자 정보를 확인할 수 없습니다. 로그인이 필요하거나, 테스트 시 userId를 DTO에 포함해야 합니다.");
        }

        Comments parentComment = null;
        if (dto.getParentId() != null) {
            parentComment = commentsRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new NoSuchElementException("부모 댓글을 찾을 수 없습니다."));
        }

        Comments comment = Comments.builder()
                .content(dto.getContent())
                .board(board)
                .user(user)
                .parent(parentComment) // 부모 댓글 설정
                .isHidden(0) // 기본값 0
                .build();

        commentsRepository.save(comment);
    }

    // 댓글 수정
    @Override
    @Transactional
    public void updateComment(Long commentId, CommentDto dto, Long userId) {
        Comments comment = commentsRepository.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("댓글을 찾을 수 없습니다."));

        // 테스트용 작성자 권한 확인 (SecurityContextHolder 대신 userId 비교)
        if (!comment.getUser().getId().equals(userId)) {
            throw new RuntimeException("댓글 작성자만 수정할 수 있습니다.");
        }

        comment.setContent(dto.getContent());
        commentsRepository.save(comment); // 변경된 내용 저장
    }

    // 댓글 삭제 (soft delete)
    @Override
    @Transactional
    public void deleteComment(Long commentId, Long userId) { // <-- userId 파라미터 추가
        Comments comment = commentsRepository.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("댓글을 찾을 수 없습니다."));

        // 테스트용 작성자 권한 확인 (SecurityContextHolder 대신 userId 비교)
        if (!comment.getUser().getId().equals(userId)) { // <-- 전달받은 userId와 댓글 작성자 ID 비교
            throw new RuntimeException("댓글 작성자만 삭제할 수 있습니다.");
        }

        comment.setIsHidden(1); // isHidden 플래그를 1로 설정하여 soft delete 처리
        commentsRepository.save(comment);
    }
}

