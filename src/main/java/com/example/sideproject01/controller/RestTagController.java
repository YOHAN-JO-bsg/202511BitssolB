package com.example.sideproject01.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sideproject01.entity.Tag;
import com.example.sideproject01.repository.TagRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class RestTagController {
    
    private final TagRepository tagRepo;
    
    @GetMapping("/tags")
    public List<Tag> getAllTags() {
        return tagRepo.findAll();
    }
}