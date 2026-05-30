package com.shannonmanifold.backend.repository;

import com.shannonmanifold.backend.entity.AnswerComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerCommentRepository extends JpaRepository<AnswerComment, Long> {
    List<AnswerComment> findByQnaAnswerIdOrderByCreatedAtAsc(Long qnaAnswerId);
}
