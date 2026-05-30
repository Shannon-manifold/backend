package com.shannonmanifold.backend.repository;

import com.shannonmanifold.backend.entity.Tutorial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TutorialRepository extends JpaRepository<Tutorial, Long> {
    boolean existsByTitle(String title);
}
