package com.shannonmanifold.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 알림 관련 API를 처리하는 컨트롤러
 * 담당자: 정윤수
 */
@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    @GetMapping
    public ResponseEntity<?> getNotifications() {
        return ResponseEntity.ok("알림 목록 조회 성공");
    }

    @PostMapping("/{notificationId}/read")
    public ResponseEntity<?> readNotification(@PathVariable Long notificationId) {
        return ResponseEntity.ok("알림 읽기 성공");
    }

    @PostMapping("/read-all")
    public ResponseEntity<?> readAllNotifications() {
        return ResponseEntity.ok("모든 알림 읽기 성공");
    }
}
