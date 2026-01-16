package com.example.sideproject01.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.sideproject01.entity.Sound;

public interface SoundRepository extends JpaRepository<Sound, Integer> {

	// 소리 목록 조회(최신순 정렬)
	@Query("SELECT s FROM Sound s JOIN FETCH s.uploader ORDER BY s.createdAt DESC")
	public List<Sound> findAllWithUploader();

	// 소리 목록 조회(인기순 정렬)
	@Query("SELECT s FROM Sound s JOIN FETCH s.uploader ORDER BY COALESCE(s.playCount, 0) DESC")
	public List<Sound> findAllWithUploaderByPopularity();

	// 키워드 검색 (최신순)
	@Query("SELECT s FROM Sound s JOIN FETCH s.uploader WHERE LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY s.createdAt DESC")
	public List<Sound> findByKeywordLatest(@Param("keyword") String keyword);

	// 키워드 검색 (인기순)
	@Query("SELECT s FROM Sound s JOIN FETCH s.uploader WHERE LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY COALESCE(s.playCount, 0) DESC")
	public List<Sound> findByKeywordPopularity(@Param("keyword") String keyword);

	// 태그로 검색 (최신순)
	@Query("SELECT DISTINCT s FROM Sound s JOIN FETCH s.uploader JOIN SoundTag st ON st.soundId = s WHERE st.tagId.tagId IN :tagIds ORDER BY s.createdAt DESC")
	public List<Sound> findByTagIdsLatest(@Param("tagIds") List<Integer> tagIds);

	// 태그로 검색 (인기순)
	@Query("SELECT DISTINCT s FROM Sound s JOIN FETCH s.uploader JOIN SoundTag st ON st.soundId = s WHERE st.tagId.tagId IN :tagIds ORDER BY COALESCE(s.playCount, 0) DESC")
	public List<Sound> findByTagIdsPopularity(@Param("tagIds") List<Integer> tagIds);

	// 키워드 + 태그 검색 (최신순)
	@Query("SELECT DISTINCT s FROM Sound s JOIN FETCH s.uploader JOIN SoundTag st ON st.soundId = s WHERE LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%')) AND st.tagId.tagId IN :tagIds ORDER BY s.createdAt DESC")
	public List<Sound> findByKeywordAndTagIdsLatest(@Param("keyword") String keyword,
			@Param("tagIds") List<Integer> tagIds);

	// 키워드 + 태그 검색 (인기순)
	@Query("SELECT DISTINCT s FROM Sound s JOIN FETCH s.uploader JOIN SoundTag st ON st.soundId = s WHERE LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%')) AND st.tagId.tagId IN :tagIds ORDER BY COALESCE(s.playCount, 0) DESC")
	public List<Sound> findByKeywordAndTagIdsPopularity(@Param("keyword") String keyword,
			@Param("tagIds") List<Integer> tagIds);

	// 조회수 증가
	@Modifying
	@Query("UPDATE Sound s SET s.playCount = COALESCE(s.playCount, 0) + 1 WHERE s.soundId = :soundId")
	void incrementPlayCount(@Param("soundId") Integer soundId);

}
