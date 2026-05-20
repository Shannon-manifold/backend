package com.shannonmanifold.backend.repository;

import com.shannonmanifold.backend.entity.UserTutorialProgress;
import com.shannonmanifold.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface UserTutorialProgressRepository extends JpaRepository<UserTutorialProgress, Long> {
    Optional<UserTutorialProgress> findByUserAndTutorialId(User user, Long tutorialId);
    List<UserTutorialProgress> findByUserOrderByUpdatedAtDesc(User user);
}
