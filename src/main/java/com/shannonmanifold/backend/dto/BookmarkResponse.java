package com.shannonmanifold.backend.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkResponse {
    private Long id;
    private String targetType; // "proof", "blog", "question", "tutorial"
    private Long targetId;
    private String title;
    private String author;
    private String logicSystem;
    private int likes;
    private String createdAt;
}
