package com.example.sideproject01.dto;


import com.example.sideproject01.entity.User;
import com.example.sideproject01.entity.UserRole;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder // 빌더 패턴 사용
public class UserResponseDto {

    private String name;

    // Entity -> DTO 변환 메소드 (스태틱 팩토리 메소드 패턴)
    public static UserResponseDto from(User user) {
        return UserResponseDto.builder()
                .name(user.getUserName())
                .build();
    }
}