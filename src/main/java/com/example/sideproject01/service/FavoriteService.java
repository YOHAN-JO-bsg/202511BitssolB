package com.example.sideproject01.service;

import java.util.List;
import com.example.sideproject01.dto.SoundDto;

public interface FavoriteService {
    void addFavorite(Integer soundId);
    void removeFavorite(Integer soundId);
    List<SoundDto> getMyFavorites();
    boolean isFavorite(Integer soundId);
}