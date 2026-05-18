package com.shannonmanifold.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
