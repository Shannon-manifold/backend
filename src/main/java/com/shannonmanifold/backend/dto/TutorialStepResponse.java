package com.shannonmanifold.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TutorialStepResponse {
    private Long id;
    private int stepOrder;
    private String title;
    private String description;
    private String explanation;
    private String starterCode;
    private String solution;
    private String hint;
}
