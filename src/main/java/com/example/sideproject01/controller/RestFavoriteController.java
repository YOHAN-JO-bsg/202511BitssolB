package com.example.sideproject01.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.sideproject01.dto.SoundDto;
import com.example.sideproject01.service.FavoriteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class RestFavoriteController {

    private final FavoriteService favoriteService;

    // 즐겨찾기 추가
    @PostMapping("/sounds/{soundId}/favorite")
    public ResponseEntity<?> addFavorite(@PathVariable Integer soundId) {
        favoriteService.addFavorite(soundId);
        return ResponseEntity.ok().build();
    }

    // 즐겨찾기 해제
    @DeleteMapping("/sounds/{soundId}/favorite")
    public ResponseEntity<?> removeFavorite(@PathVariable Integer soundId) {
        favoriteService.removeFavorite(soundId);
        return ResponseEntity.ok().build();
    }

    // 내 즐겨찾기 목록
    @GetMapping("/favorites")
    public List<SoundDto> getMyFavorites() {
        return favoriteService.getMyFavorites();
    }

    // 이미 즐겨찾기 했는지 체크
    @GetMapping("/sounds/{soundId}/favorite")
    public boolean isFavorite(@PathVariable Integer soundId) {
        return favoriteService.isFavorite(soundId);
    }
}