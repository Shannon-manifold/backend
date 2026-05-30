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

        public com.shannonmanifold.backend.dto.VerifyResponse verifyStep(Long tutorialId, Long stepId, String code) {
                // 1. 유효한 단계가 존재하는지 검증
                tutorialStepRepository.findById(stepId)
                                .orElseThrow(() -> new IllegalArgumentException("해당 단계를 찾을 수 없습니다. ID: " + stepId));

                // 2. 검증을 수행할 test 디렉토리 위치 탐색
                java.io.File testDir = new java.io.File("test");
                if (!testDir.exists()) {
                        testDir = new java.io.File(System.getProperty("user.dir"), "test");
                }
                if (!testDir.exists()) {
                        testDir = new java.io.File("../test");
                }

                // 3. 임시 파일명 생성 및 코드 저장
                String fileName = "verify_" + System.currentTimeMillis() + "_" + (int) (Math.random() * 1000) + ".lean";
                java.io.File tempFile = new java.io.File(testDir, fileName);

                try {
                        java.nio.file.Files.writeString(tempFile.toPath(), code != null ? code : "");
                } catch (java.io.IOException e) {
                        return com.shannonmanifold.backend.dto.VerifyResponse.builder()
                                        .verified(false)
                                        .output("임시 파일을 생성하는 과정에서 서버 오류가 발생했습니다: " + e.getMessage())
                                        .build();
                }

                // 4. lean 및 lake 실행 파일 절대 경로 탐색 (PATH 보정)
                String lakePath = getCommandPath("lake");
                String leanPath = getCommandPath("lean");

                try {
                        // lake env lean <파일명> 실행
                        String[] command = {lakePath, "env", leanPath, tempFile.getName()};
                        ProcessBuilder pb = new ProcessBuilder(command);
                        pb.directory(testDir);

                        Process process = pb.start();
                        boolean finished = process.waitFor(15, java.util.concurrent.TimeUnit.SECONDS);

                        if (!finished) {
                                process.destroyForcibly();
                                return com.shannonmanifold.backend.dto.VerifyResponse.builder()
                                                .verified(false)
                                                .output("검증 시간 초과 (15초 초과)")
                                                .build();
                        }

                        // 출력 스트림 읽기
                        String stdout = readStream(process.getInputStream());
                        String stderr = readStream(process.getErrorStream());
                        String output = (stdout + "\n" + stderr).trim();
                        int exitCode = process.exitValue();

                        // 검증 성공 여부 판별
                        // 1. exitCode가 0이고
                        // 2. 출력 결과에 "error:"가 없으며
                        // 3. Lean에서 sorry 가설을 사용한 경고("warning: declaration uses `sorry`" 등)가 없는 경우 성공
                        boolean verified = (exitCode == 0);
                        if (output.toLowerCase().contains("error:") || output.contains("uses `sorry`") || output.contains("uses 'sorry'")) {
                                verified = false;
                        }

                        return com.shannonmanifold.backend.dto.VerifyResponse.builder()
                                        .verified(verified)
                                        .output(output.isEmpty() ? "검증 완료 (에러 없음)" : output)
                                        .build();

                } catch (Exception e) {
                        return com.shannonmanifold.backend.dto.VerifyResponse.builder()
                                        .verified(false)
                                        .output("Lean 컴파일러 실행 중 오류가 발생했습니다: " + e.getMessage())
                                        .build();
                } finally {
                        // 임시 파일 삭제
                        try {
                                java.nio.file.Files.deleteIfExists(tempFile.toPath());
                        } catch (java.io.IOException e) {
                                // 로깅 생략
                        }
                }
        }

        private String getCommandPath(String cmd) {
                try {
                        Process p = Runtime.getRuntime().exec(new String[]{cmd, "--version"});
                        p.waitFor();
                        return cmd;
                } catch (Exception e) {
                        String home = System.getProperty("user.home");
                        java.io.File elanCmd = new java.io.File(home, ".elan/bin/" + cmd);
                        if (elanCmd.exists()) {
                                return elanCmd.getAbsolutePath();
                        }
                        return cmd;
                }
        }

        private String readStream(java.io.InputStream is) throws java.io.IOException {
                try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(is, java.nio.charset.StandardCharsets.UTF_8))) {
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                                sb.append(line).append("\n");
                        }
                        return sb.toString();
                }
        }
}
