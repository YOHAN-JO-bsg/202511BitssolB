package com.example.sideproject01.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.sideproject01.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserName(String username);
    
    // 이메일 중복 검사 (회원가입용) - 있으면 true, 없으면 false 반환
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.userName = :userName")
    boolean existsByUserName(@Param("userName") String userName);
}
