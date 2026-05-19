package com.shannonmanifold.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "tutorials")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tutorial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TutorialLevel level;

    @Column(length = 50)
    private String duration;

    @Column(name = "lessons_count", columnDefinition = "INT DEFAULT 0")
    private int lessonsCount;

    @Column(length = 50)
    private String icon;

    @Column(name = "author_id")
    private Long authorId;

    @Column(name = "author_name")
    private String authorName;

    @Column(name = "updated_at", nullable = false)
    private LocalDate updatedAt;

    @Column(name = "prerequisites_json", columnDefinition = "JSON")
    private String prerequisitesJson;

    @Column(name = "tags_json", columnDefinition = "JSON")
    private String tagsJson;
}
