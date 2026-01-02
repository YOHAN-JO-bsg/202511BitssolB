package com.example.sideproject01.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.sideproject01.entity.Tag;
import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Integer> {
    // 전체 태그 목록 조회용
    List<Tag> findAll();
}