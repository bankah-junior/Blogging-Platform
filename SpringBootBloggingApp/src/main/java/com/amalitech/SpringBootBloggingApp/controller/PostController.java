package com.amalitech.SpringBootBloggingApp.controller;

import com.amalitech.SpringBootBloggingApp.model.dto.DtoMapper;
import com.amalitech.SpringBootBloggingApp.model.dto.request.CreatePostRequest;
import com.amalitech.SpringBootBloggingApp.model.dto.request.UpdatePostRequest;
import com.amalitech.SpringBootBloggingApp.model.dto.response.ApiResponse;
import com.amalitech.SpringBootBloggingApp.model.dto.response.PageResponse;
import com.amalitech.SpringBootBloggingApp.model.dto.response.PostResponse;
import com.amalitech.SpringBootBloggingApp.model.entity.Post;
import com.amalitech.SpringBootBloggingApp.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    /**
     * Create a new post
     *
     * @param request the post creation request (authorId, title, content, published)
     * @return the created post as PostResponse
     */
    @PostMapping("/create")
    @Operation(summary = "Create a new post", description = "Creates a new post")
    @Tag(name = "Post")
    public ResponseEntity<ApiResponse<PostResponse>> createPost(@Valid @RequestBody CreatePostRequest request) {
        Post createdPost = postService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Post created", DtoMapper.toPostResponse(createdPost)));
    }

    /**
     * Get all posts sorted by date
     *
     * @param ascending whether to sort in ascending order
     * @return the sorted posts
     */
    @GetMapping("/sort/date")
    @Operation(summary = "Get all posts sorted by date", description = "Retrieves all posts sorted by date")
    @Tag(name = "Post")
    public ResponseEntity<ApiResponse<List<PostResponse>>> getAllSortedByDate(@RequestParam boolean ascending) {
        List<Post> sortedPosts = postService.sortByDate(postService.getAll(), ascending);
        return ResponseEntity.ok(ApiResponse.success(DtoMapper.toPostResponses(sortedPosts)));
    }

    /**
     * Get all posts sorted by title
     *
     * @param ascending whether to sort in ascending order
     * @return the sorted posts
     */
    @GetMapping("/sort/title")
    @Operation(summary = "Get all posts sorted by title", description = "Retrieves all posts sorted by title")
    @Tag(name = "Post")
    public ResponseEntity<ApiResponse<List<PostResponse>>> getAllSortedByTitle(@RequestParam boolean ascending) {
        List<Post> sortedPosts = postService.sortByTitle(postService.getAll(), ascending);
        return ResponseEntity.ok(ApiResponse.success(DtoMapper.toPostResponses(sortedPosts)));
    }

    /**
     * Get all sorted posts
     *
     * @param ascending whether to sort in ascending order
     * @return the sorted posts
     */
    @GetMapping("/sort/all")
    @Operation(summary = "Get all sorted posts", description = "Retrieves all posts sorted by date and title")
    @Tag(name = "Post")
    public ResponseEntity<ApiResponse<List<PostResponse>>> getAllSorted(@RequestParam boolean ascending) {
        List<Post> sortedPosts = postService.getAllSorted("date", ascending);
        return ResponseEntity.ok(ApiResponse.success(DtoMapper.toPostResponses(sortedPosts)));
    }

    /**
     * Update a post
     *
     * @param request the update request (id, title, content, published)
     * @return the updated post as PostResponse
     */
    @PutMapping("/update")
    @Operation(summary = "Update a post", description = "Updates a post")
    @Tag(name = "Post")
    public ResponseEntity<ApiResponse<PostResponse>> updatePost(@Valid @RequestBody UpdatePostRequest request) {
        Post existing = postService.getById(request.getId());
        if (existing == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Post not found"));
        existing.setTitle(request.getTitle());
        existing.setContent(request.getContent());
        existing.setPublished(request.isPublished());
        existing.setUpdatedAt(System.currentTimeMillis());
        Post updatedPost = postService.update(existing);
        return ResponseEntity.ok(ApiResponse.success("Post updated", DtoMapper.toPostResponse(updatedPost)));
    }

    /**
     * Delete a post by ID
     *
     * @param id the ID of the post to delete
     * @return ResponseEntity with no content if successful, or bad request with error message if not
     */
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete a post by ID", description = "Deletes a post by ID")
    @Tag(name = "Post")
    public ResponseEntity<ApiResponse<Void>> deletePostById(@PathVariable String id) {
        postService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Post deleted", null));
    }

    /**
     * Get all posts (optional pagination: page, size).
     */
    @GetMapping("/all")
    @Operation(summary = "Get all posts", description = "Retrieves all posts. Use page and size for pagination.")
    @Tag(name = "Post")
    public ResponseEntity<ApiResponse<?>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResponse<Post> pr = postService.getAllPaginated(page, size);
        PageResponse<PostResponse> dto = new PageResponse<>(
                DtoMapper.toPostResponses(pr.getContent()), pr.getPage(), pr.getSize(), pr.getTotalElements());
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    /**
     * Get a post by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get a post by ID", description = "Retrieves a post by ID")
    @Tag(name = "Post")
    public ResponseEntity<ApiResponse<PostResponse>> getPostById(@PathVariable String id) {
        Post post = postService.getById(id);
        if (post == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Post not found"));
        return ResponseEntity.ok(ApiResponse.success(DtoMapper.toPostResponse(post)));
    }

    @GetMapping("/author/{authorId}")
    @Operation(summary = "Get posts by author ID", description = "Retrieves all posts by a specific author")
    @Tag(name = "Post")
    public ResponseEntity<ApiResponse<List<PostResponse>>> getPostsByAuthorId(@PathVariable String authorId) {
        List<Post> posts = postService.getByAuthor(authorId);
        return ResponseEntity.ok(ApiResponse.success(DtoMapper.toPostResponses(posts)));
    }

    @GetMapping("/search/title/{keyword}")
    @Operation(summary = "Search posts by title", description = "Searches posts by title keyword")
    @Tag(name = "Post")
    public ResponseEntity<ApiResponse<List<PostResponse>>> searchPostsByTitle(@PathVariable String keyword) {
        List<Post> posts = postService.searchByTitle(keyword);
        return ResponseEntity.ok(ApiResponse.success(DtoMapper.toPostResponses(posts)));
    }

    @GetMapping("/search/tag/{tagName}")
    @Operation(summary = "Search posts by tag", description = "Searches posts by tag name")
    @Tag(name = "Post")
    public ResponseEntity<ApiResponse<List<PostResponse>>> searchPostsByTag(@PathVariable String tagName) {
        List<Post> posts = postService.searchByTag(tagName);
        return ResponseEntity.ok(ApiResponse.success(DtoMapper.toPostResponses(posts)));
    }

    @GetMapping("/published/{published}")
    @Operation(summary = "Get posts by published status", description = "Retrieves posts by published status")
    @Tag(name = "Post")
    public ResponseEntity<ApiResponse<List<PostResponse>>> getPostsByPublished(@PathVariable boolean published) {
        List<Post> posts = postService.getByPublished(published);
        return ResponseEntity.ok(ApiResponse.success(DtoMapper.toPostResponses(posts)));
    }
}