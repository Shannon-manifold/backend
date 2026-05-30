package com.shannonmanifold.backend.repository;

import com.shannonmanifold.backend.entity.ProofComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProofCommentRepository extends JpaRepository<ProofComment, Long> {
    List<ProofComment> findByProofIdOrderByCreatedAtAsc(Long proofId);
}
