# Shannon Manifold API Specification

작성일: 2026-05-18  
기준 코드: `src/main/java/com/shannonmanifold/backend/controller`

## 1. 개요

- Base URL: `/api/v1`
- 기본 Content-Type: `application/json`
- 인증 방식: TBD
  - `build.gradle`에는 Spring Security와 JWT 의존성이 포함되어 있으나, 현재 컨트롤러 코드에는 인증/인가 어노테이션이나 사용자 Principal 주입이 없습니다.
  - 아래 명세의 인증 필요 여부는 기능명 기준의 권장값입니다. 실제 보안 정책 구현 시 확정해야 합니다.
- 공통 응답 포맷: TBD
  - 현재 컨트롤러 대부분은 `ResponseEntity.ok().build()` 또는 빈 문자열을 반환하는 스텁 상태입니다.
  - 요청 DTO, 응답 DTO, 에러 응답 스키마는 아직 정의되어 있지 않습니다.

## 2. 공통 규칙

### 2.1 HTTP Status

| Status | 의미 |
| --- | --- |
| `200 OK` | 조회, 수정, 상태 변경 성공 |
| `201 Created` | 리소스 생성 성공 |
| `204 No Content` | 삭제 성공 또는 응답 본문 없는 성공 |
| `400 Bad Request` | 요청 값 검증 실패 |
| `401 Unauthorized` | 인증 실패 또는 토큰 없음 |
| `403 Forbidden` | 권한 없음 |
| `404 Not Found` | 대상 리소스 없음 |
| `409 Conflict` | 중복 요청 또는 상태 충돌 |
| `500 Internal Server Error` | 서버 오류 |

### 2.2 인증 헤더

```http
Authorization: Bearer {accessToken}
```

현재 구현에는 인증 헤더 사용 여부가 반영되어 있지 않습니다. 로그인 이후 보호 API에 적용할 권장 형식입니다.

### 2.3 공통 에러 응답 예시

```json
{
  "code": "RESOURCE_NOT_FOUND",
  "message": "요청한 리소스를 찾을 수 없습니다.",
  "details": {}
}
```

현재 코드에는 공통 에러 응답 타입이 없습니다. 구현 시 위와 같은 일관된 형태를 권장합니다.

## 3. API 목록

| Domain | Method | Path | 설명 |
| --- | --- | --- | --- |
| Auth | `POST` | `/api/v1/auth/register` | 회원가입 |
| Auth | `POST` | `/api/v1/auth/login` | 로그인 |
| Auth | `POST` | `/api/v1/auth/refresh` | 토큰 재발급 |
| Users | `GET` | `/api/v1/users/me` | 내 프로필 조회 |
| Users | `PUT` | `/api/v1/users/me` | 내 프로필 수정 |
| Users | `GET` | `/api/v1/users/me/activities` | 내 활동 조회 |
| Users | `GET` | `/api/v1/users/me/bookmarks` | 내 북마크 조회 |
| Users | `GET` | `/api/v1/users/{userId}` | 사용자 프로필 조회 |
| Users | `GET` | `/api/v1/users` | 기여자 목록 조회 |
| Proofs | `GET` | `/api/v1/proofs` | 증명 목록 조회 |
| Proofs | `GET` | `/api/v1/proofs/{proofId}` | 증명 상세 조회 |
| Proofs | `POST` | `/api/v1/proofs` | 증명 생성 |
| Proofs | `PUT` | `/api/v1/proofs/{proofId}` | 증명 수정 |
| Proofs | `DELETE` | `/api/v1/proofs/{proofId}` | 증명 삭제 |
| Proofs | `POST` | `/api/v1/proofs/{proofId}/verify` | 증명 검증 |
| Proofs | `POST` | `/api/v1/proofs/{proofId}/like` | 증명 좋아요 토글 |
| Proofs | `POST` | `/api/v1/proofs/{proofId}/bookmarks` | 증명 북마크 토글 |
| Questions | `GET` | `/api/v1/questions` | 질문 목록 조회 |
| Questions | `GET` | `/api/v1/questions/{questionId}` | 질문 상세 조회 |
| Questions | `POST` | `/api/v1/questions` | 질문 생성 |
| Questions | `PUT` | `/api/v1/questions/{questionId}` | 질문 수정 |
| Questions | `POST` | `/api/v1/questions/{questionId}/answers` | 답변 생성 |
| Questions | `POST` | `/api/v1/questions/answers/{answerId}/accept` | 답변 채택 |
| Questions | `POST` | `/api/v1/questions/{questionId}/like` | 질문 좋아요 토글 |
| Questions | `POST` | `/api/v1/questions/answers/{answerId}/like` | 답변 좋아요 토글 |
| Tutorials | `GET` | `/api/v1/tutorials` | 튜토리얼 목록 조회 |
| Tutorials | `GET` | `/api/v1/tutorials/{tutorialId}` | 튜토리얼 상세 조회 |
| Tutorials | `GET` | `/api/v1/users/me/tutorials/progress` | 내 튜토리얼 진행도 조회 |
| Tutorials | `POST` | `/api/v1/tutorials/{tutorialId}/steps/{stepId}/complete` | 튜토리얼 단계 완료 |
| Blogs | `GET` | `/api/v1/blogs` | 블로그 글 목록 조회 |
| Blogs | `GET` | `/api/v1/blogs/{postId}` | 블로그 글 상세 조회 |
| Blogs | `POST` | `/api/v1/blogs` | 블로그 글 생성 |
| Blogs | `PUT` | `/api/v1/blogs/{postId}` | 블로그 글 수정 |
| Challenges | `GET` | `/api/v1/challenges` | 챌린지 목록 조회 |
| Challenges | `GET` | `/api/v1/challenges/{challengeId}` | 챌린지 상세 조회 |
| Challenges | `POST` | `/api/v1/challenges/{challengeId}/sponsor` | 챌린지 후원 |
| Notifications | `GET` | `/api/v1/notifications` | 알림 목록 조회 |
| Notifications | `POST` | `/api/v1/notifications/{notificationId}/read` | 알림 읽음 처리 |
| Notifications | `POST` | `/api/v1/notifications/read-all` | 모든 알림 읽음 처리 |

