package com.example.sideproject01.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@Entity
public class Sound {

	@Id // sound_id 을 PK 로 설정
	@GeneratedValue(strategy = GenerationType.AUTO) // 시퀀스를 자동으로 만들어줌
	@Column(name = "sound_id")
	private Integer soundId;
	
	@ManyToOne
	@JoinColumn(name="uploader_id", referencedColumnName="id")
	private User uploader;
	
	private String title;
	private String description;
	private String fileUrl;
	private String thumbnailUrl;
	private Integer playCount;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
