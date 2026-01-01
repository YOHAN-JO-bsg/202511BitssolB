package com.example.sideproject01.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.sideproject01.entity.Sound;


public interface SoundRepository extends JpaRepository<Sound, Integer>{
	
	// 소리 목록 조회(최신순 정렬)
	@Query("SELECT s FROM Sound s JOIN FETCH s.uploader ORDER BY s.createdAt DESC")
    public List<Sound> findAllWithUploader();
	
	// 소리 목록 조회(인기순 정렬)
	@Query("SELECT s FROM Sound s JOIN FETCH s.uploader ORDER BY COALESCE(s.playCount, 0) DESC")
	public List<Sound> findAllWithUploaderByPopularity();
	
	// 조회수 증가
	@Modifying
	@Query("UPDATE Sound s SET s.playCount = COALESCE(s.playCount, 0) + 1 WHERE s.soundId = :soundId")
	void incrementPlayCount(@Param("soundId") Integer soundId);

}
