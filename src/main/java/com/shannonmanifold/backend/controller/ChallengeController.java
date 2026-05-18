package com.shannonmanifold.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 챌린지 관련 API를 처리하는 컨트롤러
 * 담당자: 정윤수
 */
@RestController
@RequestMapping("/api/v1/challenges")
public class ChallengeController {

    @GetMapping
    public ResponseEntity<?> getChallenges() {
        return ResponseEntity.ok("챌린지 목록 조회 성공");
    }

    @GetMapping("/{challengeId}")
    public ResponseEntity<?> getChallenge(@PathVariable Long challengeId) {
        return ResponseEntity.ok("챌린지 상세 조회 성공");
    }

    @PostMapping("/{challengeId}/sponsor")
    public ResponseEntity<?> sponsorChallenge(@PathVariable Long challengeId) {
        return ResponseEntity.ok("챌린지 후원 성공");
    }
}
