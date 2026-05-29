package com.shannonmanifold.backend.controller;

import com.shannonmanifold.backend.config.SecurityUtils;
import com.shannonmanifold.backend.dto.UserResponse;
import com.shannonmanifold.backend.entity.User;
import com.shannonmanifold.backend.entity.UserRole;
import com.shannonmanifold.backend.entity.Proof;
import com.shannonmanifold.backend.entity.QnaAnswer;
import com.shannonmanifold.backend.entity.QnaQuestion;
import com.shannonmanifold.backend.repository.UserRepository;
import com.shannonmanifold.backend.repository.ProofRepository;
import com.shannonmanifold.backend.repository.QnaAnswerRepository;
import com.shannonmanifold.backend.repository.QnaQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 유저 관련 API를 처리하는 컨트롤러
 * 담당자: 유승민
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final ProofRepository proofRepository;
    private final QnaAnswerRepository qnaAnswerRepository;
    private final QnaQuestionRepository qnaQuestionRepository;

    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile() {
        String email = SecurityUtils.getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("로그인한 사용자 정보를 찾을 수 없습니다."));
        return ResponseEntity.ok(user);
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateMyProfile(@RequestBody Map<String, String> request) {
        String email = SecurityUtils.getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("로그인한 사용자 정보를 찾을 수 없습니다."));
        return ResponseEntity.ok("나의 프로필 수정 성공");
    }

    @GetMapping("/me/activities")
    public ResponseEntity<?> getMyActivities() {
        return ResponseEntity.ok("나의 활동 조회 성공");
    }

    @GetMapping("/me/bookmarks")
    public ResponseEntity<?> getMyBookmarks() {
        return ResponseEntity.ok("나의 북마크 조회 성공");
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserProfile(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다. ID: " + userId));
        return ResponseEntity.ok(convertToResponse(user));
    }

    @GetMapping
    public ResponseEntity<?> getContributors() {
        List<User> users = userRepository.findAll();
        List<UserResponse> responses = users.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    private UserResponse convertToResponse(User user) {
        String roleStr = "일반 기여자";
        if (user.getRole() == UserRole.admin) {
            roleStr = "관리자";
        } else if (user.getRole() == UserRole.moderator) {
            roleStr = "중재자";
        } else if (user.getPreferredSystem() != null && !user.getPreferredSystem().isBlank()) {
            roleStr = user.getPreferredSystem() + " 증명 기여자";
        }

        String badge = "Contributor";
        if (user.getRole() == UserRole.admin) {
            badge = "Admin";
        } else if (user.getRole() == UserRole.moderator) {
            badge = "Moderator";
        } else if (user.getStatProofs() >= 100) {
            badge = "Top Prover";
        } else if (user.getStatProofs() >= 50) {
            badge = "Prover";
        } else if (user.getStatAnswers() >= 50) {
            badge = "Helper";
        }

        String initial = user.getName() != null && !user.getName().isEmpty() ? user.getName().substring(0, 1) : "U";
        String field = user.getPreferredSystem() != null && !user.getPreferredSystem().isEmpty() ? user.getPreferredSystem() : "정수론 · 대수학";

        List<String> languages = new ArrayList<>();
        if (user.getPreferredSystem() != null && !user.getPreferredSystem().isBlank()) {
            for (String lang : user.getPreferredSystem().split(",")) {
                languages.add(lang.trim());
            }
        } else {
            languages.add("Lean 4");
        }

        // Achievements
        List<UserResponse.AchievementDto> achievements = new ArrayList<>();
        if (user.getStatProofs() >= 100) {
            achievements.add(UserResponse.AchievementDto.builder().icon("🏆").title("Top Prover").desc("연간 최다 검증 증명").build());
        }
        if (user.getStatProofs() >= 10) {
            achievements.add(UserResponse.AchievementDto.builder().icon("⭐").title("10+ 증명").desc("10개 이상의 증명 기여").build());
        }
        if (user.getStatAnswers() >= 20) {
            achievements.add(UserResponse.AchievementDto.builder().icon("💬").title("헬퍼").desc("20개 이상의 답변").build());
        }
        if (user.getStatPoints() >= 100) {
            achievements.add(UserResponse.AchievementDto.builder().icon("🎯").title("꾸준한 기여").desc("평판 100점 돌파").build());
        }
        achievements.add(UserResponse.AchievementDto.builder().icon("🌱").title("첫 걸음").desc("커뮤니티의 소중한 기여자").build());

        // Recent Activity
        List<UserResponse.RecentActivityDto> recentActivities = new ArrayList<>();
        
        List<Proof> proofs = proofRepository.findByProverId(user.getId());
        for (Proof proof : proofs) {
            recentActivities.add(UserResponse.RecentActivityDto.builder()
                .type("proof")
                .title(proof.getTitle())
                .date(proof.getDate() != null ? proof.getDate().toString() : "")
                .build());
        }

        List<QnaAnswer> answers = qnaAnswerRepository.findByAuthorId(user.getId());
        for (QnaAnswer answer : answers) {
            String title = "답변: " + (answer.getContent().length() > 30 ? answer.getContent().substring(0, 30) + "..." : answer.getContent());
            Optional<QnaQuestion> question = qnaQuestionRepository.findById(answer.getQuestionId());
            if (question.isPresent()) {
                title = "답변: " + question.get().getTitle();
            }
            recentActivities.add(UserResponse.RecentActivityDto.builder()
                .type("answer")
                .title(title)
                .date(answer.getDate() != null ? answer.getDate().toString() : "")
                .build());
        }

        recentActivities.sort((a, b) -> b.getDate().compareTo(a.getDate()));

        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(roleStr)
                .badge(badge)
                .initial(initial)
                .field(field)
                .proofs(user.getStatProofs())
                .answers(user.getStatAnswers())
                .reputation(formatReputation(user.getStatPoints()))
                .joinDate(user.getJoinDate() != null ? user.getJoinDate().toString() : "")
                .bio(user.getBio() != null && !user.getBio().isEmpty() ? user.getBio() : "수학과 형식 증명에 관심이 많습니다.")
                .languages(languages)
                .achievements(achievements)
                .recentActivity(recentActivities)
                .build();
    }

    private String formatReputation(int points) {
        if (points >= 1000) {
            return String.format(Locale.US, "%.1fK", points / 1000.0).replace(".0", "");
        }
        return String.valueOf(points);
    }
}
