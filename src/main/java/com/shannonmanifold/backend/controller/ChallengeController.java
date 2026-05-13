package com.shannonmanifold.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/challenges")
public class ChallengeController {

    @GetMapping
    public ResponseEntity<?> getChallenges() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{challengeId}")
    public ResponseEntity<?> getChallenge(@PathVariable Long challengeId) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{challengeId}/sponsor")
    public ResponseEntity<?> sponsorChallenge(@PathVariable Long challengeId) {
        return ResponseEntity.ok().build();
    }
}
