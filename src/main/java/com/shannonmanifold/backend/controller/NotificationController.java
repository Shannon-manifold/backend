package com.shannonmanifold.backend.controller;

import com.shannonmanifold.backend.config.SecurityUtils;
import com.shannonmanifold.backend.dto.NotificationResponse;
import com.shannonmanifold.backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 알림 관련 API를 처리하는 컨트롤러
 * 담당자: 정윤수
 */
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // 로그인 유저의 알림 목록 조회 (최신순)
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getNotifications() {
        String email = SecurityUtils.getCurrentUserEmail();
        return ResponseEntity.ok(notificationService.getNotifications(email));
    }

    // 특정 알림 읽음 처리: 본인 알림 여부 서비스 레이어에서 검증
    @PostMapping("/{notificationId}/read")
    public ResponseEntity<Void> readNotification(@PathVariable Long notificationId) {
        String email = SecurityUtils.getCurrentUserEmail();
        notificationService.readNotification(notificationId, email);
        return ResponseEntity.noContent().build();
    }

    // 미읽음 알림 전체 읽음 처리 (bulk UPDATE)
    @PostMapping("/read-all")
    public ResponseEntity<Void> readAllNotifications() {
        String email = SecurityUtils.getCurrentUserEmail();
        notificationService.readAllNotifications(email);
        return ResponseEntity.noContent().build();
    }
}
