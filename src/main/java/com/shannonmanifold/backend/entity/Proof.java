package com.shannonmanifold.backend.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "proofs")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
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

  public void update(String title, String description, String language, String field, String latex, String code) {
    if (title != null) this.title = title;
    if (description != null) this.description = description;
    if (language != null) this.language = language;
    if (field != null) this.field = field;
    if (latex != null) this.latex = latex;
    if (code != null) this.code = code;
  }

  public void verify() {
    this.status = ProofStatus.verified;
  }

  public void fail() {
    this.status = ProofStatus.failed;
  }

  public void incrementLikes() {
    this.likes++;
  }

  public void decrementLikes() {
    if (this.likes > 0) {
      this.likes--;
    }
  }

  public void incrementCommentsCount() {
    this.commentsCount++;
  }
}
