package com.shannonmanifold.backend.controller;

import com.shannonmanifold.backend.config.SecurityUtils;
import com.shannonmanifold.backend.dto.ChallengeDetailResponse;
import com.shannonmanifold.backend.dto.ChallengeResponse;
import com.shannonmanifold.backend.service.ChallengeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 챌린지 관련 API를 처리하는 컨트롤러
 * 담당자: 정윤수
 */
@RestController
@RequestMapping("/api/v1/challenges")
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeService challengeService;

    // difficulty 파라미터가 없으면 전체 목록 반환 (예: ?difficulty=Hard)
    @GetMapping
    public ResponseEntity<List<ChallengeResponse>> getChallenges(
            @RequestParam(required = false) String difficulty) {
        return ResponseEntity.ok(challengeService.getChallenges(difficulty));
    }

    // 상세 조회: references_json을 List<Map>으로 파싱하여 포함
    @GetMapping("/{challengeId}")
    public ResponseEntity<ChallengeDetailResponse> getChallenge(@PathVariable Long challengeId) {
        return ResponseEntity.ok(challengeService.getChallenge(challengeId));
    }

    // 후원: 인증 필요, backers 카운트 1 증가
    @PostMapping("/{challengeId}/sponsor")
    public ResponseEntity<ChallengeDetailResponse> sponsorChallenge(@PathVariable Long challengeId) {
        String email = SecurityUtils.getCurrentUserEmail();
        return ResponseEntity.ok(challengeService.sponsorChallenge(challengeId, email));
    }
}
