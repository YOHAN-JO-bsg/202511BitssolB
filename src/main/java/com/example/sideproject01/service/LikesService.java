package com.example.sideproject01.service;

import com.example.sideproject01.entity.Likes;
import com.example.sideproject01.entity.User;
import com.example.sideproject01.repository.LikesRepository;
import com.example.sideproject01.repository.UserRepository;
import com.example.sideproject01.repository.BoardRepository; // BoardRepository 추가
import com.example.sideproject01.repository.CommentsRepository; // CommentsRepository 추가 (댓글 좋아요를 위해)
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class LikesService {

    private final LikesRepository likesRepository;
    private final UserRepository usersRepository;
    private final BoardRepository boardRepository; // 게시글 좋아요 확인용
    private final CommentsRepository commentsRepository; // 댓글 좋아요 확인용

    /**
     * 좋아요 추가 또는 취소 (토글)
     * @param userId 좋아요를 누른 사용자 ID
     * @param targetType 대상 타입 ("BOARD", "COMMENT")
     * @param targetId 대상 ID (게시글 ID 또는 댓글 ID)
     * @return 좋아요 추가시 true, 좋아요 취소시 false
     */
    @Transactional
    public boolean toggleLike(Long userId, String targetType, Long targetId) {
        User user = usersRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다. ID: " + userId));

        // 대상 존재 여부 확인 (게시글 또는 댓글)
        if ("BOARD".equals(targetType)) {
            boardRepository.findById(targetId)
                    .orElseThrow(() -> new NoSuchElementException("게시글을 찾을 수 없습니다. ID: " + targetId));
        } else if ("COMMENT".equals(targetType)) {
            commentsRepository.findById(targetId)
                    .orElseThrow(() -> new NoSuchElementException("댓글을 찾을 수 없습니다. ID: " + targetId));
        } else {
            throw new IllegalArgumentException("유효하지 않은 좋아요 대상 타입입니다: " + targetType);
        }


        Optional<Likes> existingLike = likesRepository.findByUserAndTargetTypeAndTargetId(user, targetType, targetId);

        if (existingLike.isPresent()) {
            // 이미 좋아요를 눌렀다면 취소
            likesRepository.delete(existingLike.get());
            return false; // 좋아요 취소됨
        } else {
            // 좋아요를 누르지 않았다면 추가
            Likes like = Likes.builder()
                    .user(user)
                    .targetType(targetType)
                    .targetId(targetId)
                    .build();
            likesRepository.save(like);
            return true; // 좋아요 추가됨
        }
    }

    /**
     * 특정 대상의 좋아요 개수 조회
     * @param targetType 대상 타입 ("BOARD", "COMMENT")
     * @param targetId 대상 ID
     * @return 좋아요 개수
     */
    @Transactional(readOnly = true)
    public int getLikeCount(String targetType, Long targetId) {
        return likesRepository.countByTargetTypeAndTargetId(targetType, targetId);
    }

    /**
     * 특정 사용자가 특정 대상에 좋아요를 눌렀는지 여부 확인
     * @param userId 사용자 ID
     * @param targetType 대상 타입 ("BOARD", "COMMENT")
     * @param targetId 대상 ID
     * @return 좋아요를 눌렀으면 true, 아니면 false
     */
    @Transactional(readOnly = true)
    public boolean isLikedByUser(Long userId, String targetType, Long targetId) {
        User user = usersRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다. ID: " + userId));
        return likesRepository.findByUserAndTargetTypeAndTargetId(user, targetType, targetId).isPresent();
    }
}
