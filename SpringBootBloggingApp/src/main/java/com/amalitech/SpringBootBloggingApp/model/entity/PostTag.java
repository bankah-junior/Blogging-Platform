package com.amalitech.SpringBootBloggingApp.model.entity;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "post_tags")
@CompoundIndex(name = "post_tag_unique", def = "{'postId': 1, 'tagId': 1}", unique = true)
public class PostTag {

    @Id
    private String id;

    @Indexed
    @NotNull
    private String postId;

    @Indexed
    @NotNull
    private String tagId;

    public PostTag() {}

    public PostTag(String postId, String tagId) {
        this.postId = postId;
        this.tagId = tagId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPostId() { return postId; }
    public void setPostId(String postId) { this.postId = postId; }

    public String getTagId() { return tagId; }
    public void setTagId(String tagId) { this.tagId = tagId; }
}
