package com.shannonmanifold.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class TutorialController {

    @GetMapping("/tutorials")
    public ResponseEntity<?> getTutorials() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/tutorials/{tutorialId}")
    public ResponseEntity<?> getTutorial(@PathVariable Long tutorialId) {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users/me/tutorials/progress")
    public ResponseEntity<?> getMyProgress() {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/tutorials/{tutorialId}/steps/{stepId}/complete")
    public ResponseEntity<?> completeStep(@PathVariable Long tutorialId, @PathVariable Long stepId) {
        return ResponseEntity.ok().build();
    }
}
