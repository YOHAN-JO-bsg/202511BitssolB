package com.example.sideproject01.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SoundResponseDto {
	private Integer soundId;
    private String title;
    private String thumbnailUrl;
    private Integer playCount;
}
