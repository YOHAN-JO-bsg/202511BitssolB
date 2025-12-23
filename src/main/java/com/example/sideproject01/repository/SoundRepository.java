package com.example.sideproject01.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.sideproject01.entity.Sound;


public interface SoundRepository extends JpaRepository<Sound, Integer>{
	
	@Query("SELECT s FROM Sound s JOIN FETCH s.uploader ORDER BY s.createdAt DESC")
    public List<Sound> findAllWithUploader();

}
