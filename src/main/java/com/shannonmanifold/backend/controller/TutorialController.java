package com.shannonmanifold.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class TutorialController {

    @GetMapping("/tutorials")
    public ResponseEntity<?> getTutorials() {
        return ResponseEntity.ok("튜토리얼 목록 조회 성공");
    }

    @GetMapping("/tutorials/{tutorialId}")
    public ResponseEntity<?> getTutorial(@PathVariable Long tutorialId) {
        return ResponseEntity.ok("튜토리얼 상세 조회 성공");
    }

    @GetMapping("/users/me/tutorials/progress")
    public ResponseEntity<?> getMyProgress() {
        return ResponseEntity.ok("나의 진행 상황 조회 성공");
    }

    @PostMapping("/tutorials/{tutorialId}/steps/{stepId}/complete")
    public ResponseEntity<?> completeStep(@PathVariable Long tutorialId, @PathVariable Long stepId) {
        return ResponseEntity.ok("단계 완료 처리 성공");
    }
}