## 4. Auth API

### 4.1 회원가입

| 항목 | 내용 |
| --- | --- |
| Method | `POST` |
| Path | `/api/v1/auth/register` |
| Controller | `AuthController.register()` |
| 인증 | 불필요 권장 |
| Request Body | TBD |
| Response Body | TBD |
| 현재 구현 | `200 OK` 빈 응답 |

요청 예시:

```json
{
  "email": "user@example.com",
  "password": "password",
  "nickname": "shannon"
}
```

성공 응답 예시:

```json
{
  "userId": 1,
  "email": "user@example.com",
  "nickname": "shannon"
}
```

### 4.2 로그인

| 항목 | 내용 |
| --- | --- |
| Method | `POST` |
| Path | `/api/v1/auth/login` |
| Controller | `AuthController.login()` |
| 인증 | 불필요 권장 |
| Request Body | TBD |
| Response Body | TBD |
| 현재 구현 | `200 OK` 빈 응답 |

요청 예시:

```json
{
  "email": "user@example.com",
  "password": "password"
}
```

성공 응답 예시:

```json
{
  "accessToken": "jwt-access-token",
  "refreshToken": "jwt-refresh-token",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```

### 4.3 토큰 재발급

| 항목 | 내용 |
| --- | --- |
| Method | `POST` |
| Path | `/api/v1/auth/refresh` |
| Controller | `AuthController.refresh()` |
| 인증 | Refresh Token 필요 권장 |
| Request Body | TBD |
| Response Body | TBD |
| 현재 구현 | `200 OK` 빈 응답 |

요청 예시:

```json
{
  "refreshToken": "jwt-refresh-token"
}
```

성공 응답 예시:

```json
{
  "accessToken": "new-jwt-access-token",
  "refreshToken": "new-jwt-refresh-token",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```

## 5. Users API

### 5.1 내 프로필 조회

| 항목 | 내용 |
| --- | --- |
| Method | `GET` |
| Path | `/api/v1/users/me` |
| Controller | `UserController.getMyProfile()` |
| 인증 | 필요 권장 |
| Request Body | 없음 |
| Response Body | TBD |
| 현재 구현 | 빈 문자열 반환 스텁 |

성공 응답 예시:

```json
{
  "userId": 1,
  "email": "user@example.com",
  "nickname": "shannon",
  "bio": "정보이론과 수학을 좋아합니다.",
  "profileImageUrl": null
}
```

### 5.2 내 프로필 수정

| 항목 | 내용 |
| --- | --- |
| Method | `PUT` |
| Path | `/api/v1/users/me` |
| Controller | `UserController.updateMyProfile()` |
| 인증 | 필요 권장 |
| Request Body | TBD |
| Response Body | TBD |
| 현재 구현 | 빈 문자열 반환 스텁 |

