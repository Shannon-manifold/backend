package com.shannonmanifold.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile() {
        return ResponseEntity.ok().build();
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateMyProfile() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me/activities")
    public ResponseEntity<?> getMyActivities() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me/bookmarks")
    public ResponseEntity<?> getMyBookmarks() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserProfile(@PathVariable Long userId) {
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<?> getContributors() {
        return ResponseEntity.ok().build();
    }
}
