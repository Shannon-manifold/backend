package com.shannonmanifold.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "proof_likes", uniqueConstraints = {
    @UniqueConstraint(name = "idx_user_proof", columnNames = {"user_id", "proof_id"})
})
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProofLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proof_id", nullable = false)
    private Proof proof;
}
