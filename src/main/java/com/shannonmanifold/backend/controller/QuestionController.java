package com.shannonmanifold.backend.controller;

import com.shannonmanifold.backend.config.SecurityUtils;
import com.shannonmanifold.backend.dto.AnswerCreateRequest;
import com.shannonmanifold.backend.dto.AnswerResponse;
import com.shannonmanifold.backend.dto.CommentResponse;
import com.shannonmanifold.backend.dto.CommentCreateRequest;
import com.shannonmanifold.backend.dto.QuestionCreateRequest;
import com.shannonmanifold.backend.dto.QuestionDetailResponse;
import com.shannonmanifold.backend.dto.QuestionResponse;
import com.shannonmanifold.backend.dto.QuestionUpdateRequest;
import com.shannonmanifold.backend.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 질문 관련 API를 처리하는 컨트롤러
 * 담당자: 홍성욱
 */
@RestController
@RequestMapping("/api/v1/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping
    public ResponseEntity<List<QuestionResponse>> getQuestions() {
        return ResponseEntity.ok(questionService.getAllQuestions());
    }

    @GetMapping("/{questionId}")
    public ResponseEntity<QuestionDetailResponse> getQuestion(@PathVariable Long questionId) {
        return ResponseEntity.ok(questionService.getQuestion(questionId));
    }

    @PostMapping
    public ResponseEntity<QuestionDetailResponse> createQuestion(@RequestBody QuestionCreateRequest request) {
        String email = SecurityUtils.getCurrentUserEmail();
        return ResponseEntity.status(201).body(questionService.createQuestion(request, email));
    }

    @PutMapping("/{questionId}")
    public ResponseEntity<QuestionDetailResponse> updateQuestion(
            @PathVariable Long questionId,
            @RequestBody QuestionUpdateRequest request) {
        String email = SecurityUtils.getCurrentUserEmail();
        return ResponseEntity.ok(questionService.updateQuestion(questionId, request, email));
    }

    @DeleteMapping("/{questionId}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long questionId) {
        String email = SecurityUtils.getCurrentUserEmail();
        questionService.deleteQuestion(questionId, email);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{questionId}/answers")
    public ResponseEntity<AnswerResponse> createAnswer(
            @PathVariable Long questionId,
            @RequestBody AnswerCreateRequest request) {
        String email = SecurityUtils.getCurrentUserEmail();
        return ResponseEntity.status(201).body(questionService.createAnswer(questionId, request, email));
    }

    @PostMapping("/answers/{answerId}/accept")
    public ResponseEntity<AnswerResponse> acceptAnswer(@PathVariable Long answerId) {
        String email = SecurityUtils.getCurrentUserEmail();
        return ResponseEntity.ok(questionService.acceptAnswer(answerId, email));
    }

    @PostMapping("/{questionId}/like")
    public ResponseEntity<Integer> toggleQuestionLike(@PathVariable Long questionId) {
        return ResponseEntity.ok(questionService.toggleQuestionLike(questionId));
    }

    @PostMapping("/answers/{answerId}/like")
    public ResponseEntity<Integer> toggleAnswerLike(@PathVariable Long answerId) {
        return ResponseEntity.ok(questionService.toggleAnswerLike(answerId));
    }

    @PostMapping("/{questionId}/bookmarks")
    public ResponseEntity<String> toggleBookmark(@PathVariable Long questionId) {
        String email = SecurityUtils.getCurrentUserEmail();
        boolean added = questionService.toggleBookmark(questionId, email);
        return ResponseEntity.ok(added ? "질문 북마크 추가 성공" : "질문 북마크 제거 성공");
    }

    @GetMapping("/answers/{answerId}/comments")
    public ResponseEntity<List<CommentResponse>> getAnswerComments(@PathVariable Long answerId) {
        List<CommentResponse> comments = questionService.getAnswerComments(answerId);
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/answers/{answerId}/comments")
    public ResponseEntity<CommentResponse> createAnswerComment(
            @PathVariable Long answerId,
            @RequestBody CommentCreateRequest request) {
        String email = SecurityUtils.getCurrentUserEmail();
        CommentResponse response = questionService.createAnswerComment(answerId, request, email);
        return ResponseEntity.status(201).body(response);
    }
}
