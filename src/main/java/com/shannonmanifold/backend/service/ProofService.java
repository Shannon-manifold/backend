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
    List<Proof> proofs = proofRepository.findAll();

    return proofs.stream()
        .map(proof -> ProofResponse.builder()
            .id(String.valueOf(proof.getId())) // Long 타입을 String으로 변환
            .title(proof.getTitle())
            .description(proof.getDescription())
            .status(proof.getStatus() != null ? String.valueOf(proof.getStatus()) : null) // enum의 이름을 그대로 사용 (verified, pending, failed)
            .prover(proof.getProverName() != null ? proof.getProverName() : "Unknown")
            .language(proof.getLanguage())
            .likes(proof.getLikes())
            .comments(proof.getCommentsCount())
            .date(proof.getDate() != null ? proof.getDate().toString() : null) // LocalDate를 "YYYY-MM-DD" 문자열로 변환
            .build())
        .collect(Collectors.toList());
  }
}