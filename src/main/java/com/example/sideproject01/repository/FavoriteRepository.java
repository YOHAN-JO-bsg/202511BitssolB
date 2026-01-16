package com.example.sideproject01.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.sideproject01.entity.Favorite;
import com.example.sideproject01.entity.Sound;
import com.example.sideproject01.entity.User;

public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {
    List<Favorite> findByUserIdOrderByCreatedAtDesc(User userId);
    Optional<Favorite> findByUserIdAndSoundId(User userId, Sound soundId);
    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM Favorite f WHERE f.userId = :userId AND f.soundId = :soundId")
    boolean existsByUserIdAndSoundId(@Param("userId") User userId, @Param("soundId") Sound soundId);
    void deleteByUserIdAndSoundId(User userId, Sound soundId);
}
