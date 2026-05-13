package com.shannonmanifold.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/blogs")
public class BlogController {

    @GetMapping
    public ResponseEntity<?> getBlogs() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{postId}")
    public ResponseEntity<?> getBlog(@PathVariable Long postId) {
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<?> createBlog() {
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{postId}")
    public ResponseEntity<?> updateBlog(@PathVariable Long postId) {
        return ResponseEntity.ok().build();
    }
}
