package com.example.sideproject01.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.sideproject01.dto.SoundDto;
import com.example.sideproject01.dto.SoundUploadRequestDto;

public interface SoundService {
	public List<SoundDto> getAll(String sortBy, String keyword, List<Integer> tagIds);

	public SoundDto saveSound(SoundUploadRequestDto requestDto, MultipartFile soundFile, MultipartFile thumbnailFile);

	public SoundDto getSoundById(Integer soundId);

	public String getSoundFileUrl(Integer soundId);

	public void incrementPlayCount(Integer soundId);
}
