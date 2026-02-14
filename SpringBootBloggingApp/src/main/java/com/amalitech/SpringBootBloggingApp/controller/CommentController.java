package com.amalitech.SpringBootBloggingApp.controller;

import com.amalitech.SpringBootBloggingApp.model.dto.DtoMapper;
import com.amalitech.SpringBootBloggingApp.model.dto.request.CreateCommentRequest;
import com.amalitech.SpringBootBloggingApp.model.dto.request.UpdateCommentRequest;
import com.amalitech.SpringBootBloggingApp.model.dto.response.ApiResponse;
import com.amalitech.SpringBootBloggingApp.model.dto.response.CommentResponse;
import com.amalitech.SpringBootBloggingApp.model.dto.response.PageResponse;
import com.amalitech.SpringBootBloggingApp.model.entity.Comment;
import com.amalitech.SpringBootBloggingApp.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comments")
@ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Resource not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
})
public class CommentController {
    private final CommentService commentService;
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * create a comment
     * @param comment the comment to create
     * @return the created comment if valid, otherwise a bad request response with an error message
     */
    @PostMapping("/create")
    @Operation(summary = "Create a comment", description = "Creates a new comment for a post")
    @SecurityRequirement(name = "Bearer Authentication")
    @Tag(name = "Comment")
    public ResponseEntity<ApiResponse<CommentResponse>> create(@Valid @RequestBody CreateCommentRequest request) {
        Comment createdComment = commentService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Comment created", DtoMapper.toCommentResponse(createdComment)));
    }

    /**
     * Find by id
     * @param commentId the comment id to find
     * @return the comment with the given id if found, otherwise null
     */
    @GetMapping("/{commentId}")
    @Operation(summary = "Find comment by id", description = "Retrieves a comment by its id")
    @Tag(name = "Comment")
    public ResponseEntity<ApiResponse<CommentResponse>> findById(@PathVariable String commentId) {
        Comment comment = commentService.findById(commentId);
        if (comment == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Comment not found"));
        return ResponseEntity.ok(ApiResponse.success(DtoMapper.toCommentResponse(comment)));
    }

    @GetMapping("/all")
    @Operation(summary = "Find all comments", description = "Retrieves comments with optional pagination (page, size).")
    @Tag(name = "Comment")
    public ResponseEntity<ApiResponse<PageResponse<CommentResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var pr = commentService.getAllPaginated(page, size);
        var dto = new PageResponse<>(DtoMapper.toCommentResponses(pr.getContent()), pr.getPage(), pr.getSize(), pr.getTotalElements());
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Find comments by user id", description = "Retrieves comments for a user with optional pagination")
    @Tag(name = "Comment")
    public ResponseEntity<ApiResponse<?>> findByUserId(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResponse<Comment> pr = commentService.getByUserPaginated(userId, page, size);
        PageResponse<CommentResponse> dto = new PageResponse<>(
                DtoMapper.toCommentResponses(pr.getContent()), pr.getPage(), pr.getSize(), pr.getTotalElements());
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @GetMapping("/post/{postId}")
    @Operation(summary = "Find comments by post id", description = "Retrieves comments for a post with optional pagination")
    @Tag(name = "Comment")
    public ResponseEntity<ApiResponse<?>> findByPostId(
            @PathVariable String postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResponse<Comment> pr = commentService.getByPostPaginated(postId, page, size);
        PageResponse<CommentResponse> dto = new PageResponse<>(
                DtoMapper.toCommentResponses(pr.getContent()), pr.getPage(), pr.getSize(), pr.getTotalElements());
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @PutMapping("/update")
    @Operation(summary = "Update a comment", description = "Updates a comment")
    @SecurityRequirement(name = "Bearer Authentication")
    @Tag(name = "Comment")
    public ResponseEntity<ApiResponse<CommentResponse>> update(@Valid @RequestBody UpdateCommentRequest request) {
        Comment existing = commentService.findById(request.getId());
        if (existing == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Comment not found"));
        existing.setContent(request.getContent());
        existing.setUpdatedAt(System.currentTimeMillis());
        boolean updated = commentService.update(existing);
        if (!updated) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Comment not found"));
        return ResponseEntity.ok(ApiResponse.success("Comment updated", DtoMapper.toCommentResponse(existing)));
    }

    @DeleteMapping("/delete/{commentId}")
    @Operation(summary = "Delete a comment", description = "Deletes a comment by its id")
    @SecurityRequirement(name = "Bearer Authentication")
    @Tag(name = "Comment")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String commentId) {
        boolean deleted = commentService.delete(commentId);
        if (!deleted) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Comment not found"));
        return ResponseEntity.ok(ApiResponse.success("Comment deleted", null));
    }
}
