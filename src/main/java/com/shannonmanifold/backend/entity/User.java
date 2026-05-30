package com.shannonmanifold.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "system", length = 100)
    private String preferredSystem;

    @Column(name = "join_date", nullable = false)
    private LocalDate joinDate;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('member', 'moderator', 'admin') DEFAULT 'member'")
    private UserRole role;

    // Stats
    @Column(name = "stat_proofs", columnDefinition = "INT DEFAULT 0")
    private int statProofs;

    @Column(name = "stat_answers", columnDefinition = "INT DEFAULT 0")
    private int statAnswers;

    @Column(name = "stat_likes", columnDefinition = "INT DEFAULT 0")
    private int statLikes;

    @Column(name = "stat_points", columnDefinition = "INT DEFAULT 0")
    private int statPoints;

    // Notifications
    @Column(name = "noti_email", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean notiEmail;

    @Column(name = "noti_answer", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean notiAnswer;

    @Column(name = "noti_like", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean notiLike;

    @Column(name = "noti_challenge", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean notiChallenge;

    @Column(name = "created_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    public void updateProfile(String name, String bio, String avatarUrl, String preferredSystem) {
        if (name != null && !name.isBlank()) this.name = name;
        if (bio != null) this.bio = bio;
        if (avatarUrl != null) this.avatarUrl = avatarUrl;
        if (preferredSystem != null) this.preferredSystem = preferredSystem;
    }
}
