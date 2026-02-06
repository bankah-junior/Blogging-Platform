package com.amalitech.SpringBootBloggingApp.model.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "comments")
@CompoundIndex(name = "post_user_idx", def = "{'post': 1, 'user': 1}")
public class Comment {

    @Id
    private String id;

    @DBRef
    @Indexed
    @NotNull
    private Post post;

    @DBRef
    @Indexed
    @NotNull
    private User user;

    @NotBlank
    @Size(min = 1, max = 1000)
    private String content;

    @Indexed(direction = IndexDirection.DESCENDING)
    private Long createdAt;

    @Indexed(direction = IndexDirection.DESCENDING)
    private Long updatedAt;

    public Comment() {}

    public Comment(String id, Post post, User user, String content, Long createdAt, Long updatedAt) {
        this.id = id;
        this.post = post;
        this.user = user;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Post getPost() { return post; }
    public void setPost(Post post) { this.post = post; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Long getCreatedAt() { return createdAt; }
    public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }

    public Long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Long updatedAt) { this.updatedAt = updatedAt; }
}