package com.shannonmanifold.backend.repository;

import com.shannonmanifold.backend.entity.Bookmark;
import com.shannonmanifold.backend.entity.BookmarkType;
import com.shannonmanifold.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    Optional<Bookmark> findByUserAndTargetTypeAndTargetId(User user, BookmarkType targetType, Long targetId);
    List<Bookmark> findByUserOrderByCreatedAtDesc(User user);
}
