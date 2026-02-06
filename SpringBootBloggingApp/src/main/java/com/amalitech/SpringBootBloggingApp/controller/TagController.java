package com.amalitech.SpringBootBloggingApp.controller;

import com.amalitech.SpringBootBloggingApp.model.dto.DtoMapper;
import com.amalitech.SpringBootBloggingApp.model.dto.request.CreateTagRequest;
import com.amalitech.SpringBootBloggingApp.model.dto.response.ApiResponse;
import com.amalitech.SpringBootBloggingApp.model.dto.response.PageResponse;
import com.amalitech.SpringBootBloggingApp.model.dto.response.TagResponse;
import com.amalitech.SpringBootBloggingApp.model.entity.Tag;
import com.amalitech.SpringBootBloggingApp.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tags")
@ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Resource not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
})
public class TagController {
    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping("/all")
    @Operation(summary = "Get all tags", description = "Retrieves tags with optional pagination (page, size).")
    @io.swagger.v3.oas.annotations.tags.Tag(name = "Tag")
    public ResponseEntity<ApiResponse<PageResponse<TagResponse>>> getAllTags(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var pr = tagService.getAllPaginated(page, size);
        var dto = new PageResponse<>(DtoMapper.toTagResponses(pr.getContent()), pr.getPage(), pr.getSize(), pr.getTotalElements());
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @PostMapping("/create")
    @Operation(summary = "Create a new tag", description = "Creates a new tag")
    @io.swagger.v3.oas.annotations.tags.Tag(name = "Tag")
    public ResponseEntity<ApiResponse<TagResponse>> createTag(@Valid @RequestBody CreateTagRequest request) {
        Tag createdTag = tagService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Tag created", DtoMapper.toTagResponse(createdTag)));
    }

    @PostMapping("/{postId}/assign/{tagId}")
    @Operation(summary = "Assign a tag to a post", description = "Assigns a tag to a post")
    @io.swagger.v3.oas.annotations.tags.Tag(name = "Tag")
    public ResponseEntity<ApiResponse<Void>> assignTagToPost(@PathVariable String postId, @PathVariable String tagId) {
        tagService.assignTagToPost(postId, tagId);
        return ResponseEntity.ok(ApiResponse.success("Tag assigned to post", null));
    }

    @GetMapping("/{postId}/assigned")
    @Operation(summary = "Get all tags assigned to a post", description = "Retrieves a list of all tags assigned to a post")
    @io.swagger.v3.oas.annotations.tags.Tag(name = "Tag")
    public ResponseEntity<ApiResponse<List<TagResponse>>> getAllTagsAssignedToPost(@PathVariable String postId) {
        List<Tag> tags = tagService.getTagsByPost(postId);
        return ResponseEntity.ok(ApiResponse.success(DtoMapper.toTagResponses(tags)));
    }

    @PostMapping("/{postId}/unassign/{tagId}")
    @Operation(summary = "Unassign a tag from a post", description = "Unassigns a specific tag from a post")
    @io.swagger.v3.oas.annotations.tags.Tag(name = "Tag")
    public ResponseEntity<ApiResponse<Void>> unassignTagFromPost(@PathVariable String postId, @PathVariable String tagId) {
        tagService.unassignTagFromPost(postId, tagId);
        return ResponseEntity.ok(ApiResponse.success("Tag unassigned from post", null));
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Get tag by name", description = "Retrieves a tag by its name")
    @io.swagger.v3.oas.annotations.tags.Tag(name = "Tag")
    public ResponseEntity<ApiResponse<TagResponse>> getTagByName(@PathVariable String name) {
        Tag tag = tagService.getByName(name);
        if (tag == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Tag not found"));
        return ResponseEntity.ok(ApiResponse.success(DtoMapper.toTagResponse(tag)));
    }
}
