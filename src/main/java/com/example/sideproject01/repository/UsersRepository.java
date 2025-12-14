package com.example.sideproject01.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.sideproject01.entity.Users; // ✅ 엔티티 import 꼭 필요!
import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByUsername(String username); // 이 부분을 추가해야 합니다.
}
