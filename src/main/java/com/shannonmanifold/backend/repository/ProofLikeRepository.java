package com.shannonmanifold.backend.repository;

import com.shannonmanifold.backend.entity.Proof;
import com.shannonmanifold.backend.entity.ProofLike;
import com.shannonmanifold.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProofLikeRepository extends JpaRepository<ProofLike, Long> {
    Optional<ProofLike> findByUserAndProof(User user, Proof proof);
}
