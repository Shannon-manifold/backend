package com.shannonmanifold.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/questions")
public class QuestionController {

    @GetMapping
    public ResponseEntity<?> getQuestions() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{questionId}")
    public ResponseEntity<?> getQuestion(@PathVariable Long questionId) {
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<?> createQuestion() {
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{questionId}")
    public ResponseEntity<?> updateQuestion(@PathVariable Long questionId) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{questionId}/answers")
    public ResponseEntity<?> createAnswer(@PathVariable Long questionId) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/answers/{answerId}/accept")
    public ResponseEntity<?> acceptAnswer(@PathVariable Long answerId) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{questionId}/like")
    public ResponseEntity<?> toggleQuestionLike(@PathVariable Long questionId) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/answers/{answerId}/like")
    public ResponseEntity<?> toggleAnswerLike(@PathVariable Long answerId) {
        return ResponseEntity.ok().build();
    }
}
