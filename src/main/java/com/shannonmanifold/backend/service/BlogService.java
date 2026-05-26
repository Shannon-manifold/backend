package com.shannonmanifold.backend.service;

import com.shannonmanifold.backend.dto.BlogPostCreateRequest;
import com.shannonmanifold.backend.dto.BlogPostDetailResponse;
import com.shannonmanifold.backend.dto.BlogPostResponse;
import com.shannonmanifold.backend.dto.BlogPostUpdateRequest;
import com.shannonmanifold.backend.entity.Bookmark;
import com.shannonmanifold.backend.entity.BlogPost;
import com.shannonmanifold.backend.entity.BookmarkType;
import com.shannonmanifold.backend.entity.User;
import com.shannonmanifold.backend.repository.BlogPostRepository;
import com.shannonmanifold.backend.repository.BookmarkRepository;
import com.shannonmanifold.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BlogService {

    private final BlogPostRepository blogPostRepository;
    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;

    public List<BlogPostResponse> getAllPosts() {
        return blogPostRepository.findAllByOrderByDateDesc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<BlogPostResponse> getPostsByCategory(String category) {
        return blogPostRepository.findByCategoryOrderByDateDesc(category).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public BlogPostDetailResponse getPost(Long postId) {
        BlogPost post = findPostOrThrow(postId);
        return toDetailResponse(post);
    }

    @Transactional
    public BlogPostDetailResponse createPost(BlogPostCreateRequest request, String email) {
        User author = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다. Email: " + email));

        BlogPost post = BlogPost.builder()
                .title(request.getTitle())
                .excerpt(request.getExcerpt())
                .authorId(author.getId())
                .authorName(author.getName())
                .date(LocalDate.now())
                .readTime(request.getReadTime())
                .category(request.getCategory())
                .imageUrl(request.getImageUrl())
                .content(request.getContent())
                .build();

        return toDetailResponse(blogPostRepository.save(post));
    }

    @Transactional
    public BlogPostDetailResponse updatePost(Long postId, BlogPostUpdateRequest request, String email) {
        BlogPost post = findPostOrThrow(postId);
        validateAuthor(post.getAuthorId(), email);

        post.update(request.getTitle(), request.getExcerpt(), request.getReadTime(),
                request.getCategory(), request.getImageUrl(), request.getContent());

        return toDetailResponse(post);
    }

    @Transactional
    public void deletePost(Long postId, String email) {
        BlogPost post = findPostOrThrow(postId);
        validateAuthor(post.getAuthorId(), email);
        blogPostRepository.delete(post);
    }

    @Transactional
    public boolean toggleBookmark(Long postId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다. Email: " + email));
        BlogPost post = findPostOrThrow(postId);

        Optional<Bookmark> existing = bookmarkRepository.findByUserAndTargetTypeAndTargetId(user, BookmarkType.blog, postId);
        if (existing.isPresent()) {
            bookmarkRepository.delete(existing.get());
            return false;
        }

        bookmarkRepository.save(Bookmark.builder()
                .user(user)
                .targetType(BookmarkType.blog)
                .targetId(postId)
                .title(post.getTitle())
                .author(post.getAuthorName())
                .build());
        return true;
    }

    private BlogPost findPostOrThrow(Long postId) {
        return blogPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("블로그 포스트를 찾을 수 없습니다. ID: " + postId));
    }

    private void validateAuthor(Long authorId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다. Email: " + email));
        if (!authorId.equals(user.getId())) {
            throw new IllegalStateException("본인이 작성한 글만 수정/삭제할 수 있습니다.");
        }
    }

    private BlogPostResponse toResponse(BlogPost post) {
        return BlogPostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .excerpt(post.getExcerpt())
                .authorName(post.getAuthorName())
                .date(post.getDate())
                .readTime(post.getReadTime())
                .category(post.getCategory())
                .imageUrl(post.getImageUrl())
                .build();
    }

    private BlogPostDetailResponse toDetailResponse(BlogPost post) {
        return BlogPostDetailResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .excerpt(post.getExcerpt())
                .authorId(post.getAuthorId())
                .authorName(post.getAuthorName())
                .date(post.getDate())
                .readTime(post.getReadTime())
                .category(post.getCategory())
                .imageUrl(post.getImageUrl())
                .content(post.getContent())
                .build();
    }
}
