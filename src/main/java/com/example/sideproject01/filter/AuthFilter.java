package com.example.sideproject01.filter;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/*
 *  1. OncePerRequestFilter 추상 클래스를 상속 받는다.
 *  2. @Component 어노테이션을 붙여서 bean 으로 만든다.
 *  
 *  이렇게 하면 spring boot 서버로 들어오는 모든 요청이 Controller 로 도달하기 전에 이 필터를 통과하게 된다.
 */
@Component
public class AuthFilter extends OncePerRequestFilter {

   //필터가 수행될 때 호출되는 메소드
   @Override
   protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
         throws ServletException, IOException {
      
      //요청의 Header 에 "Authorization" 이라는 키값으로 전달된 문자열이 있는지 읽어와 본다.
      String authHeader=request.getHeader("Authorization");
      System.out.println(authHeader);
      
      //요청의 흐름 계속 이어가기
      filterChain.doFilter(request, response);
   }

}
