package com.example.sideproject01.repository;

import com.example.sideproject01.entity.Likes;
import com.example.sideproject01.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LikesRepository extends JpaRepository<Likes, Long> {

    // 특정 사용자가 특정 타입의 대상(게시글/댓글)에 좋아요를 눌렀는지 확인
    Optional<Likes> findByUserAndTargetTypeAndTargetId(User user, String targetType, Long targetId);

    // 특정 타입의 대상(게시글/댓글)의 좋아요 총 개수 조회
    int countByTargetTypeAndTargetId(String targetType, Long targetId);
}