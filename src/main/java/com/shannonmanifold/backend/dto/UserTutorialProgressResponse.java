package com.shannonmanifold.backend.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserTutorialProgressResponse {
    private Long tutorialId;
    private String tutorialTitle;
    private int progressPercent;
    private boolean isCompleted;
    private String lastAccessedStepTitle;
    private String updatedAt;
}
