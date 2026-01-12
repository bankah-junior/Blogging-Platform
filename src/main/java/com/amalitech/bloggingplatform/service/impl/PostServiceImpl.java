package com.amalitech.bloggingplatform.service.impl;

import com.amalitech.bloggingplatform.cache.Cache;
import com.amalitech.bloggingplatform.cache.LruCache;
import com.amalitech.bloggingplatform.dao.impl.PostDAOImpl;
import com.amalitech.bloggingplatform.model.Post;
import com.amalitech.bloggingplatform.service.PostService;
import com.amalitech.bloggingplatform.utils.MongoDBConnection;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PostServiceImpl implements PostService {
    private final Cache<String, Post> postCache;

    public PostServiceImpl() {
        this.postCache = new LruCache<>(50);
    }

    @Override
    public Post create(Post post) {
        MongoClient client = MongoDBConnection.connect();
        try{
            MongoDatabase db = client.getDatabase("java-demo");
            PostDAOImpl postDAO = new PostDAOImpl(db);
            Post saved = postDAO.save(post);
            postCache.put(saved.getId(), saved);
            return saved;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create post", e);
        } finally {
            client.close();
        }
    }

    @Override
    public Post update(Post post) {
        MongoClient client = MongoDBConnection.connect();
        try{
            MongoDatabase db = client.getDatabase("java-demo");
            PostDAOImpl postDAO = new PostDAOImpl(db);
            if(postDAO.update(post)){
                postCache.put(post.getId(), post);
                return post;
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Failed to update post", e);
        } finally {
            client.close();
        }
    }

    @Override
    public boolean delete(String postId) {
        MongoClient client = MongoDBConnection.connect();
        try{
            MongoDatabase db = client.getDatabase("java-demo");
            PostDAOImpl postDAO = new PostDAOImpl(db);
            if(postDAO.deleteById(postId)){
                postCache.remove(postId);
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete post", e);
        } finally {
            client.close();
        }
    }

    @Override
    public Post getById(String postId) {
        MongoClient client = MongoDBConnection.connect();
        try{
            MongoDatabase db = client.getDatabase("java-demo");
            PostDAOImpl postDAO = new PostDAOImpl(db);
            Optional<Post> posts = postDAO.findById(postId);
            if(posts.isPresent()){
                postCache.put(postId, posts.get());
                return posts.get();
            }
            return postCache.get(postId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get post", e);
        } finally {
            client.close();
        }
    }

    @Override
    public List<Post> getAll() {
        MongoClient client = MongoDBConnection.connect();
        try{
            MongoDatabase db = client.getDatabase("java-demo");
            PostDAOImpl postDAO = new PostDAOImpl(db);
            return postDAO.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get all posts", e);
        } finally {
            client.close();
        }
    }

    @Override
    public List<Post> searchByTitle(String keyword) {
        MongoClient client = MongoDBConnection.connect();
        try{
            MongoDatabase db = client.getDatabase("java-demo");
            PostDAOImpl postDAO = new PostDAOImpl(db);
            List<Post> posts = postDAO.searchByTitle(keyword);
            posts.forEach(post -> postCache.put(post.getId(), post));
            return posts;
        } catch (Exception e) {
            throw new RuntimeException("Failed to search posts", e);
        } finally {
            client.close();
        }
    }

    @Override
    public List<Post> getByAuthor(String authorId) {
        MongoClient client = MongoDBConnection.connect();
        try{
            MongoDatabase db = client.getDatabase("java-demo");
            PostDAOImpl postDAO = new PostDAOImpl(db);
            List<Post> posts = postDAO.findByAuthorId(authorId);
            posts.forEach(post -> postCache.put(post.getId(), post));
            return posts;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get posts by author", e);
        } finally {
            client.close();
        }
    }

    @Override
    public List<Post> searchByTag(String tagName) {
        MongoClient client = MongoDBConnection.connect();
        try{
            MongoDatabase db = client.getDatabase("java-demo");
            PostDAOImpl postDAO = new PostDAOImpl(db);
            // This will need to be implemented in DAO to search posts by tag
            // For now, return all posts and filter by tag in service layer
            List<Post> allPosts = postDAO.findAll();
            // TODO: Implement proper tag-based search in DAO
            return allPosts;
        } catch (Exception e) {
            throw new RuntimeException("Failed to search posts by tag", e);
        } finally {
            client.close();
        }
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