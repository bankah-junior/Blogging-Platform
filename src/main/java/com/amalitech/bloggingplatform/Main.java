package com.amalitech.bloggingplatform;

import com.amalitech.bloggingplatform.dao.impl.*;
import com.amalitech.bloggingplatform.model.*;
import com.amalitech.bloggingplatform.utils.MongoDBConnection;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

public class Main {
    public static void main(String[] args) {
        MongoClient client = MongoDBConnection.connect();
        try {
            MongoDatabase db = client.getDatabase("java-demo");

            UserDAOImpl userDAO = new UserDAOImpl(db);
            TagDAOImpl tagDAO = new TagDAOImpl(db);
            ReviewDAOImpl reviewDAO = new ReviewDAOImpl(db);
            PostDAOImpl postDAO = new PostDAOImpl(db);
            CommentDAOImpl commentDAO = new CommentDAOImpl(db);

            // Create user
            User forexUser = new User(
                    null,
                    "ForexGuru",
                    "forex@trading.com",
                    "Secure@123.",
                    null,
                    null
            );
            userDAO.save(forexUser);

            // Create Forex tags
            Tag forexTag = new Tag(null, "Forex");
            Tag tradingTag = new Tag(null, "Trading");
            Tag strategyTag = new Tag(null, "Strategy");
            Tag fundamentalsTag = new Tag(null, "Fundamentals");

            tagDAO.save(forexTag);
            tagDAO.save(tradingTag);
            tagDAO.save(strategyTag);
            tagDAO.save(fundamentalsTag);

            // Create Forex posts
            Post post1 = new Post(
                    null,
                    forexUser.getId(),
                    "Introduction to Forex Trading",
                    "Learn the basics of Forex markets, currency pairs, and how trading works.",
                    true,
                    null,
                    null
            );

            Post post2 = new Post(
                    null,
                    forexUser.getId(),
                    "Top 5 Forex Trading Strategies",
                    "Discover scalping, swing trading, breakout strategies, and more.",
                    true,
                    null,
                    null
            );

            Post post3 = new Post(
                    null,
                    forexUser.getId(),
                    "Before the Charts, Learn the Game: Why Trading Fundamentals Are Non-Negotiable",
                    "Indicators, bots, and strategies won’t save you if you don’t understand the basics. Here’s why every trader must master the fundamentals first.",
                    true,
                    null,
                    null
            );

            postDAO.save(post1);
            postDAO.save(post2);
            postDAO.save(post3);

            // Assign tags to posts
            tagDAO.assignTagToPost(post1.getPostId(), forexTag.getId());
            tagDAO.assignTagToPost(post1.getPostId(), tradingTag.getId());
            tagDAO.assignTagToPost(post2.getPostId(), forexTag.getId());
            tagDAO.assignTagToPost(post2.getPostId(), tradingTag.getId());
            tagDAO.assignTagToPost(post2.getPostId(), strategyTag.getId());
            tagDAO.assignTagToPost(post3.getPostId(), forexTag.getId());
            tagDAO.assignTagToPost(post3.getPostId(), tradingTag.getId());
            tagDAO.assignTagToPost(post3.getPostId(), strategyTag.getId());
            tagDAO.assignTagToPost(post3.getPostId(), fundamentalsTag.getId());

            // Update a Forex post
            post1.setContent("Learn the basics of Forex markets, currency pairs, leverage, and trading sessions.");
            postDAO.update(post1);

            // Add comment
            Comment comment = new Comment(
                    null,
                    post1.getPostId(),
                    forexUser.getId(),
                    "Great introduction for beginners!",
                    null,
                    null
            );
            commentDAO.save(comment);

            // Add review
            Review review = new Review(
                    null,
                    post1.getPostId(),
                    forexUser.getId(),
                    5,
                    "Very informative and easy to understand.",
                    null,
                    null
            );
            reviewDAO.save(review);

            System.out.println("Forex blogs created and updated successfully!");

        } catch (Exception e) {
            System.err.println("Error occurred: " + e.getMessage());
            e.printStackTrace();
        } finally {
            client.close();
        }
    }
}

