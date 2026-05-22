package com.shannonmanifold.backend.repository;

import com.shannonmanifold.backend.entity.BlogPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {
    List<BlogPost> findAllByOrderByDateDesc();
    List<BlogPost> findByCategoryOrderByDateDesc(String category);
}