요청 예시:

```json
{
  "nickname": "new-name",
  "bio": "새 소개",
  "profileImageUrl": "https://example.com/profile.png"
}
```

### 5.3 내 활동 조회

| 항목 | 내용 |
| --- | --- |
| Method | `GET` |
| Path | `/api/v1/users/me/activities` |
| Controller | `UserController.getMyActivities()` |
| 인증 | 필요 권장 |
| Request Body | 없음 |
| Response Body | TBD |
| 현재 구현 | 빈 문자열 반환 스텁 |

### 5.4 내 북마크 조회

| 항목 | 내용 |
| --- | --- |
| Method | `GET` |
| Path | `/api/v1/users/me/bookmarks` |
| Controller | `UserController.getMyBookmarks()` |
| 인증 | 필요 권장 |
| Request Body | 없음 |
| Response Body | TBD |
| 현재 구현 | 빈 문자열 반환 스텁 |

### 5.5 사용자 프로필 조회

| 항목 | 내용 |
| --- | --- |
| Method | `GET` |
| Path | `/api/v1/users/{userId}` |
| Controller | `UserController.getUserProfile(Long userId)` |
| 인증 | 선택 또는 불필요 권장 |
| Request Body | 없음 |
| Response Body | TBD |
| 현재 구현 | 빈 문자열 반환 스텁 |

Path Variables:

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `userId` | `Long` | Y | 조회할 사용자 ID |

### 5.6 기여자 목록 조회

| 항목 | 내용 |
| --- | --- |
| Method | `GET` |
| Path | `/api/v1/users` |
| Controller | `UserController.getContributors()` |
| 인증 | 불필요 권장 |
| Request Body | 없음 |
| Response Body | TBD |
| 현재 구현 | 빈 문자열 반환 스텁 |

## 6. Proofs API

### 6.1 증명 목록 조회

| 항목 | 내용 |
| --- | --- |
| Method | `GET` |
| Path | `/api/v1/proofs` |
| Controller | `ProofController.getProofs()` |
| 인증 | 불필요 또는 선택 권장 |
| Query Parameters | TBD |
| Request Body | 없음 |
| Response Body | TBD |
| 현재 구현 | `200 OK` 빈 응답 |

권장 Query Parameters:

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `page` | `Integer` | N | 페이지 번호 |
| `size` | `Integer` | N | 페이지 크기 |
| `sort` | `String` | N | 정렬 기준 |
| `keyword` | `String` | N | 검색어 |

### 6.2 증명 상세 조회

| 항목 | 내용 |
| --- | --- |
| Method | `GET` |
| Path | `/api/v1/proofs/{proofId}` |
| Controller | `ProofController.getProof(Long proofId)` |
| 인증 | 불필요 또는 선택 권장 |
| Request Body | 없음 |
| Response Body | TBD |
| 현재 구현 | `200 OK` 빈 응답 |

Path Variables:

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `proofId` | `Long` | Y | 증명 ID |

### 6.3 증명 생성

| 항목 | 내용 |
| --- | --- |
| Method | `POST` |
| Path | `/api/v1/proofs` |
| Controller | `ProofController.createProof()` |
| 인증 | 필요 권장 |
| Request Body | TBD |
| Response Body | TBD |
| 현재 구현 | `200 OK` 빈 응답 |

요청 예시:

```json
{
  "title": "채널 용량 정리 증명",
  "content": "증명 본문",
  "tags": ["information-theory", "proof"]
}
```

### 6.4 증명 수정

| 항목 | 내용 |
| --- | --- |
| Method | `PUT` |
| Path | `/api/v1/proofs/{proofId}` |
| Controller | `ProofController.updateProof(Long proofId)` |
| 인증 | 작성자 권한 필요 권장 |
| Request Body | TBD |
| Response Body | TBD |
| 현재 구현 | `200 OK` 빈 응답 |

Path Variables:

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `proofId` | `Long` | Y | 수정할 증명 ID |

### 6.5 증명 삭제

| 항목 | 내용 |
| --- | --- |
| Method | `DELETE` |
| Path | `/api/v1/proofs/{proofId}` |
| Controller | `ProofController.deleteProof(Long proofId)` |
| 인증 | 작성자 또는 관리자 권한 필요 권장 |
| Request Body | 없음 |
| Response Body | 없음 권장 |
| 현재 구현 | `200 OK` 빈 응답 |

