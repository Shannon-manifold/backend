package com.shannonmanifold.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TutorialResponse {
    private Long id;
    private String title;
    private String description;
    private String level;
    private String duration;
    private int lessonsCount;
    private String icon;
    private String authorName;
    private String tagsJson;
}
