package com.example.sideproject01.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SoundUploadRequestDto {
	@NotBlank(message="제목은 필수 입니다")
	private String title;
	
	@NotBlank(message="설명은 필수 입니다")
	private String description;
}
