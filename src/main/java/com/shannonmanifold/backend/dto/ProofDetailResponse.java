package com.shannonmanifold.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProofDetailResponse {
    private Long id;
    private String title;
    private String description;
    private String status;
    private Long proverId;
    private String proverName;
    private String language;
    private int likes;
    private int commentsCount;
    private LocalDate date;
    private String field;
    private String latex;
    private String code;
}