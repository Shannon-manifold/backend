package com.shannonmanifold.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tutorial_steps")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TutorialStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tutorial_id", nullable = false)
    private Long tutorialId;

    @Column(name = "step_order", nullable = false)
    private int stepOrder;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "LONGTEXT")
    private String explanation;

    @Column(name = "starter_code", columnDefinition = "LONGTEXT")
    private String starterCode;

    @Column(columnDefinition = "LONGTEXT")
    private String solution;

    @Column(columnDefinition = "TEXT")
    private String hint;
}
