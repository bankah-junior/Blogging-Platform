package com.amalitech.SpringBootBloggingApp.model.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "posts")
@CompoundIndexes({
    // Optimize: Get published posts sorted by date (most common query)
    @CompoundIndex(name = "published_created_idx", def = "{'published': 1, 'createdAt': -1}"),
    
    // Optimize: Get posts by author sorted by date
    @CompoundIndex(name = "author_created_idx", def = "{'author': 1, 'createdAt': -1}"),
    
    // Optimize: Get published posts by author
    @CompoundIndex(name = "author_published_idx", def = "{'author': 1, 'published': 1, 'createdAt': -1}")
})
public class Post {

    @Id
    private String id;

    @DBRef
    @Indexed
    @NotNull
    private User author;

    @NotBlank
    @Size(min = 1, max = 200)
    @TextIndexed(weight = 2)  // Higher weight for title in text search
    private String title;

    @NotBlank
    @Size(max = 10000)
    @TextIndexed  // Enable full-text search on content
    private String content;

    @Indexed
    private boolean published;

    @Indexed(direction = IndexDirection.DESCENDING)
    private Long createdAt;

    @Indexed(direction = IndexDirection.DESCENDING)
    private Long updatedAt;

    @DBRef
    private List<Review> reviews;

    public Post() {}

    public Post(String id, User author, String title, String content, boolean published, Long createdAt, Long updatedAt, List<Review> reviews) {
        this.id = id;
        this.author = author;
        this.title = title;
        this.content = content;
        this.published = published;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.reviews = reviews;
    }

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public boolean isPublished() { return published; }
    public void setPublished(boolean published) { this.published = published; }

    public Long getCreatedAt() { return createdAt; }
    public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }

    public Long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Long updatedAt) { this.updatedAt = updatedAt; }

    public List<Review> getReviews() { return reviews; }
    public void setReviews(List<Review> reviews) { this.reviews = reviews; }

}
