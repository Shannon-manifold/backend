package com.shannonmanifold.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProofCreateRequest {
    private String title;
    private String description;
    private String language;
    private String field;
    private String latex;
    private String code;
}
