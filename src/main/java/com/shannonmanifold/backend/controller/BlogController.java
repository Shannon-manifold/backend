package com.shannonmanifold.backend.controller;

import com.shannonmanifold.backend.config.SecurityUtils;
import com.shannonmanifold.backend.dto.BlogPostCreateRequest;
import com.shannonmanifold.backend.dto.BlogPostDetailResponse;
import com.shannonmanifold.backend.dto.BlogPostResponse;
import com.shannonmanifold.backend.dto.BlogPostUpdateRequest;
import com.shannonmanifold.backend.dto.CommentResponse;
import com.shannonmanifold.backend.dto.CommentCreateRequest;
import com.shannonmanifold.backend.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 블로그 관련 API를 처리하는 컨트롤러
 * 담당자: 홍성욱
 */
@RestController
@RequestMapping("/api/v1/blogs")
@RequiredArgsConstructor
public class BlogController {

    private final BlogService blogService;

    @GetMapping
    public ResponseEntity<List<BlogPostResponse>> getBlogs(
            @RequestParam(required = false) String category) {
        List<BlogPostResponse> posts = (category != null && !category.isBlank())
                ? blogService.getPostsByCategory(category)
                : blogService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<BlogPostDetailResponse> getBlog(@PathVariable Long postId) {
        return ResponseEntity.ok(blogService.getPost(postId));
    }

    @PostMapping
    public ResponseEntity<BlogPostDetailResponse> createBlog(@RequestBody BlogPostCreateRequest request) {
        String email = SecurityUtils.getCurrentUserEmail();
        return ResponseEntity.status(201).body(blogService.createPost(request, email));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<BlogPostDetailResponse> updateBlog(
            @PathVariable Long postId,
            @RequestBody BlogPostUpdateRequest request) {
        String email = SecurityUtils.getCurrentUserEmail();
        return ResponseEntity.ok(blogService.updatePost(postId, request, email));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deleteBlog(@PathVariable Long postId) {
        String email = SecurityUtils.getCurrentUserEmail();
        blogService.deletePost(postId, email);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{postId}/bookmarks")
    public ResponseEntity<String> toggleBookmark(@PathVariable Long postId) {
        String email = SecurityUtils.getCurrentUserEmail();
        boolean added = blogService.toggleBookmark(postId, email);
        return ResponseEntity.ok(added ? "블로그 북마크 추가 성공" : "블로그 북마크 제거 성공");
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long postId) {
        List<CommentResponse> comments = blogService.getComments(postId);
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable Long postId,
            @RequestBody CommentCreateRequest request) {
        String email = SecurityUtils.getCurrentUserEmail();
        CommentResponse response = blogService.createComment(postId, request, email);
        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentCreateRequest request) {
        String email = SecurityUtils.getCurrentUserEmail();
        CommentResponse response = blogService.updateComment(commentId, request, email);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        String email = SecurityUtils.getCurrentUserEmail();
        blogService.deleteComment(commentId, email);
        return ResponseEntity.noContent().build();
    }
}
