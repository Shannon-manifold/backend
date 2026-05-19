package com.shannonmanifold.backend.service;

import com.shannonmanifold.backend.dto.TutorialDetailResponse;
import com.shannonmanifold.backend.dto.TutorialResponse;
import com.shannonmanifold.backend.dto.TutorialStepResponse;
import com.shannonmanifold.backend.entity.Tutorial;
import com.shannonmanifold.backend.repository.TutorialRepository;
import com.shannonmanifold.backend.repository.TutorialStepRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TutorialService {

        private final TutorialRepository tutorialRepository;
        private final TutorialStepRepository tutorialStepRepository;

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
        public void completeStep(Long tutorialId, Long stepId) {
                boolean stepExists = tutorialStepRepository.existsById(stepId);
                if (!stepExists) {
                        throw new IllegalArgumentException("해당 단계를 찾을 수 없습니다. ID: " + stepId);
                }
        }
}
