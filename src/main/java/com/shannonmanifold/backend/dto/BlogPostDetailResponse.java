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
public class BlogPostDetailResponse {
    private Long id;
    private String title;
    private String excerpt;
    private Long authorId;
    private String authorName;
    private LocalDate date;
    private String readTime;
    private String category;
    private String imageUrl;
    private String content;
    private int likes;
}
