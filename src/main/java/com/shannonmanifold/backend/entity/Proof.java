package com.shannonmanifold.backend.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "proofs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Proof {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // DB에서는 Long(BIGINT)을 사용

    private String title;
    private String description;

    @Column(name = "status", length = 20)
    private String status; // "verified", "pending", "failed"

    private String prover;

    private String language; // "Lean 4", "Rocq" 등

    private int likes;
    private int comments;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
