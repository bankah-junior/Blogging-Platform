package com.amalitech.bloggingplatform.controller;

import com.amalitech.bloggingplatform.Launcher;
import com.amalitech.bloggingplatform.model.Comment;
import com.amalitech.bloggingplatform.model.Post;
import com.amalitech.bloggingplatform.model.User;
import com.amalitech.bloggingplatform.service.impl.CommentServiceImpl;
import com.amalitech.bloggingplatform.service.impl.PostServiceImpl;
import com.amalitech.bloggingplatform.utils.exceptions.UserInputsException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

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

    private Post post;
    private String currentUserId;
    private Runnable refreshCallback;
    private PostServiceImpl postService;
    private CommentServiceImpl commentService;

    public void setData(Post post, String currentUserId, Runnable refreshCallback) {
        this.post = post;
        this.currentUserId = currentUserId;
        this.refreshCallback = refreshCallback;
        this.postService = new PostServiceImpl();
        this.commentService = new CommentServiceImpl();

        try {
            UserController userController = new UserController();
            User author = userController.getById(post.getAuthorId());
            titleLabel.setText(post.getTitle());
            authorLabel.setText("By " + author.getUsername());
            contentLabel.setText(post.getContent());

            // Format time
            if (post.getCreatedAt() != null) {
                LocalDateTime dateTime = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(post.getCreatedAt()),
                    ZoneId.systemDefault()
                );
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH:mm");
                timeLabel.setText(dateTime.format(formatter));
            }

            // Load comment count
            try {
                List<Comment> comments = commentService.getByPost(post.getId());
                commentsLabel.setText(String.valueOf(comments.size()));
            } catch (Exception e) {
                commentsLabel.setText("0");
                System.err.println("Error loading comments: " + e.getMessage());
            }

            // Wire up buttons
            setupButtons();
        } catch (UserInputsException e) {
            System.err.println("Error loading blog item: " + e.getMessage());
        }
    }

    private void setupButtons() {
        // Only show edit/delete if user is the author
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
            // Set a simple refresh callback
            if (refreshCallback != null) {
                controller.setRefreshCallback(refreshCallback);
            }

            Stage stage = new Stage();
            stage.setTitle("Edit Post");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();
            
            // Always refresh after closing the editor
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
                // Refresh comment count
                List<Comment> comments = commentService.getByPost(post.getId());
                commentsLabel.setText(String.valueOf(comments.size()));
                // Refresh the parent view if callback is available
                if (refreshCallback != null) {
                    refreshCallback.run();
                }
            } else {
                showAlert("Error", "Failed to post comment.");
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to post comment: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void onShareClick() {
        // Simple share functionality - copy to clipboard
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
}

