package com.shannonmanifold.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {
    private Long id;
    private Long proofId;
    private Long blogId;
    private Long answerId;
    private Long authorId;
    private String authorName;
    private String content;
    private String date;
}
