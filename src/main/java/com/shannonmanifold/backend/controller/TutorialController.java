package com.shannonmanifold.backend.controller;

import com.shannonmanifold.backend.dto.TutorialDetailResponse;
import com.shannonmanifold.backend.dto.TutorialResponse;
import com.shannonmanifold.backend.service.TutorialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 튜토리얼 관련 API를 처리하는 컨트롤러
 * 담당자: 이인수
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TutorialController {

    private final TutorialService tutorialService;

    @GetMapping("/tutorials")
    public ResponseEntity<List<TutorialResponse>> getTutorials() {
        return ResponseEntity.ok(tutorialService.getAllTutorials());
    }

    @GetMapping("/tutorials/{tutorialId}")
    public ResponseEntity<TutorialDetailResponse> getTutorial(@PathVariable Long tutorialId) {
        return ResponseEntity.ok(tutorialService.getTutorialDetail(tutorialId));
    }

    @GetMapping("/users/me/tutorials/progress")
    public ResponseEntity<?> getMyProgress() {
        String email = com.shannonmanifold.backend.config.SecurityUtils.getCurrentUserEmail();
        return ResponseEntity.ok(tutorialService.getUserTutorialProgress(email));
    }

    @PostMapping("/tutorials/{tutorialId}/steps/{stepId}/complete")
    public ResponseEntity<Void> completeStep(@PathVariable Long tutorialId, @PathVariable Long stepId) {
        String email = com.shannonmanifold.backend.config.SecurityUtils.getCurrentUserEmail();
        tutorialService.completeStep(tutorialId, stepId, email);
        return ResponseEntity.ok().build();
    }
}
