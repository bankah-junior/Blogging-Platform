package com.amalitech.bloggingplatform.model.dto;

import com.amalitech.bloggingplatform.model.dto.response.*;
import com.amalitech.bloggingplatform.model.entity.*;

import java.util.List;
import java.util.stream.Collectors;

public final class DtoMapper {

    private DtoMapper() {}

    public static UserResponse toUserResponseWithoutToken(User u) {
        if (u == null) return null;
        return new UserResponse(u.getId(), u.getUsername(), u.getEmail(), u.getCreatedAt(), u.getUpdatedAt(), null);
    }

    public static List<UserResponse> toUserResponsesWithoutToken(List<User> users) {
        return users == null ? List.of() : users.stream().map(DtoMapper::toUserResponseWithoutToken).collect(Collectors.toList());
    }

    public static PostResponse toPostResponse(Post p) {
        if (p == null) return null;
        String authorId = p.getAuthorId() != null ? p.getAuthorId() : null;
        return new PostResponse(p.getId(), authorId, p.getTitle(), p.getContent(), p.isPublished(), p.getCreatedAt(), p.getUpdatedAt());
    }

    public static List<PostResponse> toPostResponses(List<Post> posts) {
        return posts == null ? List.of() : posts.stream().map(DtoMapper::toPostResponse).collect(Collectors.toList());
    }

    public static CommentResponse toCommentResponse(Comment c) {
        if (c == null) return null;
        String postId = c.getPostId() != null ? c.getPostId() : null;
        String userId = c.getUserId() != null ? c.getUserId() : null;
        return new CommentResponse(c.getId(), postId, userId, c.getContent(), c.getCreatedAt(), c.getUpdatedAt());
    }

    public static List<CommentResponse> toCommentResponses(List<Comment> comments) {
        return comments == null ? List.of() : comments.stream().map(DtoMapper::toCommentResponse).collect(Collectors.toList());
    }

    public static TagResponse toTagResponse(Tag t) {
        return t == null ? null : new TagResponse(t.getId(), t.getName());
    }

    public static List<TagResponse> toTagResponses(List<Tag> tags) {
        return tags == null ? List.of() : tags.stream().map(DtoMapper::toTagResponse).collect(Collectors.toList());
    }

    public static ReviewResponse toReviewResponse(Review r) {
        if (r == null) return null;
        String postId = r.getPostId() != null ? r.getPostId() : null;
        String userId = r.getUserId() != null ? r.getUserId() : null;
        return new ReviewResponse(r.getId(), postId, userId, r.getRating(), r.getFeedback(), r.getCreatedAt(), r.getUpdatedAt());
    }

    public static List<ReviewResponse> toReviewResponses(List<Review> reviews) {
        return reviews == null ? List.of() : reviews.stream().map(DtoMapper::toReviewResponse).collect(Collectors.toList());
    }

    public static PostTagResponse toPostTagResponse(PostTag pt) {
        if (pt == null) return null;
        return new PostTagResponse(pt.getId(), pt.getPostId(), pt.getTagId());
    }

    public static List<PostTagResponse> toPostTagResponses(List<PostTag> postTags) {
        return postTags == null ? List.of() : postTags.stream().map(DtoMapper::toPostTagResponse).collect(Collectors.toList());
    }
}
