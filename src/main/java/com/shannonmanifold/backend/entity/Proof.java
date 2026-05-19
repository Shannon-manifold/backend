package com.shannonmanifold.backend.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

enum ProofStatus {
  verified, pending, failed
}

@Entity
@Table(name = "proofs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Proof {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String title;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ProofStatus status;

  @Column(name = "prover_id")
  private Long proverId;

  @Column(name = "prover_name")
  private String proverName;

  @Column(length = 100)
  private String language;

  @Column(columnDefinition = "INT DEFAULT 0")
  private int likes;

  @Column(name = "comments_count", columnDefinition = "INT DEFAULT 0")
  private int commentsCount;

  @Column(nullable = false)
  private LocalDate date;

  @Column(length = 100)
  private String field;

  @Column(columnDefinition = "TEXT")
  private String latex;

  @Column(columnDefinition = "LONGTEXT")
  private String code;
}
