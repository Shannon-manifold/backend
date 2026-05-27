package com.shannonmanifold.backend.dto;

import com.shannonmanifold.backend.entity.ChallengeDifficulty;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeDetailResponse {
    private Long id;
    private String title;
    private String field;
    private String description;
    private String prize;
    private String sponsorPool;
    private int backers;
    private int progress;
    private ChallengeDifficulty difficulty;
    private String proofSystem;
    private String accent;
    private String detailedDescription;
    private List<Map<String, String>> references;
}
