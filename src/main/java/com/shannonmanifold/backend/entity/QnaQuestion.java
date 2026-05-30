package com.shannonmanifold.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "qna_questions")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QnaQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "author_id")
    private Long authorId;

    @Column(name = "author_name")
    private String authorName;

    @Column(nullable = false)
    private LocalDate date;

    @Column(columnDefinition = "INT DEFAULT 0")
    private int views;

    @Column(name = "answers_count", columnDefinition = "INT DEFAULT 0")
    private int answersCount;

    @Column(columnDefinition = "INT DEFAULT 0")
    private int likes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QnaStatus status;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @Column(name = "tags_json", columnDefinition = "JSON")
    private String tagsJson;

    public void update(String title, String description, String content, String tagsJson) {
        if (title != null) this.title = title;
        if (description != null) this.description = description;
        if (content != null) this.content = content;
        if (tagsJson != null) this.tagsJson = tagsJson;
    }

    public void markAnswered() {
        this.status = QnaStatus.answered;
    }

    public void incrementAnswersCount() {
        this.answersCount++;
    }

    public void decrementAnswersCount() {
        if (this.answersCount > 0) {
            this.answersCount--;
        }
    }

    public void incrementViews() {
        this.views++;
    }

    public void incrementLikes() {
        this.likes++;
    }

    public void decrementLikes() {
        if (this.likes > 0) this.likes--;
    }
}
