package com.shannonmanifold.backend.service;

import com.shannonmanifold.backend.dto.AnswerCreateRequest;
import com.shannonmanifold.backend.dto.AnswerResponse;
import com.shannonmanifold.backend.dto.QuestionCreateRequest;
import com.shannonmanifold.backend.dto.QuestionDetailResponse;
import com.shannonmanifold.backend.dto.QuestionResponse;
import com.shannonmanifold.backend.dto.QuestionUpdateRequest;
import com.shannonmanifold.backend.entity.Bookmark;
import com.shannonmanifold.backend.entity.BookmarkType;
import com.shannonmanifold.backend.entity.QnaAnswer;
import com.shannonmanifold.backend.entity.QnaQuestion;
import com.shannonmanifold.backend.entity.QnaStatus;
import com.shannonmanifold.backend.entity.User;
import com.shannonmanifold.backend.entity.Notification;
import com.shannonmanifold.backend.entity.NotificationType;
import com.shannonmanifold.backend.repository.BookmarkRepository;
import com.shannonmanifold.backend.repository.QnaAnswerRepository;
import com.shannonmanifold.backend.repository.QnaQuestionRepository;
import com.shannonmanifold.backend.repository.UserRepository;
import com.shannonmanifold.backend.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionService {

    private final QnaQuestionRepository questionRepository;
    private final QnaAnswerRepository answerRepository;
    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    public List<QuestionResponse> getAllQuestions() {
        return questionRepository.findAllByOrderByDateDesc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public QuestionDetailResponse getQuestion(Long questionId) {
        QnaQuestion question = findQuestionOrThrow(questionId);
        question.incrementViews();
        List<QnaAnswer> answers = answerRepository.findByQuestionIdOrderByAcceptedDescDateAsc(questionId);
        return toDetailResponse(question, answers);
    }

    @Transactional
    public QuestionDetailResponse createQuestion(QuestionCreateRequest request, String email) {
        User author = findUserOrThrow(email);

        QnaQuestion question = QnaQuestion.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .authorId(author.getId())
                .authorName(author.getName())
                .date(LocalDate.now())
                .views(0)
                .answersCount(0)
                .likes(0)
                .status(QnaStatus.open)
                .content(request.getContent())
                .tagsJson(toJson(request.getTags()))
                .build();

        QnaQuestion saved = questionRepository.save(question);
        return toDetailResponse(saved, Collections.emptyList());
    }

    @Transactional
    public QuestionDetailResponse updateQuestion(Long questionId, QuestionUpdateRequest request, String email) {
        QnaQuestion question = findQuestionOrThrow(questionId);
        validateAuthor(question.getAuthorId(), email);

        question.update(request.getTitle(), request.getDescription(), request.getContent(), toJson(request.getTags()));

        List<QnaAnswer> answers = answerRepository.findByQuestionIdOrderByAcceptedDescDateAsc(questionId);
        return toDetailResponse(question, answers);
    }

    @Transactional
    public void deleteQuestion(Long questionId, String email) {
        QnaQuestion question = findQuestionOrThrow(questionId);
        validateAuthor(question.getAuthorId(), email);
        questionRepository.delete(question);
    }

    @Transactional
    public AnswerResponse createAnswer(Long questionId, AnswerCreateRequest request, String email) {
        QnaQuestion question = findQuestionOrThrow(questionId);
        User author = findUserOrThrow(email);

        QnaAnswer answer = QnaAnswer.builder()
                .questionId(questionId)
                .authorId(author.getId())
                .authorName(author.getName())
                .date(LocalDate.now())
                .content(request.getContent())
                .likes(0)
                .accepted(false)
                .build();

        QnaAnswer saved = answerRepository.save(answer);
        question.incrementAnswersCount();

        if (!question.getAuthorId().equals(author.getId())) {
            Optional<User> questionAuthor = userRepository.findById(question.getAuthorId());
            if (questionAuthor.isPresent()) {
                Notification notification = Notification.builder()
                        .user(questionAuthor.get())
                        .type(NotificationType.answer)
                        .title("새 답변")
                        .message("\"" + question.getTitle() + "\" 질문에 답변이 달렸습니다.")
                        .targetType("qna")
                        .targetId(question.getId())
                        .isRead(false)
                        .createdAt(LocalDateTime.now())
                        .build();
                notificationRepository.save(notification);
            }
        }

        return toAnswerResponse(saved);
    }

    @Transactional
    public AnswerResponse acceptAnswer(Long answerId, String email) {
        QnaAnswer answer = findAnswerOrThrow(answerId);
        QnaQuestion question = findQuestionOrThrow(answer.getQuestionId());
        validateAuthor(question.getAuthorId(), email);

        answer.accept();
        question.markAnswered();

        return toAnswerResponse(answer);
    }

    @Transactional
    public int toggleQuestionLike(Long questionId) {
        QnaQuestion question = findQuestionOrThrow(questionId);
        question.incrementLikes();
        return question.getLikes();
    }

    @Transactional
    public int toggleAnswerLike(Long answerId) {
        QnaAnswer answer = findAnswerOrThrow(answerId);
        answer.incrementLikes();
        return answer.getLikes();
    }

    @Transactional
    public boolean toggleBookmark(Long questionId, String email) {
        User user = findUserOrThrow(email);
        QnaQuestion question = findQuestionOrThrow(questionId);

        Optional<Bookmark> existing = bookmarkRepository.findByUserAndTargetTypeAndTargetId(user, BookmarkType.question, questionId);
        if (existing.isPresent()) {
            bookmarkRepository.delete(existing.get());
            return false;
        }

        bookmarkRepository.save(Bookmark.builder()
                .user(user)
                .targetType(BookmarkType.question)
                .targetId(questionId)
                .title(question.getTitle())
                .author(question.getAuthorName())
                .likes(question.getLikes())
                .build());
        return true;
    }

    private QnaQuestion findQuestionOrThrow(Long questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("질문을 찾을 수 없습니다. ID: " + questionId));
    }

    private QnaAnswer findAnswerOrThrow(Long answerId) {
        return answerRepository.findById(answerId)
                .orElseThrow(() -> new IllegalArgumentException("답변을 찾을 수 없습니다. ID: " + answerId));
    }

    private User findUserOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다. Email: " + email));
    }

    private void validateAuthor(Long authorId, String email) {
        User user = findUserOrThrow(email);
        if (!authorId.equals(user.getId())) {
            throw new IllegalStateException("본인이 작성한 글만 수정/삭제할 수 있습니다.");
        }
    }

    private String toJson(List<String> tags) {
        if (tags == null || tags.isEmpty()) return "[]";
        String joined = tags.stream()
                .map(t -> "\"" + t.replace("\\", "\\\\").replace("\"", "\\\"") + "\"")
                .collect(Collectors.joining(","));
        return "[" + joined + "]";
    }

    private List<String> fromJson(String tagsJson) {
        if (tagsJson == null || tagsJson.isBlank() || tagsJson.equals("[]")) return Collections.emptyList();
        String inner = tagsJson.trim();
        inner = inner.substring(1, inner.length() - 1).trim();
        if (inner.isEmpty()) return Collections.emptyList();
        return Arrays.stream(inner.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"))
                .map(s -> s.trim().replaceAll("^\"|\"$", ""))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    private QuestionResponse toResponse(QnaQuestion q) {
        return QuestionResponse.builder()
                .id(q.getId())
                .title(q.getTitle())
                .description(q.getDescription())
                .authorName(q.getAuthorName())
                .date(q.getDate())
                .views(q.getViews())
                .answersCount(q.getAnswersCount())
                .likes(q.getLikes())
                .status(q.getStatus() != null ? q.getStatus().name() : null)
                .tags(fromJson(q.getTagsJson()))
                .build();
    }

    private QuestionDetailResponse toDetailResponse(QnaQuestion q, List<QnaAnswer> answers) {
        return QuestionDetailResponse.builder()
                .id(q.getId())
                .title(q.getTitle())
                .description(q.getDescription())
                .authorId(q.getAuthorId())
                .authorName(q.getAuthorName())
                .date(q.getDate())
                .views(q.getViews())
                .answersCount(q.getAnswersCount())
                .likes(q.getLikes())
                .status(q.getStatus() != null ? q.getStatus().name() : null)
                .content(q.getContent())
                .tags(fromJson(q.getTagsJson()))
                .answers(answers.stream().map(this::toAnswerResponse).collect(Collectors.toList()))
                .build();
    }

    private AnswerResponse toAnswerResponse(QnaAnswer a) {
        return AnswerResponse.builder()
                .id(a.getId())
                .questionId(a.getQuestionId())
                .authorId(a.getAuthorId())
                .authorName(a.getAuthorName())
                .date(a.getDate())
                .content(a.getContent())
                .likes(a.getLikes())
                .accepted(a.isAccepted())
                .build();
    }
}
