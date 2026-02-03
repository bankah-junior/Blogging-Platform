package com.amalitech.bloggingplatform.service.impl;

import com.amalitech.bloggingplatform.cache.Cache;
import com.amalitech.bloggingplatform.cache.LruCache;
import com.amalitech.bloggingplatform.model.dto.request.CreatePostRequest;
import com.amalitech.bloggingplatform.model.dto.request.UpdatePostRequest;
import com.amalitech.bloggingplatform.model.dto.response.PageResponse;
import com.amalitech.bloggingplatform.model.dto.response.PostResponse;
import com.amalitech.bloggingplatform.model.entity.Post;
import com.amalitech.bloggingplatform.service.PostService;
import com.amalitech.bloggingplatform.utils.apis.ApiClient;
import com.amalitech.bloggingplatform.utils.apis.ApiResponse;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PostServiceImpl implements PostService {
    private final Cache<String, Post> postCache;
    private final Gson gson;


    public PostServiceImpl(Cache<String, Post> postCache) {
        this.postCache = postCache;
        this.gson = new Gson();
    }

    public PostServiceImpl() {
        this(new LruCache<>(50));
    }

    @Override
    public Post create(Post post) {
        if (post == null) {
            return null;
        }

        CreatePostRequest request = new CreatePostRequest();
        request.setAuthorId(post.getAuthorId());
        request.setTitle(post.getTitle());
        request.setContent(post.getContent());
        request.setPublished(post.isPublished());

        ApiResponse response = ApiClient.createPost(request);
        Post created = parseSinglePostFromResponse(response);
        if (created != null) {
            postCache.put(created.getId(), created);
        }
        return created;
    }

    @Override
    public Post update(Post post) {
        if (post == null || post.getId() == null) {
            return null;
        }

        UpdatePostRequest request = new UpdatePostRequest();
        request.setId(post.getId());
        request.setTitle(post.getTitle());
        request.setContent(post.getContent());
        request.setPublished(post.isPublished());

        ApiResponse response = ApiClient.updatePost(post.getId(), request);
        Post updated = parseSinglePostFromResponse(response);
        if (updated != null) {
            postCache.put(updated.getId(), updated);
        }
        return updated;
    }

    @Override
    public boolean delete(String postId) {
        if (postId == null) {
            return false;
        }
        ApiResponse response = ApiClient.deletePost(postId);
        boolean success = "success".equalsIgnoreCase(response.getStatus());
        if (success) {
            postCache.remove(postId);
        }
        return success;
    }

    @Override
    public Post getById(String postId) {
        // Check cache first
        Post cachedPost = postCache.get(postId);
        if (cachedPost != null) {
            return cachedPost;
        }

        ApiResponse response = ApiClient.getPostById(postId);
        Post post = parseSinglePostFromResponse(response);
        if (post != null) {
            postCache.put(postId, post);
        }
        return post;
    }

    @Override
    public List<Post> getAll() {
        ApiResponse response = ApiClient.getAllPosts(0, 100);
        PageResponse<PostResponse> page = parsePostPageFromResponse(response);
        if (page == null || page.getContent() == null) {
            return Collections.emptyList();
        }
        List<Post> posts = toPostEntities(page.getContent());
        for (Post post : posts) {
            if (post.getId() != null) {
                postCache.put(post.getId(), post);
            }
        }
        return posts;
    }

    @Override
    public List<Post> searchByTitle(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return Collections.emptyList();
        }
        ApiResponse response = ApiClient.searchPostsByTitle(keyword);
        List<PostResponse> dtos = parsePostListFromResponse(response);
        List<Post> posts = toPostEntities(dtos);
        posts.forEach(post -> {
            if (post.getId() != null) {
                postCache.put(post.getId(), post);
            }
        });
        return posts;
    }

    @Override
    public List<Post> getByAuthor(String authorId) {
        if (authorId == null || authorId.isBlank()) {
            return Collections.emptyList();
        }
        ApiResponse response = ApiClient.getPostsByAuthor(authorId);
        List<PostResponse> dtos = parsePostListFromResponse(response);
        List<Post> posts = toPostEntities(dtos);
        posts.forEach(post -> {
            if (post.getId() != null) {
                postCache.put(post.getId(), post);
            }
        });
        return posts;
    }

    @Override
    public List<Post> searchByTag(String tagName) {
        // Currently the REST API layer exposes tag-based search via PostApis.
        if (tagName == null || tagName.isBlank()) {
            return Collections.emptyList();
        }
        // Delegate to the API client once a dedicated method is available.
        // For now, fall back to in-memory filtering of all posts.
        List<Post> allPosts = getAll();
        if (allPosts.isEmpty()) {
            return allPosts;
        }
        List<Post> filtered = new ArrayList<>();
        String lower = tagName.toLowerCase();
        for (Post post : allPosts) {
            if (post.getTitle() != null && post.getTitle().toLowerCase().contains(lower)) {
                filtered.add(post);
            }
        }
        return filtered;
    }

    @Override
    public List<Post> sortByDate(List<Post> posts, boolean ascending) {
        List<Post> sorted = new ArrayList<>(posts);
        sorted.sort((p1, p2) -> {
            Long date1 = p1.getCreatedAt() != null ? p1.getCreatedAt() : 0L;
            Long date2 = p2.getCreatedAt() != null ? p2.getCreatedAt() : 0L;
            int comparison = date1.compareTo(date2);
            return ascending ? comparison : -comparison;
        });
        return sorted;
    }

    @Override
    public List<Post> sortByTitle(List<Post> posts, boolean ascending) {
        List<Post> sorted = new ArrayList<>(posts);
        sorted.sort((p1, p2) -> {
            String title1 = p1.getTitle() != null ? p1.getTitle().toLowerCase() : "";
            String title2 = p2.getTitle() != null ? p2.getTitle().toLowerCase() : "";
            int comparison = title1.compareTo(title2);
            return ascending ? comparison : -comparison;
        });
        return sorted;
    }

    @Override
    public List<Post> getAllSorted(String sortBy, boolean ascending) {
        List<Post> posts = getAll();
        switch (sortBy.toLowerCase()) {
            case "date":
                return sortByDate(posts, ascending);
            case "title":
                return sortByTitle(posts, ascending);
            default:
                return posts;
        }
    }

    // ---------- Helpers ----------

    private boolean isSuccess(ApiResponse response) {
        return response != null && "success".equalsIgnoreCase(response.getStatus());
    }

    private Post parseSinglePostFromResponse(ApiResponse response) {
        if (!isSuccess(response) || response.getData() == null) {
            return null;
        }
        // First deserialize to PostResponse DTO (handles nested objects if present)
        PostResponse dto = gson.fromJson(response.getData(), PostResponse.class);
        if (dto == null) {
            return null;
        }
        // Extract authorId if nested object exists in JSON
        String authorId = extractAuthorIdFromJson(response.getData());
        if (authorId != null) {
            dto.setAuthorId(authorId);
        }
        // Convert DTO to entity
        return new Post(
                dto.getId(),
                dto.getAuthorId(),
                dto.getTitle(),
                dto.getContent(),
                dto.isPublished(),
                dto.getCreatedAt(),
                dto.getUpdatedAt()
        );
    }

    private String extractAuthorIdFromJson(JsonElement json) {
        if (json == null || !json.isJsonObject()) {
            return null;
        }
        com.google.gson.JsonObject obj = json.getAsJsonObject();
        // Check if there's a nested "author" object
        if (obj.has("author") && obj.get("author").isJsonObject()) {
            com.google.gson.JsonObject author = obj.get("author").getAsJsonObject();
            if (author.has("id")) {
                return author.get("id").getAsString();
            }
        }
        // If authorId is already present as a string, return it
        if (obj.has("authorId") && obj.get("authorId").isJsonPrimitive()) {
            return obj.get("authorId").getAsString();
        }
        return null;
    }

    private PageResponse<PostResponse> parsePostPageFromResponse(ApiResponse response) {
        if (!isSuccess(response) || response.getData() == null) {
            return null;
        }
        Type pageType = new TypeToken<PageResponse<PostResponse>>() {}.getType();
        return gson.fromJson(response.getData(), pageType);
    }

    private List<PostResponse> parsePostListFromResponse(ApiResponse response) {
        if (!isSuccess(response) || response.getData() == null) {
            return Collections.emptyList();
        }
        JsonElement data = response.getData();
        List<PostResponse> dtos = new ArrayList<>();
        
        if (data.isJsonArray()) {
            for (JsonElement elem : data.getAsJsonArray()) {
                PostResponse dto = gson.fromJson(elem, PostResponse.class);
                if (dto != null) {
                    // Extract authorId from nested object if present
                    String authorId = extractAuthorIdFromJson(elem);
                    if (authorId != null) {
                        dto.setAuthorId(authorId);
                    }
                    dtos.add(dto);
                }
            }
        }
        return dtos;
    }

    private List<Post> toPostEntities(List<PostResponse> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            return Collections.emptyList();
        }
        List<Post> posts = new ArrayList<>();
        for (PostResponse dto : dtos) {
            if (dto == null) continue;
            Post post = new Post(
                    dto.getId(),
                    dto.getAuthorId(),
                    dto.getTitle(),
                    dto.getContent(),
                    dto.isPublished(),
                    dto.getCreatedAt(),
                    dto.getUpdatedAt()
            );
            posts.add(post);
        }
        return posts;
    }
}
