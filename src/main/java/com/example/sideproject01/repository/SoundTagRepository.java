package com.example.sideproject01.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.sideproject01.entity.SoundTag;
import java.util.List;

public interface SoundTagRepository extends JpaRepository<SoundTag, Integer> {
    // 특정 Sound의 태그들 조회
    List<SoundTag> findBySoundId_SoundId(Integer soundId);
}