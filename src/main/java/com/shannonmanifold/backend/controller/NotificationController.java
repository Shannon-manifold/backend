package com.shannonmanifold.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    @GetMapping
    public ResponseEntity<?> getNotifications() {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{notificationId}/read")
    public ResponseEntity<?> readNotification(@PathVariable Long notificationId) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/read-all")
    public ResponseEntity<?> readAllNotifications() {
        return ResponseEntity.ok().build();
    }
}
