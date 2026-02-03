package com.amalitech.bloggingplatform.controller;

import com.amalitech.bloggingplatform.Launcher;
import com.amalitech.bloggingplatform.model.entity.Comment;
import com.amalitech.bloggingplatform.model.entity.Post;
import com.amalitech.bloggingplatform.model.entity.Review;
import com.amalitech.bloggingplatform.model.entity.User;
import com.amalitech.bloggingplatform.model.entity.Tag;
import com.amalitech.bloggingplatform.service.impl.CommentServiceImpl;
import com.amalitech.bloggingplatform.service.impl.PostServiceImpl;
import com.amalitech.bloggingplatform.service.impl.ReviewServiceImpl;
import com.amalitech.bloggingplatform.service.impl.TagServiceImpl;
import com.amalitech.bloggingplatform.utils.exceptions.UserInputsException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class BlogItemController {

    @FXML public Button sharePostButton;
    @FXML public Button editPostButton;
    @FXML public Button deletePostButton;
    @FXML public Button postCommentButton;
    @FXML private Label titleLabel;
    @FXML private Label authorLabel;
    @FXML private Label contentLabel;
    @FXML private Label timeLabel;
    @FXML private Label commentsLabel;
    @FXML private TextField commentTextField;
    @FXML private VBox reviewsContainer;
    @FXML private ChoiceBox<Integer> ratingChoiceBox;
    @FXML private TextArea reviewTextArea;
    @FXML private Button submitReviewButton;
    @FXML private VBox commentsContainer;
    @FXML private Separator commentSeparator;
    @FXML private HBox tagsContainer;

    private Post post;
    private String currentUserId;
    private Runnable refreshCallback;
    private PostServiceImpl postService;
    private CommentServiceImpl commentService;
    private ReviewController reviewController;
    private ReviewServiceImpl reviewService;

    public void setData(Post post, String currentUserId, Runnable refreshCallback) {
        this.post = post;
        this.currentUserId = currentUserId;
        this.refreshCallback = refreshCallback;
        this.postService = new PostServiceImpl();
        this.commentService = new CommentServiceImpl();
        this.reviewController = new ReviewController();
        this.reviewService = new ReviewServiceImpl();

        try {
            UserController userController = new UserController();
            User author = userController.getById(post.getAuthorId());
            titleLabel.setText(post.getTitle());
            authorLabel.setText("By " + author.getUsername());
            contentLabel.setText(post.getContent());

            if (post.getCreatedAt() != null) {
                LocalDateTime dateTime = LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(post.getCreatedAt()),
                        ZoneId.systemDefault()
                );
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH:mm");
                timeLabel.setText(dateTime.format(formatter));
            }

            loadTags();
            loadComments();
            setupButtons();
            loadReviews();
            initializeReviewForm();
        } catch (UserInputsException e) {
            System.err.println("Error loading blog item: " + e.getMessage());
        }
    }
    
    private void loadTags() {
        tagsContainer.getChildren().clear();
        try {
            TagServiceImpl tagService = new TagServiceImpl();
            List<Tag> tags = tagService.getTagsByPost(post.getId());
            for (Tag tag : tags) {
                Label tagLabel = new Label("#" + tag.getName());
                tagLabel.setStyle("-fx-background-color: #eef2ff; -fx-text-fill: #4338ca; -fx-padding: 4 8; -fx-background-radius: 6; -fx-font-size: 12px; -fx-font-weight: bold;");
                tagsContainer.getChildren().add(tagLabel);
            }
        } catch (Exception e) {
            System.err.println("Error loading tags: " + e.getMessage());
        }
    }

    private void setupButtons() {
        boolean isAuthor = post.getAuthorId().equals(currentUserId);
        editPostButton.setVisible(isAuthor);
        deletePostButton.setVisible(isAuthor);

        editPostButton.setOnAction(e -> onEditClick());
        deletePostButton.setOnAction(e -> onDeleteClick());
        postCommentButton.setOnAction(e -> onPostCommentClick());
        sharePostButton.setOnAction(e -> onShareClick());
    }

    private void onEditClick() {
        try {
            FXMLLoader loader = new FXMLLoader(Launcher.class.getResource("post-editor.fxml"));
            Parent root = loader.load();
            PostEditorController controller = loader.getController();
            controller.setCurrentPost(post);
            controller.setCurrentUser(currentUserId, null);
            if (refreshCallback != null) {
                controller.setRefreshCallback(refreshCallback);
            }

            Stage stage = new Stage();
            stage.setTitle("Edit Post");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();

            if (refreshCallback != null) {
                refreshCallback.run();
            }
        } catch (IOException e) {
            showAlert("Error", "Failed to open post editor: " + e.getMessage());
        }
    }

    private void onDeleteClick() {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Delete Post");
        confirmAlert.setHeaderText("Are you sure you want to delete this post?");
        confirmAlert.setContentText("This action cannot be undone.");

        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                boolean deleted = postService.delete(post.getId());
                if (deleted) {
                    if (refreshCallback != null) {
                        refreshCallback.run();
                    }
                    showAlert("Success", "Post deleted successfully.");
                } else {
                    showAlert("Error", "Failed to delete post.");
                }
            } catch (Exception e) {
                showAlert("Error", "Failed to delete post: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void onPostCommentClick() {
        String commentText = commentTextField.getText().trim();
        if (commentText.isEmpty()) {
            showAlert("Error", "Please enter a comment.");
            return;
        }

        Comment comment = new Comment();
        comment.setPostId(post.getId());
        comment.setUserId(currentUserId);
        comment.setContent(commentText);

        try {
            Comment created = commentService.create(comment);
            if (created != null && created.getId() != null) {
                commentTextField.clear();
                loadComments();
            } else {
                showAlert("Error", "Failed to post comment.");
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to post comment: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void onShareClick() {
        String shareText = "Check out this post: " + post.getTitle() + "\n" + post.getContent();
        javafx.scene.input.Clipboard.getSystemClipboard().setContent(
                new javafx.scene.input.ClipboardContent() {{
                    putString(shareText);
                }}
        );
        showAlert("Success", "Post content copied to clipboard!");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadComments() {
        commentsContainer.getChildren().clear();
        try {
            List<Comment> comments = commentService.getByPost(post.getId());
            commentsLabel.setText(String.valueOf(comments.size()));
            commentSeparator.setVisible(!comments.isEmpty());
            for (Comment comment : comments) {
                commentsContainer.getChildren().add(createCommentNode(comment));
            }
        } catch (Exception e) {
            commentsLabel.setText("0");
            System.err.println("Error loading comments: " + e.getMessage());
        }
    }

    private Node createCommentNode(Comment comment) {
        VBox commentNode = new VBox(5);
        commentNode.setStyle("-fx-padding: 8; -fx-background-color: #f1f5f9; -fx-background-radius: 8;");

        Label authorLabel = new Label();
        try {
            User author = new UserController().getById(comment.getUserId());
            authorLabel.setText(author.getUsername());
        } catch (UserInputsException e) {
            authorLabel.setText("Unknown user");
        }

        authorLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #4b5563;");
        Label contentLabel = new Label(comment.getContent());
        contentLabel.setWrapText(true);
        contentLabel.setStyle("-fx-text-fill: #374151;");
        commentNode.getChildren().addAll(authorLabel, contentLabel);
        return commentNode;
    }

    private void initializeReviewForm() {
        ratingChoiceBox.getItems().addAll(1, 2, 3, 4, 5);
        ratingChoiceBox.setValue(5);

        if (hasUserReviewed()) {
            disableReviewForm();
        } else {
            submitReviewButton.setOnAction(e -> onSubmitReviewClick());
        }
    }

    private void loadReviews() {
        clearReviews();
        List<Review> reviews = reviewService.getByPost(post.getId());
        for (Review review : reviews) {
            reviewsContainer.getChildren().add(createReviewNode(review));
        }
    }

    private Node createReviewNode(Review review) {
        VBox reviewNode = new VBox(5);
        reviewNode.setStyle("-fx-padding: 8; -fx-background-color: #f1f5f9; -fx-background-radius: 8;");

        Label authorLabel = new Label();
        try {
            User author = new UserController().getById(review.getUserId());
            authorLabel.setText(author.getUsername() + " rated: " + review.getRating() + "/5");
        } catch (UserInputsException e) {
            authorLabel.setText("Unknown user rated: " + review.getRating() + "/5");
        }

        authorLabel.setStyle("-fx-font-weight: bold;");
        Label feedbackLabel = new Label(review.getFeedback());
        feedbackLabel.setWrapText(true);
        reviewNode.getChildren().addAll(authorLabel, feedbackLabel);
        return reviewNode;
    }

    private void onSubmitReviewClick() {
        if (hasUserReviewed()) {
            showAlert("Info", "You have already reviewed this post.");
            return;
        }

        Integer rating = ratingChoiceBox.getValue();
        String feedback = reviewTextArea.getText().trim();

        if (rating == null || feedback.isEmpty()) {
            showAlert("Error", "Please provide a rating and feedback.");
            return;
        }

        Review review = new Review();
        review.setPostId(post.getId());
        review.setUserId(currentUserId);
        review.setRating(rating);
        review.setFeedback(feedback);

        Review createdReview = reviewController.createReview(review);
        if (createdReview != null) {
            showAlert("Success", "Your review has been submitted.");
            loadReviews();
            disableReviewForm();
            if (refreshCallback != null) {
                refreshCallback.run();
            }
        } else {
            showAlert("Error", "Failed to submit your review.");
        }
    }

    private void clearReviews() {
        reviewsContainer.getChildren().clear();
    }

    private boolean hasUserReviewed() {
        List<Review> reviews = reviewService.getByPost(post.getId());
        return reviews.stream().anyMatch(r -> Objects.equals(r.getUserId(), currentUserId));
    }

    private void disableReviewForm() {
        reviewTextArea.setDisable(true);
        ratingChoiceBox.setDisable(true);
        submitReviewButton.setDisable(true);
        reviewTextArea.setPromptText("You have already reviewed this post.");
    }
}

