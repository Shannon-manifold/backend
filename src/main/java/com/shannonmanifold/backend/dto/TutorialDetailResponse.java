package com.shannonmanifold.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TutorialDetailResponse {
    private Long id;
    private String title;
    private String description;
    private String level;
    private String duration;
    private int lessonsCount;
    private String icon;
    private Long authorId;
    private String authorName;
    private String updatedAt;
    private String prerequisitesJson;
    private String tagsJson;

    private List<TutorialStepResponse> steps;
}
