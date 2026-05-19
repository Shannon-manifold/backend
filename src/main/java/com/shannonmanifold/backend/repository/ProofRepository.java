package com.shannonmanifold.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.shannonmanifold.backend.entity.Proof;

@Repository
public interface ProofRepository extends JpaRepository<Proof, Long> {
}