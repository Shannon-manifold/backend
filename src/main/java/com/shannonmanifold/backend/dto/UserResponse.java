package com.shannonmanifold.backend.dto;

import lombok.*;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private String role;
    private String badge;
    private String initial;
    private String field;
    private int proofs;
    private int answers;
    private String reputation;
    private String joinDate;
    private String bio;
    private List<String> languages;
    private List<AchievementDto> achievements;
    private List<RecentActivityDto> recentActivity;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AchievementDto {
        private String icon;
        private String title;
        private String desc;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentActivityDto {
        private String type;
        private String title;
        private String date;
    }
}
