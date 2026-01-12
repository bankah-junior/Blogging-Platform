package com.amalitech.bloggingplatform;

import com.amalitech.bloggingplatform.dao.impl.*;
import com.amalitech.bloggingplatform.model.*;
import com.amalitech.bloggingplatform.utils.MongoDBConnection;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

public class Main {
    public static void main(String[] args) {
        MongoClient client = MongoDBConnection.connect();
        try{
            MongoDatabase db = client.getDatabase("java-demo");
            UserDAOImpl userDAO = new UserDAOImpl(db);
            TagDAOImpl tagDAO = new TagDAOImpl(db);
            ReviewDAOImpl reviewDAO = new ReviewDAOImpl(db);
            PostDAOImpl postDAO = new PostDAOImpl(db);
            CommentDAOImpl commentDAO = new CommentDAOImpl(db);
            User newUser = new User("695511c7cb68ab517fff18e8", "Bankahs", "bankahs@gmail.com","145::@113cvd", null, null);
            userDAO.save(newUser);
            var allUsers = userDAO.findAll();
            for (int i = 0; i < allUsers.size(); i++) {
                int userIndex = i + 1;
                String userName = allUsers.get(i).getUsername();
                String userEmail = allUsers.get(i).getEmail();
                System.out.println(userIndex + ") Name: " + userName + ", Email: " + userEmail);
            }
            Post newPost = new Post(null, newUser.getId(), "New Psot", "Just a new post.", true, null, null);
            postDAO.save(newPost);
            Tag newTag = new Tag(null, "Demo");
            tagDAO.save(newTag);
            Comment newComment = new Comment(null, newPost.getPostId(), newUser.getId(), "Very Cool", null, null);
            commentDAO.save(newComment);
            Review newReview = new Review(null, newPost.getPostId(), newUser.getId(), 3, "All Clear", null, null);
            reviewDAO.save(newReview);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            client.close(); // close only after all DB work is done
        }
    }
}

