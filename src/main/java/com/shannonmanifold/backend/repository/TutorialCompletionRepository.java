package com.shannonmanifold.backend.repository;

import com.shannonmanifold.backend.entity.TutorialCompletion;
import com.shannonmanifold.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TutorialCompletionRepository extends JpaRepository<TutorialCompletion, Long> {
    List<TutorialCompletion> findByUserAndStep_Tutorial_Id(User user, Long tutorialId);
    Optional<TutorialCompletion> findByUserAndStepId(User user, Long stepId);
    int countByUserAndStep_Tutorial_Id(User user, Long tutorialId);
}
