package com.shannonmanifold.backend.repository;

import com.shannonmanifold.backend.entity.QnaQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QnaQuestionRepository extends JpaRepository<QnaQuestion, Long> {
    List<QnaQuestion> findAllByOrderByDateDesc();
}
