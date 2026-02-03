package com.amalitech.SpringBootBloggingApp.controller;

import com.amalitech.SpringBootBloggingApp.model.dto.DtoMapper;
import com.amalitech.SpringBootBloggingApp.model.dto.response.ApiResponse;
import com.amalitech.SpringBootBloggingApp.model.dto.response.PageResponse;
import com.amalitech.SpringBootBloggingApp.model.dto.response.PostTagResponse;
import com.amalitech.SpringBootBloggingApp.model.entity.PostTag;
import com.amalitech.SpringBootBloggingApp.service.PostTagService;
import com.amalitech.SpringBootBloggingApp.util.exceptions.UserInputsException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/post-tags")
public class PostTagController {
    private final PostTagService postTagService;
    
    public PostTagController(PostTagService postTagService) {
        this.postTagService = postTagService;
    }

    @PostMapping("/create")
    @Operation(summary = "Create post-tag relationship", description = "Creates a new relationship between a post and a tag")
    @Tag(name = "PostTag")
    public ResponseEntity<ApiResponse<PostTagResponse>> create(@RequestBody PostTag postTag) {
        PostTagResponse created = postTagService.create(postTag);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(created));
    }

    @GetMapping("/all")
    @Operation(summary = "Get all post-tag relationships", description = "Retrieves all post-tag relationships")
    @Tag(name = "PostTag")
    public ResponseEntity<ApiResponse<PageResponse<PostTagResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var pr = postTagService.getAllPaginated(page, size);
        var dto = new PageResponse<>(DtoMapper.toPostTagResponses(pr.getContent()), pr.getPage(), pr.getSize(), pr.getTotalElements());
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @GetMapping("/post/{postId}")
    @Operation(summary = "Get post-tag relationships by post", description = "Retrieves all tags for a specific post")
    @Tag(name = "PostTag")
    public ResponseEntity<ApiResponse<List<PostTagResponse>>> getByPostId(@PathVariable String postId) {
        List<PostTagResponse> postTags = postTagService.getByPostId(postId);
        return ResponseEntity.ok(ApiResponse.success(postTags));
    }

    @GetMapping("/tag/{tagId}")
    @Operation(summary = "Get post-tag relationships by tag", description = "Retrieves all posts for a specific tag")
    @Tag(name = "PostTag")
    public ResponseEntity<ApiResponse<List<PostTagResponse>>> getByTagId(@PathVariable String tagId) {
        List<PostTagResponse> postTags = postTagService.getByTagId(tagId);
        return ResponseEntity.ok(ApiResponse.success(postTags));
    }

    @PostMapping("/{postId}/assign/{tagId}")
    @Operation(summary = "Assign tag to post", description = "Assigns a tag to a post")
    @Tag(name = "PostTag")
    public ResponseEntity<ApiResponse<Void>> assignTagToPost(@PathVariable String postId, @PathVariable String tagId) {
        postTagService.assignTagToPost(postId, tagId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(null));
    }

    @DeleteMapping("/{postId}/unassign/{tagId}")
    @Operation(summary = "Unassign tag from post", description = "Unassigns a specific tag from a post")
    @Tag(name = "PostTag")
    public ResponseEntity<ApiResponse<Void>> unassignTagFromPost(@PathVariable String postId, @PathVariable String tagId) {
        postTagService.unassignTagFromPost(postId, tagId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.success(null));
    }

    @DeleteMapping("/{postId}/unassign-all")
    @Operation(summary = "Unassign all tags from post", description = "Unassigns all tags from a post")
    @Tag(name = "PostTag")
    public ResponseEntity<ApiResponse<Void>> unassignAllTagsFromPost(@PathVariable String postId) {
        postTagService.unassignAllTagsFromPost(postId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.success(null));
    }

    @GetMapping("/{postId}/exists/{tagId}")
    @Operation(summary = "Check if post-tag relationship exists", description = "Checks if a specific post-tag relationship exists")
    @Tag(name = "PostTag")
    public ResponseEntity<ApiResponse<Boolean>> exists(@PathVariable String postId, @PathVariable String tagId) {
        boolean exists = postTagService.exists(postId, tagId);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }
}
