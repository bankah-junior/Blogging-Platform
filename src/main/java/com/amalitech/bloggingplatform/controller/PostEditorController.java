package com.amalitech.bloggingplatform.controller;

import com.amalitech.bloggingplatform.dao.impl.TagDAOImpl;
import com.amalitech.bloggingplatform.model.Post;
import com.amalitech.bloggingplatform.model.Tag;
import com.amalitech.bloggingplatform.service.impl.PostServiceImpl;
import com.amalitech.bloggingplatform.service.impl.TagServiceImpl;
import com.amalitech.bloggingplatform.utils.MongoDBConnection;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.List;

public class PostEditorController {

    @FXML
    private TextField titleField;

    @FXML
    private TextArea contentArea;

    @FXML
    private CheckBox publishedCheckBox;

    @FXML
    private TextField tagsField;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    private Post currentPost;
    private String currentUserId;
    private String currentUsername;
    private HomeController homeController;
    private Runnable refreshCallback;
    private PostServiceImpl postService;
    private TagServiceImpl tagService;

    @FXML
    public void initialize() {
        postService = new PostServiceImpl();
        MongoClient client = MongoDBConnection.connect();
        MongoDatabase db = client.getDatabase("java-demo");
        TagDAOImpl tagDAO = new TagDAOImpl(db);
        tagService = new TagServiceImpl(tagDAO);
    }

    public void setCurrentUser(String userId, String username) {
        this.currentUserId = userId;
        this.currentUsername = username;
    }

    public void setCurrentPost(Post post) {
        this.currentPost = post;
        if (post != null) {
            titleField.setText(post.getTitle());
            contentArea.setText(post.getContent());
            publishedCheckBox.setSelected(post.isPublished());

            // Load existing tags
            try {
                List<Tag> tags = tagService.getTagsByPost(post.getId());
                if (tags != null && !tags.isEmpty()) {
                    String tagNames = String.join(", ", tags.stream()
                        .map(Tag::getName)
                        .toArray(String[]::new));
                    tagsField.setText(tagNames);
                }
            } catch (Exception e) {
                System.err.println("Error loading tags: " + e.getMessage());
            }
        }
    }

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }
    
    public void setRefreshCallback(Runnable refreshCallback) {
        this.refreshCallback = refreshCallback;
    }

    @FXML
    private void onSaveClick() {
        String title = titleField.getText().trim();
        String content = contentArea.getText().trim();

        if (title.isEmpty() || content.isEmpty()) {
            showAlert("Validation Error", "Title and content are required.");
            return;
        }

        try {
            Post post;
            if (currentPost == null) {
                // Create new post
                post = new Post();
                post.setAuthorId(currentUserId);
                post.setTitle(title);
                post.setContent(content);
                post.setPublished(publishedCheckBox.isSelected());
                post = postService.create(post);
            } else {
                // Update existing post
                currentPost.setTitle(title);
                currentPost.setContent(content);
                currentPost.setPublished(publishedCheckBox.isSelected());
                post = postService.update(currentPost);
            }

            if (post != null) {
                // Handle tags
                tagService.unassignAllTagsFromPost(post.getId()); // Clear old tags

                String tagsInput = tagsField.getText().trim();
                if (!tagsInput.isEmpty()) {
                    String[] tagNames = tagsInput.split(",");
                    for (String tagName : tagNames) {
                        tagName = tagName.trim();
                        if (!tagName.isEmpty()) {
                            Tag tag = tagService.getByName(tagName);
                            if (tag == null) {
                                tag = new Tag();
                                tag.setName(tagName);
                                tag = tagService.create(tag);
                            }
                            if (tag != null && tag.getId() != null) {
                                try {
                                    tagService.assignTagToPost(post.getId(), tag.getId());
                                } catch (Exception e) {
                                    // Tag might already be assigned, continue
                                    System.err.println("Tag assignment note: " + e.getMessage());
                                }
                            }
                        }
                    }
                }

                showAlert("Success", currentPost == null ? "Post created successfully!" : "Post updated successfully!");
                closeWindow();
                if (homeController != null) {
                    homeController.refreshPosts();
                } else if (refreshCallback != null) {
                    refreshCallback.run();
                }
            } else {
                showAlert("Error", "Failed to save post.");
            }
        } catch (Exception e) {
            showAlert("Error", "An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onCancelClick() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
