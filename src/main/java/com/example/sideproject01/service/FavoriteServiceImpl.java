package com.example.sideproject01.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.sideproject01.dto.SoundDto;
import com.example.sideproject01.entity.Favorite;
import com.example.sideproject01.entity.Sound;
import com.example.sideproject01.entity.User;
import com.example.sideproject01.repository.FavoriteRepository;
import com.example.sideproject01.repository.SoundRepository;
import com.example.sideproject01.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepo;
    private final UserRepository userRepo;
    private final SoundRepository soundRepo;

    private User getCurrentUser() {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByUserName(userName)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
    }

    @Override
    @Transactional
    public void addFavorite(Integer soundId) {
        User user = getCurrentUser();
        Sound sound = soundRepo.findById(soundId)
                .orElseThrow(() -> new RuntimeException("소리를 찾을 수 없습니다."));

        if (favoriteRepo.existsByUserIdAndSoundId(user, sound)) {
            throw new RuntimeException("이미 즐겨찾기에 추가되어 있습니다.");
        }

        Favorite favorite = Favorite.builder()
                .userId(user)
                .soundId(sound)
                .createdAt(LocalDateTime.now())
                .build();

        favoriteRepo.save(favorite);
    }

    @Override
    @Transactional
    public void removeFavorite(Integer soundId) {
        User user = getCurrentUser();
        Sound sound = soundRepo.findById(soundId)
                .orElseThrow(() -> new RuntimeException("소리를 찾을 수 없습니다."));

        favoriteRepo.deleteByUserIdAndSoundId(user, sound);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SoundDto> getMyFavorites() {
        User user = getCurrentUser();
        List<Favorite> favorites = favoriteRepo.findByUserIdOrderByCreatedAtDesc(user);

        return favorites.stream()
                .map(fav -> SoundDto.toDto(fav.getSoundId(), fav.getSoundId().getUploader()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFavorite(Integer soundId) {
        User user = getCurrentUser();
        Sound sound = soundRepo.findById(soundId).orElse(null);
        if (sound == null) return false;
        return favoriteRepo.existsByUserIdAndSoundId(user, sound);
    }
}