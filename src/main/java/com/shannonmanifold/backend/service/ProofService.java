package com.shannonmanifold.backend.service;

import com.shannonmanifold.backend.dto.ProofCreateRequest;
import com.shannonmanifold.backend.dto.ProofUpdateRequest;
import com.shannonmanifold.backend.entity.ProofStatus;
import java.time.LocalDate;

import com.shannonmanifold.backend.dto.ProofDetailResponse;
import com.shannonmanifold.backend.dto.ProofResponse;
import com.shannonmanifold.backend.entity.Proof;
import com.shannonmanifold.backend.repository.ProofRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;
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
            .id(proof.getId())
            .title(proof.getTitle())
            .description(proof.getDescription())
            .status(proof.getStatus() != null ? String.valueOf(proof.getStatus()) : null)
            .prover(proof.getProverName() != null ? proof.getProverName() : "Unknown")
            .language(proof.getLanguage())
            .likes(proof.getLikes())
            .comments(proof.getCommentsCount())
            .date(proof.getDate() != null ? proof.getDate().toString() : null)
            .build())
        .collect(Collectors.toList());
  }

  public ProofDetailResponse getProofDetail(Long proofId) {
    // DB에서 ID로 조회, 없으면 404 성격의 예외 발생
    Proof proof = proofRepository.findById(proofId)
        .orElseThrow(() -> new IllegalArgumentException("해당 증명을 찾을 수 없습니다. ID: " + proofId));

    // Entity -> DTO 변환
    return ProofDetailResponse.builder()
        .id(proof.getId())
        .title(proof.getTitle())
        .description(proof.getDescription())
        .status(proof.getStatus() != null ? String.valueOf(proof.getStatus()) : null)
        .proverId(proof.getProverId())
        .proverName(proof.getProverName() != null ? proof.getProverName() : "Unknown")
        .language(proof.getLanguage())
        .likes(proof.getLikes())
        .commentsCount(proof.getCommentsCount())
        .date(proof.getDate())
        .field(proof.getField())
        .latex(proof.getLatex())
        .code(proof.getCode())
        .build();
  }

  @Transactional
  public ProofDetailResponse createProof(ProofCreateRequest request) {
    Proof proof = Proof.builder()
        .title(request.getTitle())
        .description(request.getDescription())
        .status(ProofStatus.pending)
        .language(request.getLanguage())
        .field(request.getField())
        .latex(request.getLatex())
        .code(request.getCode())
        .date(LocalDate.now())
        .likes(0)
        .commentsCount(0)
        // Todo: Extract proverId and proverName from authentication context
        .proverId(1L)
        .proverName("Current User")
        .build();

    Proof savedProof = proofRepository.save(proof);

    return getProofDetail(savedProof.getId());
  }

  @Transactional
  public ProofDetailResponse updateProof(Long proofId, ProofUpdateRequest request) {
    Proof proof = proofRepository.findById(proofId)
        .orElseThrow(() -> new IllegalArgumentException("해당 증명을 찾을 수 없습니다. ID: " + proofId));

    proof.update(request.getTitle(), request.getDescription(), request.getLanguage(), request.getField(),
        request.getLatex(), request.getCode());
    // 영속성 컨텍스트에 의해 자동 변경감지(Dirty Checking)가 일어나므로 save 명시 생략 가능

    return getProofDetail(proof.getId());
  }

  @Transactional
  public void deleteProof(Long proofId) {
    Proof proof = proofRepository.findById(proofId)
        .orElseThrow(() -> new IllegalArgumentException("해당 증명을 찾을 수 없습니다. ID: " + proofId));
    proofRepository.delete(proof);
  }

  @Transactional
  public ProofDetailResponse verifyProof(Long proofId) {
    Proof proof = proofRepository.findById(proofId)
        .orElseThrow(() -> new IllegalArgumentException("해당 증명을 찾을 수 없습니다. ID: " + proofId));

    String language = proof.getLanguage();
    if (language != null && language.toLowerCase().startsWith("lean")) {
      try {
        File tempFile = File.createTempFile("proof", ".lean");
        Files.writeString(tempFile.toPath(), proof.getCode() != null ? proof.getCode() : "");

        ProcessBuilder pb = new ProcessBuilder("lean", tempFile.getAbsolutePath());
        Process process = pb.start();
        boolean finished = process.waitFor(30, TimeUnit.SECONDS);

        if (finished && process.exitValue() == 0) {
          proof.verify();
        } else {
          proof.fail();
        }

        if (tempFile.exists()) {
          tempFile.delete();
        }
      } catch (IOException | InterruptedException e) {
        proof.fail();
        if (e instanceof InterruptedException) {
          Thread.currentThread().interrupt();
        }
      }
    } else {
      proof.verify();
    }

    return getProofDetail(proof.getId());
  }

  @Transactional
  public ProofDetailResponse toggleLike(Long proofId) {
    Proof proof = proofRepository.findById(proofId)
        .orElseThrow(() -> new IllegalArgumentException("해당 증명을 찾을 수 없습니다. ID: " + proofId));
    // 임시로 무조건 좋아요 증가 (실제로는 User-Like 매핑 테이블 확인 후 토글 구현 필요)
    proof.incrementLikes();
    return getProofDetail(proof.getId());
  }
}
