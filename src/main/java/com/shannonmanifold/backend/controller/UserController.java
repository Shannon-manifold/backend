package com.shannonmanifold.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile() {
        return ResponseEntity.ok("나의 프로필 조회 성공");
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateMyProfile() {
        return ResponseEntity.ok("나의 프로필 수정 성공");
    }

    @GetMapping("/me/activities")
    public ResponseEntity<?> getMyActivities() {
        return ResponseEntity.ok("나의 활동 조회 성공");
    }

    @GetMapping("/me/bookmarks")
    public ResponseEntity<?> getMyBookmarks() {
        return ResponseEntity.ok("나의 북마크 조회 성공");
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserProfile(@PathVariable Long userId) {
        return ResponseEntity.ok("유저 프로필 조회 성공");
    }

    @GetMapping
    public ResponseEntity<?> getContributors() {
        return ResponseEntity.ok("컨트리뷰터 조회 성공");
    }
}
