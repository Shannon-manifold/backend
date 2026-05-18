package com.shannonmanifold.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 블로그 관련 API를 처리하는 컨트롤러
 * 담당자: 홍성욱
 */
@RestController
@RequestMapping("/api/v1/blogs")
public class BlogController {

    @GetMapping
    public ResponseEntity<?> getBlogs() {
        return ResponseEntity.ok("블로그 목록 조회 성공");
    }

    @GetMapping("/{postId}")
    public ResponseEntity<?> getBlog(@PathVariable Long postId) {
        return ResponseEntity.ok("블로그 상세 조회 성공");
    }

    @PostMapping
    public ResponseEntity<?> createBlog() {
        return ResponseEntity.ok("블로그 작성 성공");
    }

    @PutMapping("/{postId}")
    public ResponseEntity<?> updateBlog(@PathVariable Long postId) {
        return ResponseEntity.ok("블로그 수정 성공");
    }
}
