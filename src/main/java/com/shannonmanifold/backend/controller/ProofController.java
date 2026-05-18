package com.shannonmanifold.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 증명(Proof) 관련 API를 처리하는 컨트롤러
 * 담당자: 이인수
 */
@RestController
@RequestMapping("/api/v1/proofs")
public class ProofController {

    @GetMapping
    public ResponseEntity<?> getProofs() {
        return ResponseEntity.ok("증명 목록 조회 성공");
    }

    @GetMapping("/{proofId}")
    public ResponseEntity<?> getProof(@PathVariable Long proofId) {
        return ResponseEntity.ok("증명 상세 조회 성공");
    }

    @PostMapping
    public ResponseEntity<?> createProof() {
        return ResponseEntity.ok("증명 생성 성공");
    }

    @PutMapping("/{proofId}")
    public ResponseEntity<?> updateProof(@PathVariable Long proofId) {
        return ResponseEntity.ok("증명 수정 성공");
    }

    @DeleteMapping("/{proofId}")
    public ResponseEntity<?> deleteProof(@PathVariable Long proofId) {
        return ResponseEntity.ok("증명 삭제 성공");
    }

    @PostMapping("/{proofId}/verify")
    public ResponseEntity<?> verifyProof(@PathVariable Long proofId) {
        return ResponseEntity.ok("증명 검증 성공");
    }

    @PostMapping("/{proofId}/like")
    public ResponseEntity<?> toggleLike(@PathVariable Long proofId) {
        return ResponseEntity.ok("증명 좋아요 성공");
    }

    @PostMapping("/{proofId}/bookmarks")
    public ResponseEntity<?> toggleBookmark(@PathVariable Long proofId) {
        return ResponseEntity.ok("증명 북마크 성공");
    }
}
