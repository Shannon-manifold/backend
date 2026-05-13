package com.shannonmanifold.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/proofs")
public class ProofController {

    @GetMapping
    public ResponseEntity<?> getProofs() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{proofId}")
    public ResponseEntity<?> getProof(@PathVariable Long proofId) {
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<?> createProof() {
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{proofId}")
    public ResponseEntity<?> updateProof(@PathVariable Long proofId) {
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{proofId}")
    public ResponseEntity<?> deleteProof(@PathVariable Long proofId) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{proofId}/verify")
    public ResponseEntity<?> verifyProof(@PathVariable Long proofId) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{proofId}/like")
    public ResponseEntity<?> toggleLike(@PathVariable Long proofId) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{proofId}/bookmarks")
    public ResponseEntity<?> toggleBookmark(@PathVariable Long proofId) {
        return ResponseEntity.ok().build();
    }
}
