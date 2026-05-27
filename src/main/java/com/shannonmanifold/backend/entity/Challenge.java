package com.shannonmanifold.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "challenges")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Challenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 100)
    private String field;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 100)
    private String prize;

    @Column(name = "sponsor_pool", length = 100)
    private String sponsorPool;

    @Column(columnDefinition = "INT DEFAULT 0")
    private int backers;

    @Column(columnDefinition = "INT DEFAULT 0")
    private int progress;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('Millennium', 'Hard', 'Medium')", nullable = false)
    private ChallengeDifficulty difficulty;

    @Column(name = "proof_system", length = 100)
    private String proofSystem;

    @Column(length = 50)
    private String accent;

    @Column(name = "detailed_description", columnDefinition = "LONGTEXT")
    private String detailedDescription;

    // [{"title": "...", "url": "..."}] 형식의 JSON 문자열, 응답 시 List<Map>으로 파싱
    @Column(name = "references_json", columnDefinition = "JSON")
    private String referencesJson;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // 후원(sponsor) 시 호출
    public void incrementBackers() {
        this.backers++;
    }
}
