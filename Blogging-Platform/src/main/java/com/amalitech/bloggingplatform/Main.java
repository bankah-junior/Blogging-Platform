package com.amalitech.bloggingplatform;

import com.amalitech.bloggingplatform.model.entity.*;
import com.amalitech.bloggingplatform.service.UserService;
import com.amalitech.bloggingplatform.service.impl.*;

public class Main {
    public static void main(String[] args) {
        try {

            UserServiceImpl userService = new UserServiceImpl();
            TagServiceImpl tagService = new TagServiceImpl();
            PostServiceImpl postService = new PostServiceImpl();
            CommentServiceImpl commentService = new CommentServiceImpl();
            ReviewServiceImpl reviewService = new ReviewServiceImpl();

            // Create user
            User forexUser = new User(
                    null,
                    "ForexGuru",
                    "forex@trading.com",
                    "Secure@1",
                    System.currentTimeMillis(),
                    null
            );
            userService.create(forexUser);

            // Create Forex tags
            Tag forexTag = new Tag(null, "Forex");
            Tag tradingTag = new Tag(null, "Trading");
            Tag strategyTag = new Tag(null, "Strategy");
            Tag fundamentalsTag = new Tag(null, "Fundamentals");

            tagService.create(forexTag);
            tagService.create(tradingTag);
            tagService.create(strategyTag);
            tagService.create(fundamentalsTag);

            // Create Forex posts
            Post post1 = new Post(
                    null,
                    forexUser.getId(),
                    "Introduction to Forex Trading",
                    "Learn the basics of Forex markets, currency pairs, and how trading works.",
                    true,
                    System.currentTimeMillis(),
                    null
            );

            Post post2 = new Post(
                    null,
                    forexUser.getId(),
                    "Top 5 Forex Trading Strategies",
                    "Discover scalping, swing trading, breakout strategies, and more.",
                    true,
                    System.currentTimeMillis(),
                    null
            );

            Post post3 = new Post(
                    null,
                    forexUser.getId(),
                    "Before the Charts, Learn the Game",
                    "Indicators, bots, and strategies won’t save you if you don’t understand the basics.",
                    true,
                    System.currentTimeMillis(),
                    null
            );

            postService.create(post1);
            postService.create(post2);
            postService.create(post3);

            // Assign tags to posts
            tagService.assignTagToPost(post1.getId(), forexTag.getId());
            tagService.assignTagToPost(post1.getId(), tradingTag.getId());
            tagService.assignTagToPost(post2.getId(), forexTag.getId());
            tagService.assignTagToPost(post2.getId(), tradingTag.getId());
            tagService.assignTagToPost(post2.getId(), strategyTag.getId());
            tagService.assignTagToPost(post3.getId(), forexTag.getId());
            tagService.assignTagToPost(post3.getId(), tradingTag.getId());
            tagService.assignTagToPost(post3.getId(), strategyTag.getId());
            tagService.assignTagToPost(post3.getId(), fundamentalsTag.getId());

            // Update a Forex post
            post1.setContent("Learn the basics of Forex markets, currency pairs, leverage, and trading sessions.");
            postService.update(post1);

            // Add comment
            Comment comment = new Comment(
                    null,
                    post1.getId(),
                    forexUser.getId(),
                    "Great introduction for beginners!",
                    System.currentTimeMillis(),
                    null
            );
            commentService.create(comment);

            // Add review
            Review review = new Review(
                    null,
                    post1.getId(),
                    forexUser.getId(),
                    5,
                    "Very informative and easy to understand.",
                    System.currentTimeMillis(),
                    null
            );
            reviewService.create(review);

            System.out.println("Forex blogs created and updated successfully!");

        } catch (Exception e) {
            System.err.println("Error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

