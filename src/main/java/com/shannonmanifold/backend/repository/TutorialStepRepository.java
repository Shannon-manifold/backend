package com.shannonmanifold.backend.repository;

import com.shannonmanifold.backend.entity.TutorialStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TutorialStepRepository extends JpaRepository<TutorialStep, Long> {
    List<TutorialStep> findByTutorialIdOrderByStepOrderAsc(Long tutorialId);
}