Path Variables:

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `proofId` | `Long` | Y | 삭제할 증명 ID |

### 6.6 증명 검증

| 항목 | 내용 |
| --- | --- |
| Method | `POST` |
| Path | `/api/v1/proofs/{proofId}/verify` |
| Controller | `ProofController.verifyProof(Long proofId)` |
| 인증 | 필요 권장 |
| Request Body | TBD |
| Response Body | TBD |
| 현재 구현 | `200 OK` 빈 응답 |

Path Variables:

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `proofId` | `Long` | Y | 검증할 증명 ID |

### 6.7 증명 좋아요 토글

| 항목 | 내용 |
| --- | --- |
| Method | `POST` |
| Path | `/api/v1/proofs/{proofId}/like` |
| Controller | `ProofController.toggleLike(Long proofId)` |
| 인증 | 필요 권장 |
| Request Body | 없음 |
| Response Body | TBD |
| 현재 구현 | `200 OK` 빈 응답 |

Path Variables:

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `proofId` | `Long` | Y | 좋아요를 토글할 증명 ID |

### 6.8 증명 북마크 토글

| 항목 | 내용 |
| --- | --- |
| Method | `POST` |
| Path | `/api/v1/proofs/{proofId}/bookmarks` |
| Controller | `ProofController.toggleBookmark(Long proofId)` |
| 인증 | 필요 권장 |
| Request Body | 없음 |
| Response Body | TBD |
| 현재 구현 | `200 OK` 빈 응답 |

Path Variables:

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `proofId` | `Long` | Y | 북마크를 토글할 증명 ID |

## 7. Questions API

### 7.1 질문 목록 조회

| 항목 | 내용 |
| --- | --- |
| Method | `GET` |
| Path | `/api/v1/questions` |
| Controller | `QuestionController.getQuestions()` |
| 인증 | 불필요 또는 선택 권장 |
| Query Parameters | TBD |
| Request Body | 없음 |
| Response Body | TBD |
| 현재 구현 | `200 OK` 빈 응답 |

### 7.2 질문 상세 조회

| 항목 | 내용 |
| --- | --- |
| Method | `GET` |
| Path | `/api/v1/questions/{questionId}` |
| Controller | `QuestionController.getQuestion(Long questionId)` |
| 인증 | 불필요 또는 선택 권장 |
| Request Body | 없음 |
| Response Body | TBD |
| 현재 구현 | `200 OK` 빈 응답 |

Path Variables:

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `questionId` | `Long` | Y | 질문 ID |

### 7.3 질문 생성

| 항목 | 내용 |
| --- | --- |
| Method | `POST` |
| Path | `/api/v1/questions` |
| Controller | `QuestionController.createQuestion()` |
| 인증 | 필요 권장 |
| Request Body | TBD |
| Response Body | TBD |
| 현재 구현 | `200 OK` 빈 응답 |

요청 예시:

```json
{
  "title": "정리 증명에서 이 단계가 맞나요?",
  "content": "질문 본문",
  "tags": ["proof", "question"]
}
```

### 7.4 질문 수정

| 항목 | 내용 |
| --- | --- |
| Method | `PUT` |
| Path | `/api/v1/questions/{questionId}` |
| Controller | `QuestionController.updateQuestion(Long questionId)` |
| 인증 | 작성자 권한 필요 권장 |
| Request Body | TBD |
| Response Body | TBD |
| 현재 구현 | `200 OK` 빈 응답 |

Path Variables:

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `questionId` | `Long` | Y | 수정할 질문 ID |

### 7.5 답변 생성

| 항목 | 내용 |
| --- | --- |
| Method | `POST` |
| Path | `/api/v1/questions/{questionId}/answers` |
| Controller | `QuestionController.createAnswer(Long questionId)` |
| 인증 | 필요 권장 |
| Request Body | TBD |
| Response Body | TBD |
| 현재 구현 | `200 OK` 빈 응답 |

Path Variables:

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `questionId` | `Long` | Y | 답변을 작성할 질문 ID |

요청 예시:

```json
{
  "content": "답변 본문"
}
```

### 7.6 답변 채택

| 항목 | 내용 |
| --- | --- |
| Method | `POST` |
| Path | `/api/v1/questions/answers/{answerId}/accept` |
| Controller | `QuestionController.acceptAnswer(Long answerId)` |
| 인증 | 질문 작성자 권한 필요 권장 |
| Request Body | 없음 |
| Response Body | TBD |
| 현재 구현 | `200 OK` 빈 응답 |

