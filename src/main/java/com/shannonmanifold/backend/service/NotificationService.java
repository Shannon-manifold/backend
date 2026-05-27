package com.shannonmanifold.backend.service;

import com.shannonmanifold.backend.dto.NotificationResponse;
import com.shannonmanifold.backend.entity.Notification;
import com.shannonmanifold.backend.entity.User;
import com.shannonmanifold.backend.repository.NotificationRepository;
import com.shannonmanifold.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public List<NotificationResponse> getNotifications(String email) {
        User user = findUserOrThrow(email);
        return notificationRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void readNotification(Long notificationId, String email) {
        User user = findUserOrThrow(email);
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("알림을 찾을 수 없습니다. ID: " + notificationId));
        if (!notification.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("본인의 알림만 읽을 수 있습니다.");
        }
        notification.markRead();
    }

    @Transactional
    public void readAllNotifications(String email) {
        User user = findUserOrThrow(email);
        notificationRepository.markAllReadByUser(user);
    }

    private User findUserOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다. Email: " + email));
    }

    private NotificationResponse toResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .type(notification.getType())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .targetType(notification.getTargetType())
                .targetId(notification.getTargetId())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
