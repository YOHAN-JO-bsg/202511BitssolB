package com.example.sideproject01.service;

import com.example.sideproject01.dto.CommentDto;
import com.example.sideproject01.dto.CommentListResponse;
import com.example.sideproject01.entity.Board;
import com.example.sideproject01.entity.Comments;
import com.example.sideproject01.entity.User;
import com.example.sideproject01.repository.BoardRepository;
import com.example.sideproject01.repository.CommentsRepository;
import com.example.sideproject01.repository.UserRepository;
import com.example.sideproject01.service.LikesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

        private final CommentsRepository commentsRepository;
        private final BoardRepository boardRepository;
        private final UserRepository userRepository; // ✅ UsersRepository → UserRepository
        private final LikesService likesService;

        // 게시글에 달린 댓글 목록 (페이징 + 계층)
        @Override
        @Transactional(readOnly = true)
        public CommentListResponse getComments(Long boardId, int pageNum, int pageSize, Long userId) {

                Board board = boardRepository.findById(boardId)
                                .orElseThrow(() -> new NoSuchElementException("게시글을 찾을 수 없습니다."));

                int startRow = (pageNum - 1) * pageSize;
                int endRow = pageNum * pageSize;
                int isHidden = 0;

                long totalRowCount = commentsRepository.countRootCommentsByBoard(board.getBoardId(), isHidden);

                List<Comments> rootComments = commentsRepository.findRootCommentsByBoardWithPagination(
                                board.getBoardId(), isHidden, startRow, endRow);

                List<CommentDto> rootDtos = rootComments.stream()
                                .map(comment -> {
                                        CommentDto dto = CommentDto.fromEntity(comment);
                                        dto.setLikeCount(likesService.getLikeCount("COMMENT", dto.getCommentId()));
                                        dto.setLikedByUser(userId != null &&
                                                        likesService.isLikedByUser(userId, "COMMENT",
                                                                        dto.getCommentId()));
                                        dto.setChildren(getChildrenComments(comment, userId));
                                        return dto;
                                })
                                .toList();

                int totalPageNum = (int) Math.ceil((double) totalRowCount / pageSize);

                return CommentListResponse.builder()
                                .comments(rootDtos)
                                .currentPage(pageNum)
                                .totalPages(totalPageNum)
                                .totalElements(totalRowCount)
                                .startPageNum(1)
                                .endPageNum(totalPageNum)
                                .build();
        }

        // 자식 댓글 재귀
        private List<CommentDto> getChildrenComments(Comments parent, Long userId) {
                return commentsRepository
                                .findByParentAndIsHiddenOrderByCreatedAtAsc(parent, 0)
                                .stream()
                                .map(child -> {
                                        CommentDto dto = CommentDto.fromEntity(child);
                                        dto.setLikeCount(likesService.getLikeCount("COMMENT", dto.getCommentId()));
                                        dto.setLikedByUser(userId != null &&
                                                        likesService.isLikedByUser(userId, "COMMENT",
                                                                        dto.getCommentId()));
                                        dto.setChildren(getChildrenComments(child, userId));
                                        return dto;
                                })
                                .toList();
        }

        // ✅ 댓글 생성 (원댓글 / 대댓글)
        @Override
        @Transactional
        public void createComment(Long boardId, CommentDto dto, Long userId) {

                Board board = boardRepository.findById(boardId)
                                .orElseThrow(() -> new NoSuchElementException("게시글을 찾을 수 없습니다."));

                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));

                Comments parent = null;
                if (dto.getParentId() != null) {
                        parent = commentsRepository.findById(dto.getParentId())
                                        .orElseThrow(() -> new NoSuchElementException("부모 댓글을 찾을 수 없습니다."));
                }

                Comments comment = Comments.builder()
                                .content(dto.getContent())
                                .board(board)
                                .user(user)
                                .parent(parent)
                                .isHidden(0)
                                .build();

                commentsRepository.save(comment);
        }

        // 댓글 수정
        @Override
        @Transactional
        public void updateComment(Long commentId, CommentDto dto, Long userId) {

                Comments comment = commentsRepository.findById(commentId)
                                .orElseThrow(() -> new NoSuchElementException("댓글을 찾을 수 없습니다."));

                if (!comment.getUser().getId().equals(userId)) {
                        throw new RuntimeException("댓글 작성자만 수정할 수 있습니다.");
                }

                comment.setContent(dto.getContent());
        }

        // 댓글 삭제 (soft delete)
        @Override
        @Transactional
        public void deleteComment(Long commentId, Long userId) {

                Comments comment = commentsRepository.findById(commentId)
                                .orElseThrow(() -> new NoSuchElementException("댓글을 찾을 수 없습니다."));

                if (!comment.getUser().getId().equals(userId)) {
                        throw new RuntimeException("댓글 작성자만 삭제할 수 있습니다.");
                }

                comment.setIsHidden(1);
        }

    @Override
    public long getCommentCount(Long boardId) {
        return commentsRepository.countAllCommentsByBoard(boardId);
    }

    @Override
    @Transactional
    public void deleteByBoardId(Long boardId) {
        commentsRepository.deleteByBoardId(boardId);
    }

}
