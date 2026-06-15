package com.shannonmanifold.backend.dto;

import com.shannonmanifold.backend.entity.ChallengeDifficulty;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeCreateRequest {
    private String title;
    private String field;
    private String description;
    private String prize;
    private String sponsorPool;
    private int progress;
    private ChallengeDifficulty difficulty;
    private String proofSystem;
    private String accent;
    private String detailedDescription;
    // [{"title": "...", "url": "..."}] 형식, 저장 시 JSON 문자열로 직렬화
    private List<Map<String, String>> references;
}
