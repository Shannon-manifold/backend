# Database Schema (MySQL 8.0)

This schema translates the TypeScript data interfaces into a normalized relational database design, intended for use with MySQL 8.0 and Spring Boot.

## `schema.sql`

```sql
-- ==============================================================================
-- Shannon-manifold Knowledge Sharing Platform - MySQL 8.0 DDL
-- ==============================================================================

-- ------------------------------------------------------------------------------
-- 1. Users (UserProfile)
-- ------------------------------------------------------------------------------
CREATE TABLE `users` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `name` VARCHAR(255) NOT NULL,
  `email` VARCHAR(255) NOT NULL UNIQUE,
  `password` VARCHAR(255) NOT NULL,
  `avatar_url` VARCHAR(255),
  `bio` TEXT,
  `system` VARCHAR(100), -- 주 사용 증명 보조기 (예: Lean 4, Coq)
  `join_date` DATE NOT NULL,
  `role` ENUM('member', 'moderator', 'admin') DEFAULT 'member',
  
  -- Stats
  `stat_proofs` INT DEFAULT 0,
  `stat_answers` INT DEFAULT 0,
  `stat_likes` INT DEFAULT 0,
  `stat_points` INT DEFAULT 0,
  
  -- Notifications
  `noti_email` BOOLEAN DEFAULT TRUE,
  `noti_answer` BOOLEAN DEFAULT TRUE,
  `noti_like` BOOLEAN DEFAULT TRUE,
  `noti_challenge` BOOLEAN DEFAULT TRUE,
  
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------------------------
-- 2. Blog Posts
-- ------------------------------------------------------------------------------
CREATE TABLE `blog_posts` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `title` VARCHAR(255) NOT NULL,
  `excerpt` TEXT,
  `author_id` BIGINT, -- users 테이블 참조
  `author_name` VARCHAR(255), -- 역정규화된 작성자 이름 (옵션)
  `date` DATE NOT NULL,
  `read_time` VARCHAR(50),
  `category` VARCHAR(100),
  `image_url` VARCHAR(255),
  `content` LONGTEXT NOT NULL,
  
  FOREIGN KEY (`author_id`) REFERENCES `users`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------------------------
-- 3. Challenges
-- ------------------------------------------------------------------------------
CREATE TABLE `challenges` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `title` VARCHAR(255) NOT NULL,
  `field` VARCHAR(100),
  `description` TEXT,
  `prize` VARCHAR(100),
  `sponsor_pool` VARCHAR(100),
  `backers` INT DEFAULT 0,
  `progress` INT DEFAULT 0,
  `difficulty` ENUM('Millennium', 'Hard', 'Medium') NOT NULL,
  `proof_system` VARCHAR(100),
  `accent` VARCHAR(50),
  `detailed_description` LONGTEXT,
  `references_json` JSON, -- 레퍼런스 링크 리스트: [{"title": "...", "url": "..."}]
  
  `created_at` DATETIME NOT NULL,
  `updated_at` DATETIME NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------------------------
-- 4. Contributors
-- ------------------------------------------------------------------------------
CREATE TABLE `contributors` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT UNIQUE, -- users 테이블 참조 (실제 서비스 연동 시)
  `name` VARCHAR(255) NOT NULL,
  `role` VARCHAR(100),
  `field` VARCHAR(100),
  `proofs` INT DEFAULT 0,
  `answers` INT DEFAULT 0,
  `reputation` VARCHAR(100),
  `badge` VARCHAR(100),
  `initial` VARCHAR(10),
  `bio` TEXT,
  `join_date` DATE NOT NULL,
  
  `languages_json` JSON, -- 사용 언어 배열: ["Lean 4", "Coq"]
  `achievements_json` JSON, -- 성과 배열: [{"icon": "...", "title": "...", "desc": "..."}]
  
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------------------------
-- 5. Proofs
-- ------------------------------------------------------------------------------
CREATE TABLE `proofs` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `title` VARCHAR(255) NOT NULL,
  `description` TEXT,
  `status` ENUM('verified', 'pending', 'failed') NOT NULL,
  `prover_id` BIGINT, -- users 테이블 참조
  `prover_name` VARCHAR(255), 
  `language` VARCHAR(100),
  `likes` INT DEFAULT 0,
  `comments_count` INT DEFAULT 0,
  `date` DATE NOT NULL,
  `field` VARCHAR(100),
  `latex` TEXT,
  `code` LONGTEXT,
  
  FOREIGN KEY (`prover_id`) REFERENCES `users`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------------------------
-- 6. QnA Questions & Answers
-- ------------------------------------------------------------------------------
CREATE TABLE `qna_questions` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `title` VARCHAR(255) NOT NULL,
  `description` TEXT,
  `author_id` BIGINT, -- users 테이블 참조
  `author_name` VARCHAR(255),
  `date` DATE NOT NULL,
  `views` INT DEFAULT 0,
  `answers_count` INT DEFAULT 0,
  `likes` INT DEFAULT 0,
  `status` ENUM('answered', 'open') NOT NULL,
  `content` LONGTEXT,
  `tags_json` JSON, -- 태그 배열: ["Lean 4", "Inductive"]
  
  FOREIGN KEY (`author_id`) REFERENCES `users`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `qna_answers` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `question_id` BIGINT NOT NULL,
  `author_id` BIGINT,
  `author_name` VARCHAR(255),
  `date` DATE NOT NULL,
  `content` LONGTEXT NOT NULL,
  `likes` INT DEFAULT 0,
  `accepted` BOOLEAN DEFAULT FALSE,
  
  FOREIGN KEY (`question_id`) REFERENCES `qna_questions`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`author_id`) REFERENCES `users`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------------------------
-- 7. Tutorials & Steps
-- ------------------------------------------------------------------------------
CREATE TABLE `tutorials` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `title` VARCHAR(255) NOT NULL,
  `description` TEXT,
  `level` ENUM('Beginner', 'Intermediate', 'Advanced') NOT NULL,
  `duration` VARCHAR(50),
  `lessons_count` INT DEFAULT 0,
  `icon` VARCHAR(50),
  `author_id` BIGINT,
  `author_name` VARCHAR(255),
  `updated_at` DATE NOT NULL,
  
  `prerequisites_json` JSON, -- 선수 지식 배열: ["Set Theory"]
  `tags_json` JSON, -- 태그 배열: ["Logic", "Lean 4"]
  
  FOREIGN KEY (`author_id`) REFERENCES `users`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `tutorial_steps` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `tutorial_id` BIGINT NOT NULL,
  `step_order` INT NOT NULL, -- 튜토리얼 내 순서
  `title` VARCHAR(255) NOT NULL,
  `description` TEXT,
  `explanation` LONGTEXT,
  `starter_code` LONGTEXT,
  `solution` LONGTEXT,
  `hint` TEXT,
  
  FOREIGN KEY (`tutorial_id`) REFERENCES `tutorials`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------------------------
-- 8. User Bookmarks
-- ------------------------------------------------------------------------------
CREATE TABLE `user_bookmarks` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL,
  `target_type` ENUM('proof', 'blog', 'question', 'tutorial') NOT NULL,
  `target_id` BIGINT NOT NULL,
  
  -- Caching fields for performance
  `title` VARCHAR(255),
  `author` VARCHAR(255),
  `system` VARCHAR(100),
  `likes` INT DEFAULT 0,
  
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  
  UNIQUE KEY `idx_user_target` (`user_id`, `target_type`, `target_id`),
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------------------------
-- 9. Tutorial Progress
-- ------------------------------------------------------------------------------
-- Granular step completion
CREATE TABLE `tutorial_completions` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL,
  `step_id` BIGINT NOT NULL,
  `completed_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  
  UNIQUE KEY `idx_user_step` (`user_id`, `step_id`),
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`step_id`) REFERENCES `tutorial_steps`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Overall tutorial status
CREATE TABLE `user_tutorial_progress` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL,
  `tutorial_id` BIGINT NOT NULL,
  `last_accessed_step_id` BIGINT,
  `is_completed` BOOLEAN DEFAULT FALSE,
  `progress_percent` INT DEFAULT 0,
  `completed_at` DATETIME,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  
  UNIQUE KEY `idx_user_tutorial` (`user_id`, `tutorial_id`),
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`tutorial_id`) REFERENCES `tutorials`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------------------------
-- 10. Authentication (Refresh Tokens)
-- ------------------------------------------------------------------------------
CREATE TABLE `refresh_tokens` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL,
  `token` VARCHAR(255) NOT NULL UNIQUE,
  `expiry_date` DATETIME NOT NULL,
  
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
-- ------------------------------------------------------------------------------
-- 11. Notifications (PR #31 대응을 위해 추가 필요)
-- ------------------------------------------------------------------------------
CREATE TABLE `notifications` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL, -- 알림을 받는 사용자 (users 테이블 참조)
  `type` VARCHAR(100) NOT NULL, -- NotificationType 열거형 대응 (예: 'ANSWER', 'LIKE' 등)
  `message` TEXT NOT NULL, -- 알림 본문 내용
  `is_read` BOOLEAN DEFAULT FALSE, -- 읽음 여부 (PR의 읽음 처리 로직 대응)
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------------------------
-- 12. Proof Comments
-- ------------------------------------------------------------------------------
CREATE TABLE `proof_comments` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `proof_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  `content` TEXT NOT NULL,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  
  FOREIGN KEY (`proof_id`) REFERENCES `proofs`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------------------------
-- 13. Blog Comments
-- ------------------------------------------------------------------------------
CREATE TABLE `blog_comments` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `blog_post_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  `content` TEXT NOT NULL,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  
  FOREIGN KEY (`blog_post_id`) REFERENCES `blog_posts`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------------------------
-- 14. Answer Comments
-- ------------------------------------------------------------------------------
CREATE TABLE `answer_comments` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `answer_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  `content` TEXT NOT NULL,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  
  FOREIGN KEY (`answer_id`) REFERENCES `qna_answers`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```
