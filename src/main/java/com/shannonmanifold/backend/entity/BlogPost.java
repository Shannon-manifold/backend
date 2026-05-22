package com.shannonmanifold.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "blog_posts")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BlogPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String excerpt;

    @Column(name = "author_id")
    private Long authorId;

    @Column(name = "author_name")
    private String authorName;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "read_time", length = 50)
    private String readTime;

    @Column(length = 100)
    private String category;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    @Column(columnDefinition = "INT DEFAULT 0")
    private int likes;

    public void update(String title, String excerpt, String readTime, String category, String imageUrl, String content) {
        if (title != null) this.title = title;
        if (excerpt != null) this.excerpt = excerpt;
        if (readTime != null) this.readTime = readTime;
        if (category != null) this.category = category;
        if (imageUrl != null) this.imageUrl = imageUrl;
        if (content != null) this.content = content;
    }

    public void incrementLikes() {
        this.likes++;
    }

    public void decrementLikes() {
        if (this.likes > 0) this.likes--;
    }
}
