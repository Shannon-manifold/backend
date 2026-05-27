package com.shannonmanifold.backend.repository;

import com.shannonmanifold.backend.entity.Challenge;
import com.shannonmanifold.backend.entity.ChallengeDifficulty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
    List<Challenge> findAllByOrderByCreatedAtDesc();
    List<Challenge> findByDifficultyOrderByCreatedAtDesc(ChallengeDifficulty difficulty);
}
