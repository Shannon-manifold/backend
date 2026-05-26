package com.shannonmanifold.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDetailResponse {
    private Long id;
    private String title;
    private String description;
    private Long authorId;
    private String authorName;
    private LocalDate date;
    private int views;
    private int answersCount;
    private int likes;
    private String status;
    private String content;
    private List<String> tags;
    private List<AnswerResponse> answers;
}
