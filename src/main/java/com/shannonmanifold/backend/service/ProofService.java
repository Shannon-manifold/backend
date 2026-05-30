package com.shannonmanifold.backend.service;

import com.shannonmanifold.backend.dto.ProofCreateRequest;
import com.shannonmanifold.backend.dto.ProofUpdateRequest;
import com.shannonmanifold.backend.entity.ProofStatus;
import java.time.LocalDate;

import com.shannonmanifold.backend.dto.ProofDetailResponse;
import com.shannonmanifold.backend.dto.ProofResponse;
import com.shannonmanifold.backend.entity.Bookmark;
import com.shannonmanifold.backend.entity.BookmarkType;
import com.shannonmanifold.backend.entity.User;
import com.shannonmanifold.backend.entity.Proof;
import com.shannonmanifold.backend.entity.Notification;
import com.shannonmanifold.backend.entity.NotificationType;
import com.shannonmanifold.backend.entity.ProofLike;
import com.shannonmanifold.backend.repository.BookmarkRepository;
import com.shannonmanifold.backend.repository.ProofRepository;
import com.shannonmanifold.backend.repository.UserRepository;
import com.shannonmanifold.backend.repository.NotificationRepository;
import com.shannonmanifold.backend.repository.ProofLikeRepository;
import com.shannonmanifold.backend.repository.ProofCommentRepository;
import com.shannonmanifold.backend.entity.ProofComment;
import com.shannonmanifold.backend.dto.CommentResponse;
import com.shannonmanifold.backend.dto.CommentCreateRequest;
import java.util.Optional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
  private final BookmarkRepository bookmarkRepository;
  private final UserRepository userRepository;
  private final NotificationRepository notificationRepository;
  private final ProofLikeRepository proofLikeRepository;
  private final ProofCommentRepository proofCommentRepository;

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
  public ProofDetailResponse createProof(ProofCreateRequest request, String email) {
    User prover = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다. Email: " + email));

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
        .proverId(prover.getId())
        .proverName(prover.getName())
        .build();

    Proof savedProof = proofRepository.save(proof);

    return getProofDetail(savedProof.getId());
  }

  @Transactional
  public ProofDetailResponse updateProof(Long proofId, ProofUpdateRequest request, String email) {
    Proof proof = proofRepository.findById(proofId)
        .orElseThrow(() -> new IllegalArgumentException("해당 증명을 찾을 수 없습니다. ID: " + proofId));
    validateProver(proof.getProverId(), email);

    proof.update(request.getTitle(), request.getDescription(), request.getLanguage(), request.getField(),
        request.getLatex(), request.getCode());
    // 영속성 컨텍스트에 의해 자동 변경감지(Dirty Checking)가 일어나므로 save 명시 생략 가능

    return getProofDetail(proof.getId());
  }

  @Transactional
  public void deleteProof(Long proofId, String email) {
    Proof proof = proofRepository.findById(proofId)
        .orElseThrow(() -> new IllegalArgumentException("해당 증명을 찾을 수 없습니다. ID: " + proofId));
    validateProver(proof.getProverId(), email);
    proofRepository.delete(proof);
  }

  @Transactional
  public ProofDetailResponse verifyProof(Long proofId, String email) {
    Proof proof = proofRepository.findById(proofId)
        .orElseThrow(() -> new IllegalArgumentException("해당 증명을 찾을 수 없습니다. ID: " + proofId));
    validateProver(proof.getProverId(), email);

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

    String email = com.shannonmanifold.backend.config.SecurityUtils.getCurrentUserEmail();
    if (email != null && !email.equals("anonymousUser") && !email.isBlank()) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다. Email: " + email));

        Optional<ProofLike> existingLike = proofLikeRepository.findByUserAndProof(user, proof);

        if (existingLike.isPresent()) {
            proofLikeRepository.delete(existingLike.get());
            proof.decrementLikes();
        } else {
            ProofLike proofLike = ProofLike.builder()
                .user(user)
                .proof(proof)
                .build();
            proofLikeRepository.save(proofLike);
            proof.incrementLikes();

            if (!user.getId().equals(proof.getProverId())) {
                Optional<User> prover = userRepository.findById(proof.getProverId());
                if (prover.isPresent()) {
                    Notification notification = Notification.builder()
                            .user(prover.get())
                            .type(NotificationType.like)
                            .title("좋아요")
                            .message(user.getName() + " 님이 회원님의 증명에 좋아요를 눌렀습니다.")
                            .targetType("proof")
                            .targetId(proof.getId())
                            .isRead(false)
                            .createdAt(LocalDateTime.now())
                            .build();
                    notificationRepository.save(notification);
                }
            }
        }
    } else {
        proof.incrementLikes();
    }

    return getProofDetail(proof.getId());
  }

  @Transactional
  public boolean toggleBookmark(Long proofId, String email) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다. Email: " + email));
    Proof proof = proofRepository.findById(proofId)
        .orElseThrow(() -> new IllegalArgumentException("해당 증명을 찾을 수 없습니다. ID: " + proofId));

    Optional<Bookmark> bookmarkOpt = bookmarkRepository.findByUserAndTargetTypeAndTargetId(user, BookmarkType.proof, proofId);

    if (bookmarkOpt.isPresent()) {
      bookmarkRepository.delete(bookmarkOpt.get());
      return false; // Bookmarked removed
    } else {
      Bookmark bookmark = Bookmark.builder()
          .user(user)
          .targetType(BookmarkType.proof)
          .targetId(proofId)
          .title(proof.getTitle())
          .author(proof.getProverName())
          .logicSystem(proof.getLanguage())
          .likes(proof.getLikes())
          .build();
      bookmarkRepository.save(bookmark);
      return true; // Bookmarked added
    }
  }

  private void validateProver(Long proverId, String email) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다. Email: " + email));
    if (proverId == null || !proverId.equals(user.getId())) {
      throw new IllegalStateException("본인이 작성한 증명만 수정/삭제/검증할 수 있습니다.");
    }
  }

  public List<CommentResponse> getComments(Long proofId) {
    List<ProofComment> comments = proofCommentRepository.findByProofIdOrderByCreatedAtAsc(proofId);
    return comments.stream()
        .map(c -> CommentResponse.builder()
            .id(c.getId())
            .proofId(c.getProof().getId())
            .authorId(c.getUser().getId())
            .authorName(c.getUser().getName())
            .content(c.getContent())
            .date(c.getCreatedAt() != null ? c.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : null)
            .build())
        .collect(Collectors.toList());
  }

  @Transactional
  public CommentResponse createComment(Long proofId, CommentCreateRequest request, String email) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다. Email: " + email));
    Proof proof = proofRepository.findById(proofId)
        .orElseThrow(() -> new IllegalArgumentException("해당 증명을 찾을 수 없습니다. ID: " + proofId));

    ProofComment comment = ProofComment.builder()
        .proof(proof)
        .user(user)
        .content(request.getContent())
        .build();

    ProofComment saved = proofCommentRepository.save(comment);
    proof.incrementCommentsCount();

    return CommentResponse.builder()
        .id(saved.getId())
        .proofId(saved.getProof().getId())
        .authorId(saved.getUser().getId())
        .authorName(saved.getUser().getName())
        .content(saved.getContent())
        .date(saved.getCreatedAt() != null ? saved.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : null)
        .build();
  }

  @Transactional
  public CommentResponse updateComment(Long commentId, CommentCreateRequest request, String email) {
    ProofComment comment = proofCommentRepository.findById(commentId)
        .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다. ID: " + commentId));
    validateProver(comment.getUser().getId(), email);

    comment.update(request.getContent());

    return CommentResponse.builder()
        .id(comment.getId())
        .proofId(comment.getProof().getId())
        .authorId(comment.getUser().getId())
        .authorName(comment.getUser().getName())
        .content(comment.getContent())
        .date(comment.getCreatedAt() != null ? comment.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : null)
        .build();
  }

  @Transactional
  public void deleteComment(Long commentId, String email) {
    ProofComment comment = proofCommentRepository.findById(commentId)
        .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다. ID: " + commentId));
    validateProver(comment.getUser().getId(), email);

    Proof proof = comment.getProof();
    proof.decrementCommentsCount();
    proofCommentRepository.delete(comment);
  }
}
