package com.shannonmanifold.backend.service;

import com.shannonmanifold.backend.dto.ProofResponse;
import com.shannonmanifold.backend.entity.Proof;
import com.shannonmanifold.backend.repository.ProofRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProofService {

  private final ProofRepository proofRepository;

  public List<ProofResponse> getAllProofs() {
    // 1. DB에서 모든 증명 데이터를 조회
    List<Proof> proofs = proofRepository.findAll();

    // 2. Entity를 DTO로 변환하여 프론트엔드 규격에 맞춤
    return proofs.stream()
        .map(proof -> ProofResponse.builder()
            .id(String.valueOf(proof.getId())) // 프론트엔드 id 타입(String)에 맞게 변환
            .title(proof.getTitle())
            .description(proof.getDescription())
            .status(proof.getStatus())
            .prover(proof.getProver())
            .language(proof.getLanguage())
            .likes(proof.getLikes())
            .comments(proof.getComments())
            .date(proof.getCreatedAt().toLocalDate().toString()) // "YYYY-MM-DD" 형태로 변환
            .build())
        .collect(Collectors.toList());
  }
}
