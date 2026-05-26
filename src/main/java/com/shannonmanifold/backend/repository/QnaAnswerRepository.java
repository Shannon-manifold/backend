package com.shannonmanifold.backend.repository;

import com.shannonmanifold.backend.entity.QnaAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QnaAnswerRepository extends JpaRepository<QnaAnswer, Long> {
    List<QnaAnswer> findByQuestionIdOrderByAcceptedDescDateAsc(Long questionId);
}
