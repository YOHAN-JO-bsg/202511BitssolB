package com.example.sideproject01.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sideproject01.dto.UserLoginRequestDto;
import com.example.sideproject01.dto.UserResponseDto;
import com.example.sideproject01.dto.UserSignupRequestDto;
import com.example.sideproject01.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody UserSignupRequestDto requestDto) {
        userService.signup(requestDto);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserLoginRequestDto requestDto) {
        String token = userService.login(requestDto);
        // 토큰을 헤더에 넣어서 줄 수도 있고, 바디에 넣어서 줄 수도 있음 (여기선 바디)
        return ResponseEntity.ok(token);
    }
    
    // 내 정보 조회
    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getMyInfo(Principal principal) {
        // principal.getName() 안에는 토큰에서 뽑아낸 "이메일(아이디)"이 들어있음
        UserResponseDto myInfo = userService.getMyInfo(principal.getName());
        
        return ResponseEntity.ok(myInfo);
    }
}