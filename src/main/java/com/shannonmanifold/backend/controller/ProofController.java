package com.shannonmanifold.backend.controller;

import com.shannonmanifold.backend.dto.ProofCreateRequest;
import com.shannonmanifold.backend.dto.ProofUpdateRequest;
import com.shannonmanifold.backend.dto.ProofDetailResponse;
import com.shannonmanifold.backend.dto.ProofResponse;
import com.shannonmanifold.backend.service.ProofService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 증명(Proof) 관련 API를 처리하는 컨트롤러
 * 담당자: 이인수
 */
@RestController
@RequestMapping("/api/proofs")
@RequiredArgsConstructor
public class ProofController {

  private final ProofService proofService;

  @GetMapping
  public ResponseEntity<List<ProofResponse>> getProofs() {
    List<ProofResponse> proofs = proofService.getAllProofs();
    return ResponseEntity.ok(proofs);
  }

  @GetMapping("/{proofId}")
  public ResponseEntity<?> getProof(@PathVariable Long proofId) {
    ProofDetailResponse response = proofService.getProofDetail(proofId);
    if (response == null)
      return ResponseEntity.notFound().build();
    return ResponseEntity.ok(response);
  }

  @PostMapping
  public ResponseEntity<ProofDetailResponse> createProof(@RequestBody ProofCreateRequest request) {
    ProofDetailResponse response = proofService.createProof(request);
    return ResponseEntity.status(201).body(response);
  }

  @PutMapping("/{proofId}")
  public ResponseEntity<ProofDetailResponse> updateProof(
      @PathVariable Long proofId,
      @RequestBody ProofUpdateRequest request) {
    ProofDetailResponse response = proofService.updateProof(proofId, request);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{proofId}")
  public ResponseEntity<Void> deleteProof(@PathVariable Long proofId) {
    proofService.deleteProof(proofId);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/{proofId}/verify")
  public ResponseEntity<ProofDetailResponse> verifyProof(@PathVariable Long proofId) {
    ProofDetailResponse response = proofService.verifyProof(proofId);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/{proofId}/like")
  public ResponseEntity<ProofDetailResponse> toggleLike(@PathVariable Long proofId) {
    ProofDetailResponse response = proofService.toggleLike(proofId);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/{proofId}/bookmarks")
  public ResponseEntity<?> toggleBookmark(@PathVariable Long proofId) {
    String email = com.shannonmanifold.backend.config.SecurityUtils.getCurrentUserEmail();
    boolean added = proofService.toggleBookmark(proofId, email);
    String message = added ? "증명 북마크 추가 성공" : "증명 북마크 제거 성공";
    return ResponseEntity.ok(message);
  }
}
