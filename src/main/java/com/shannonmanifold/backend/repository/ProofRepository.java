package com.shannonmanifold.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.shannonmanifold.backend.entity.Proof;

public interface ProofRepository extends JpaRepository<Proof, Long> {
    // 기본적인 CRUD 및 전체 조회(findAll) 메서드가 자동으로 제공됩니다.
}