Path Variables:

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `answerId` | `Long` | Y | 채택할 답변 ID |

### 7.7 질문 좋아요 토글

| 항목 | 내용 |
| --- | --- |
| Method | `POST` |
| Path | `/api/v1/questions/{questionId}/like` |
| Controller | `QuestionController.toggleQuestionLike(Long questionId)` |
| 인증 | 필요 권장 |
| Request Body | 없음 |
| Response Body | TBD |
| 현재 구현 | `200 OK` 빈 응답 |

Path Variables:

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `questionId` | `Long` | Y | 좋아요를 토글할 질문 ID |

### 7.8 답변 좋아요 토글

| 항목 | 내용 |
| --- | --- |
| Method | `POST` |
| Path | `/api/v1/questions/answers/{answerId}/like` |
| Controller | `QuestionController.toggleAnswerLike(Long answerId)` |
| 인증 | 필요 권장 |
| Request Body | 없음 |
| Response Body | TBD |
| 현재 구현 | `200 OK` 빈 응답 |

Path Variables:

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `answerId` | `Long` | Y | 좋아요를 토글할 답변 ID |

## 8. Tutorials API

### 8.1 튜토리얼 목록 조회

| 항목 | 내용 |
| --- | --- |
| Method | `GET` |
| Path | `/api/v1/tutorials` |
| Controller | `TutorialController.getTutorials()` |
| 인증 | 불필요 또는 선택 권장 |
| Request Body | 없음 |
| Response Body | TBD |
| 현재 구현 | 빈 문자열 반환 스텁 |

### 8.2 튜토리얼 상세 조회

| 항목 | 내용 |
| --- | --- |
| Method | `GET` |
| Path | `/api/v1/tutorials/{tutorialId}` |
| Controller | `TutorialController.getTutorial(Long tutorialId)` |
| 인증 | 불필요 또는 선택 권장 |
| Request Body | 없음 |
| Response Body | TBD |
| 현재 구현 | 빈 문자열 반환 스텁 |

Path Variables:

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `tutorialId` | `Long` | Y | 튜토리얼 ID |

### 8.3 내 튜토리얼 진행도 조회

| 항목 | 내용 |
| --- | --- |
| Method | `GET` |
| Path | `/api/v1/users/me/tutorials/progress` |
| Controller | `TutorialController.getMyProgress()` |
| 인증 | 필요 권장 |
| Request Body | 없음 |
| Response Body | TBD |
| 현재 구현 | 빈 문자열 반환 스텁 |

### 8.4 튜토리얼 단계 완료

| 항목 | 내용 |
| --- | --- |
| Method | `POST` |
| Path | `/api/v1/tutorials/{tutorialId}/steps/{stepId}/complete` |
| Controller | `TutorialController.completeStep(Long tutorialId, Long stepId)` |
| 인증 | 필요 권장 |
| Request Body | 없음 |
| Response Body | TBD |
| 현재 구현 | 빈 문자열 반환 스텁 |

Path Variables:

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `tutorialId` | `Long` | Y | 튜토리얼 ID |
| `stepId` | `Long` | Y | 완료 처리할 단계 ID |

## 9. Blogs API

### 9.1 블로그 글 목록 조회

| 항목 | 내용 |
| --- | --- |
| Method | `GET` |
| Path | `/api/v1/blogs` |
| Controller | `BlogController.getBlogs()` |
| 인증 | 불필요 또는 선택 권장 |
| Query Parameters | TBD |
| Request Body | 없음 |
| Response Body | TBD |
| 현재 구현 | `200 OK` 빈 응답 |

### 9.2 블로그 글 상세 조회

| 항목 | 내용 |
| --- | --- |
| Method | `GET` |
| Path | `/api/v1/blogs/{postId}` |
| Controller | `BlogController.getBlog(Long postId)` |
| 인증 | 불필요 또는 선택 권장 |
| Request Body | 없음 |
| Response Body | TBD |
| 현재 구현 | `200 OK` 빈 응답 |

Path Variables:

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `postId` | `Long` | Y | 블로그 글 ID |

### 9.3 블로그 글 생성

| 항목 | 내용 |
| --- | --- |
| Method | `POST` |
| Path | `/api/v1/blogs` |
| Controller | `BlogController.createBlog()` |
| 인증 | 관리자 또는 작성 권한 필요 권장 |
| Request Body | TBD |
| Response Body | TBD |
| 현재 구현 | `200 OK` 빈 응답 |

