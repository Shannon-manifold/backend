package com.shannonmanifold.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 질문 관련 API를 처리하는 컨트롤러
 * 담당자: 홍성욱
 */
@RestController
@RequestMapping("/api/v1/questions")
public class QuestionController {

    @GetMapping
    public ResponseEntity<?> getQuestions() {
        return ResponseEntity.ok("질문 목록 조회 성공");
    }

    @GetMapping("/{questionId}")
    public ResponseEntity<?> getQuestion(@PathVariable Long questionId) {
        return ResponseEntity.ok("질문 상세 조회 성공");
    }

    @PostMapping
    public ResponseEntity<?> createQuestion() {
        return ResponseEntity.ok("질문 작성 성공");
    }

    @PutMapping("/{questionId}")
    public ResponseEntity<?> updateQuestion(@PathVariable Long questionId) {
        return ResponseEntity.ok("질문 수정 성공");
    }

    @PostMapping("/{questionId}/answers")
    public ResponseEntity<?> createAnswer(@PathVariable Long questionId) {
        return ResponseEntity.ok("답변 작성 성공");
    }

    @PostMapping("/answers/{answerId}/accept")
    public ResponseEntity<?> acceptAnswer(@PathVariable Long answerId) {
        return ResponseEntity.ok("답변 채택 성공");
    }

    @PostMapping("/{questionId}/like")
    public ResponseEntity<?> toggleQuestionLike(@PathVariable Long questionId) {
        return ResponseEntity.ok("질문 좋아요 성공");
    }

    @PostMapping("/answers/{answerId}/like")
    public ResponseEntity<?> toggleAnswerLike(@PathVariable Long answerId) {
        return ResponseEntity.ok("답변 좋아요 성공");
    }
}
