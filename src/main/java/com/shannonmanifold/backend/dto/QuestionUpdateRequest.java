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
public class QuestionUpdateRequest {
    private String title;
    private String description;
    private String content;
    private List<String> tags;
}
