package com.shannonmanifold.backend.service;

import com.shannonmanifold.backend.dto.TutorialDetailResponse;
import com.shannonmanifold.backend.dto.TutorialResponse;
import com.shannonmanifold.backend.dto.TutorialStepResponse;
import com.shannonmanifold.backend.dto.UserTutorialProgressResponse;
import com.shannonmanifold.backend.entity.*;
import com.shannonmanifold.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TutorialService {

        private final TutorialRepository tutorialRepository;
        private final TutorialStepRepository tutorialStepRepository;
        private final TutorialCompletionRepository tutorialCompletionRepository;
        private final UserTutorialProgressRepository userTutorialProgressRepository;
        private final UserRepository userRepository;
        private final BookmarkRepository bookmarkRepository;

        public List<TutorialResponse> getAllTutorials() {
                return tutorialRepository.findAll().stream()
                                .map(tutorial -> TutorialResponse.builder()
                                                .id(tutorial.getId())
                                                .title(tutorial.getTitle())
                                                .description(tutorial.getDescription())
                                                .level(tutorial.getLevel().name())
                                                .duration(tutorial.getDuration())
                                                .lessonsCount(tutorial.getLessonsCount())
                                                .icon(tutorial.getIcon())
                                                .authorName(tutorial.getAuthorName())
                                                .tagsJson(tutorial.getTagsJson())
                                                .build())
                                .collect(Collectors.toList());
        }

        public TutorialDetailResponse getTutorialDetail(Long tutorialId) {
                Tutorial tutorial = tutorialRepository.findById(tutorialId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "해당 튜토리얼을 찾을 수 없습니다. ID: " + tutorialId));

                List<TutorialStepResponse> steps = tutorialStepRepository
                                .findByTutorialIdOrderByStepOrderAsc(tutorialId)
                                .stream()
                                .map(step -> TutorialStepResponse.builder()
                                                .id(step.getId())
                                                .stepOrder(step.getStepOrder())
                                                .title(step.getTitle())
                                                .description(step.getDescription())
                                                .explanation(step.getExplanation())
                                                .starterCode(step.getStarterCode())
                                                .solution(step.getSolution())
                                                .hint(step.getHint())
                                                .build())
                                .collect(Collectors.toList());

                return TutorialDetailResponse.builder()
                                .id(tutorial.getId())
                                .title(tutorial.getTitle())
                                .description(tutorial.getDescription())
                                .level(tutorial.getLevel().name())
                                .duration(tutorial.getDuration())
                                .lessonsCount(tutorial.getLessonsCount())
                                .icon(tutorial.getIcon())
                                .authorId(tutorial.getAuthorId())
                                .authorName(tutorial.getAuthorName())
                                .updatedAt(tutorial.getUpdatedAt() != null ? tutorial.getUpdatedAt().toString() : null)
                                .prerequisitesJson(tutorial.getPrerequisitesJson())
                                .tagsJson(tutorial.getTagsJson())
                                .steps(steps)
                                .build();
        }

        @Transactional
        public void completeStep(Long tutorialId, Long stepId, String email) {
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다. Email: " + email));
                TutorialStep step = tutorialStepRepository.findById(stepId)
                                .orElseThrow(() -> new IllegalArgumentException("해당 단계를 찾을 수 없습니다. ID: " + stepId));

                // 1. 단계 완료 기록 저장 (중복 체크)
                Optional<TutorialCompletion> completionOpt = tutorialCompletionRepository.findByUserAndStepId(user, stepId);
                if (completionOpt.isEmpty()) {
                        tutorialCompletionRepository.save(TutorialCompletion.builder()
                                        .user(user)
                                        .step(step)
                                        .completedAt(LocalDateTime.now())
                                        .build());
                }

                // 2. 전체 진행 상태 업데이트
                Tutorial tutorial = tutorialRepository.findById(tutorialId)
                                .orElseThrow(() -> new IllegalArgumentException("해당 튜토리얼을 찾을 수 없습니다. ID: " + tutorialId));

                UserTutorialProgress progress = userTutorialProgressRepository.findByUserAndTutorialId(user, tutorialId)
                                .orElseGet(() -> UserTutorialProgress.builder()
                                                .user(user)
                                                .tutorial(tutorial)
                                                .isCompleted(false)
                                                .progressPercent(0)
                                                .createdAt(LocalDateTime.now())
                                                .build());

                int completedCount = tutorialCompletionRepository.countByUserAndStep_TutorialId(user, tutorialId);
                int totalCount = tutorial.getLessonsCount();

                progress.updateProgress(completedCount, totalCount, step);
                userTutorialProgressRepository.save(progress);
        }

        public List<UserTutorialProgressResponse> getUserTutorialProgress(String email) {
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다. Email: " + email));

                return userTutorialProgressRepository.findByUserOrderByUpdatedAtDesc(user).stream()
                                .map(p -> UserTutorialProgressResponse.builder()
                                                .tutorialId(p.getTutorial().getId())
                                                .tutorialTitle(p.getTutorial().getTitle())
                                                .progressPercent(p.getProgressPercent())
                                                .isCompleted(p.isCompleted())
                                                .lastAccessedStepTitle(p.getLastAccessedStep() != null ? p.getLastAccessedStep().getTitle() : null)
                                                .updatedAt(p.getUpdatedAt() != null ? p.getUpdatedAt().toString() : null)
                                                .build())
                                .collect(Collectors.toList());
        }

        @Transactional
        public boolean toggleBookmark(Long tutorialId, String email) {
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다. Email: " + email));
                Tutorial tutorial = tutorialRepository.findById(tutorialId)
                                .orElseThrow(() -> new IllegalArgumentException("해당 튜토리얼을 찾을 수 없습니다. ID: " + tutorialId));

                Optional<Bookmark> bookmarkOpt = bookmarkRepository.findByUserAndTargetTypeAndTargetId(user, BookmarkType.tutorial, tutorialId);

                if (bookmarkOpt.isPresent()) {
                        bookmarkRepository.delete(bookmarkOpt.get());
                        return false;
                } else {
                        Bookmark bookmark = Bookmark.builder()
                                        .user(user)
                                        .targetType(BookmarkType.tutorial)
                                        .targetId(tutorialId)
                                        .title(tutorial.getTitle())
                                        .author(tutorial.getAuthorName())
                                        .build();
                        bookmarkRepository.save(bookmark);
                        return true;
                }
        }
}