요청 예시:

```json
{
  "title": "Shannon Manifold 소개",
  "content": "게시글 본문",
  "thumbnailUrl": "https://example.com/thumbnail.png",
  "tags": ["notice"]
}
```

### 9.4 블로그 글 수정

| 항목 | 내용 |
| --- | --- |
| Method | `PUT` |
| Path | `/api/v1/blogs/{postId}` |
| Controller | `BlogController.updateBlog(Long postId)` |
| 인증 | 관리자 또는 작성자 권한 필요 권장 |
| Request Body | TBD |
| Response Body | TBD |
| 현재 구현 | `200 OK` 빈 응답 |

Path Variables:

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `postId` | `Long` | Y | 수정할 블로그 글 ID |

## 10. Challenges API

### 10.1 챌린지 목록 조회

| 항목 | 내용 |
| --- | --- |
| Method | `GET` |
| Path | `/api/v1/challenges` |
| Controller | `ChallengeController.getChallenges()` |
| 인증 | 불필요 또는 선택 권장 |
| Query Parameters | TBD |
| Request Body | 없음 |
| Response Body | TBD |
| 현재 구현 | `200 OK` 빈 응답 |

### 10.2 챌린지 상세 조회

| 항목 | 내용 |
| --- | --- |
| Method | `GET` |
| Path | `/api/v1/challenges/{challengeId}` |
| Controller | `ChallengeController.getChallenge(Long challengeId)` |
| 인증 | 불필요 또는 선택 권장 |
| Request Body | 없음 |
| Response Body | TBD |
| 현재 구현 | `200 OK` 빈 응답 |

Path Variables:

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `challengeId` | `Long` | Y | 챌린지 ID |

### 10.3 챌린지 후원

| 항목 | 내용 |
| --- | --- |
| Method | `POST` |
| Path | `/api/v1/challenges/{challengeId}/sponsor` |
| Controller | `ChallengeController.sponsorChallenge(Long challengeId)` |
| 인증 | 필요 권장 |
| Request Body | TBD |
| Response Body | TBD |
| 현재 구현 | `200 OK` 빈 응답 |

Path Variables:

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `challengeId` | `Long` | Y | 후원할 챌린지 ID |

요청 예시:

```json
{
  "amount": 10000,
  "message": "응원합니다."
}
```

## 11. Notifications API

### 11.1 알림 목록 조회

| 항목 | 내용 |
| --- | --- |
| Method | `GET` |
| Path | `/api/v1/notifications` |
| Controller | `NotificationController.getNotifications()` |
| 인증 | 필요 권장 |
| Query Parameters | TBD |
| Request Body | 없음 |
| Response Body | TBD |
| 현재 구현 | `200 OK` 빈 응답 |

### 11.2 알림 읽음 처리

| 항목 | 내용 |
| --- | --- |
| Method | `POST` |
| Path | `/api/v1/notifications/{notificationId}/read` |
| Controller | `NotificationController.readNotification(Long notificationId)` |
| 인증 | 필요 권장 |
| Request Body | 없음 |
| Response Body | TBD |
| 현재 구현 | `200 OK` 빈 응답 |

Path Variables:

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `notificationId` | `Long` | Y | 읽음 처리할 알림 ID |

### 11.3 모든 알림 읽음 처리

| 항목 | 내용 |
| --- | --- |
| Method | `POST` |
| Path | `/api/v1/notifications/read-all` |
| Controller | `NotificationController.readAllNotifications()` |
| 인증 | 필요 권장 |
| Request Body | 없음 |
| Response Body | TBD |
| 현재 구현 | `200 OK` 빈 응답 |

## 12. 구현 시 확정 필요 항목

1. 요청/응답 DTO 정의
2. 공통 성공/에러 응답 포맷 정의
3. 인증/인가 정책 및 공개 API 범위 확정
4. 페이지네이션/검색/정렬 Query Parameter 규칙 확정
5. 생성 API의 성공 상태를 `200 OK`로 유지할지 `201 Created`로 변경할지 결정
6. 삭제 API의 성공 상태를 `200 OK`로 유지할지 `204 No Content`로 변경할지 결정
7. 토글 API 응답에 현재 상태(`liked`, `bookmarked`)를 포함할지 결정
8. `UserController`, `TutorialController`의 빈 문자열 반환 스텁을 `ResponseEntity` 반환으로 정리
