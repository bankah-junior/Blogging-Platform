package com.amalitech.bloggingplatform.service.impl;

import com.amalitech.bloggingplatform.cache.Cache;
import com.amalitech.bloggingplatform.cache.LruCache;
import com.amalitech.bloggingplatform.dao.PostDAO;
import com.amalitech.bloggingplatform.dao.ReviewDAO;
import com.amalitech.bloggingplatform.dao.impl.PostDAOImpl;
import com.amalitech.bloggingplatform.dao.impl.ReviewDAOImpl;
import com.amalitech.bloggingplatform.model.Post;
import com.amalitech.bloggingplatform.model.Review;
import com.amalitech.bloggingplatform.service.PostService;
import com.amalitech.bloggingplatform.utils.MongoDBConnection;
import com.mongodb.client.MongoDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PostServiceImpl implements PostService {
    private final PostDAO postDAO;
    private final ReviewDAO reviewDAO;
    private final Cache<String, Post> postCache;


    public PostServiceImpl() {
        MongoDatabase database = MongoDBConnection.getDatabase();
        this.postDAO = new PostDAOImpl(database);
        this.reviewDAO = new ReviewDAOImpl(database);
        this.postCache = new LruCache<>(50);
    }

    @Override
    public Post create(Post post) {
        Post saved = postDAO.save(post);
        if (saved != null) {
            postCache.put(saved.getId(), saved);
        }
        return saved;
    }

    @Override
    public Post update(Post post) {
        if (postDAO.update(post)) {
            postCache.put(post.getId(), post);
            return post;
        }
        return null;
    }

    @Override
    public boolean delete(String postId) {
        if (postDAO.deleteById(postId)) {
            postCache.remove(postId);
            return true;
        }
        return false;
    }

    @Override
    public Post getById(String postId) {
        // Check cache first
        Post cachedPost = postCache.get(postId);
        if (cachedPost != null) {
            return cachedPost;
        }

        Optional<Post> postOptional = postDAO.findById(postId);
        if (postOptional.isPresent()) {
            Post post = postOptional.get();
            List<Review> reviews = reviewDAO.findByPostId(postId);
            post.setReviews(reviews);
            postCache.put(postId, post); // Add to cache
            return post;
        }
        return null;
    }

    @Override
    public List<Post> getAll() {
        List<Post> posts = postDAO.findAll();
        for (Post post : posts) {
            List<Review> reviews = reviewDAO.findByPostId(post.getId());
            post.setReviews(reviews);
            postCache.put(post.getId(), post);
        }
        return posts;
    }

    @Override
    public List<Post> searchByTitle(String keyword) {
        List<Post> posts = postDAO.searchByTitle(keyword);
        posts.forEach(post -> postCache.put(post.getId(), post));
        return posts;
    }

    @Override
    public List<Post> getByAuthor(String authorId) {
        List<Post> posts = postDAO.findByAuthorId(authorId);
        posts.forEach(post -> postCache.put(post.getId(), post));
        return posts;
    }

    @Override
    public List<Post> searchByTag(String tagName) {
        // TODO: Implement proper tag-based search in DAO
        List<Post> allPosts = postDAO.findAll();
        return allPosts;
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
}
