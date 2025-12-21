package com.example.sideproject01.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
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
public class PlayHistory {
	
	@Id
	private Integer historyId;
	
	@ManyToOne
	@JoinColumn(name="user_id", referencedColumnName="id")
	private User userId;
	
	@ManyToOne
	@JoinColumn(name="sound_id", referencedColumnName="sound_id")
	private Sound soundId;
	
	private LocalDateTime playedAt;
}
