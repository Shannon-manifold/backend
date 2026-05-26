package com.shannonmanifold.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogPostUpdateRequest {
    private String title;
    private String excerpt;
    private String readTime;
    private String category;
    private String imageUrl;
    private String content;
}
