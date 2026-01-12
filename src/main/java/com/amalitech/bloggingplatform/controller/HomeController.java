package com.amalitech.bloggingplatform.controller;

import com.amalitech.bloggingplatform.Launcher;
import com.amalitech.bloggingplatform.model.Post;
import com.amalitech.bloggingplatform.model.User;
import com.amalitech.bloggingplatform.service.impl.PostServiceImpl;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class HomeController {

    @FXML
    private ListView<Post> blogListView;

    @FXML
    private Label username;

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private Button newPostButton;

    @FXML
    private Button analyticsButton;

    @FXML
    private Button clearFiltersButton;

    @FXML
    private ComboBox<String> sortComboBox;

    @FXML
    private ComboBox<String> sortOrderComboBox;

    private PostServiceImpl postService;
    private String currentUserId;
    private String currentUsername;
    private List<Post> allPosts;

    @FXML
    public void initialize() {
        postService = new PostServiceImpl();
        allPosts = postService.getAll();
        loadPosts(allPosts);

        // Initialize sort combo boxes
        sortComboBox.getItems().addAll("Date", "Title", "Author");
        sortOrderComboBox.getItems().addAll("Ascending", "Descending");

        // Set up search field to trigger search on Enter
        searchField.setOnAction(e -> onSearchClick());

        // Set up sort combo boxes to trigger sort on change
        sortComboBox.setOnAction(e -> applySorting());
        sortOrderComboBox.setOnAction(e -> applySorting());
    }

    private void loadPosts(List<Post> posts) {
        blogListView.getItems().clear();
        blogListView.getItems().addAll(posts);

        HomeController homeControllerRef = this;
        blogListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Post post, boolean empty) {
                super.updateItem(post, empty);

                if (empty || post == null) {
                    setGraphic(null);
                } else {
                    try {
                        FXMLLoader loader = new FXMLLoader(
                                getClass().getResource("/com/amalitech/bloggingplatform/blog-item.fxml")
                        );
                        Parent root = loader.load();

                        BlogItemController controller = loader.getController();
                        controller.setData(post, currentUserId, homeControllerRef::refreshPosts);

                        setGraphic(root);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @FXML
    public void setUsername(String username) {
        this.username.setText(username);
        this.currentUsername = username;
        // Get user ID from username
        UserController userController = new UserController();
        try {
            List<User> users = userController.getAll();
            for (User user : users) {
                if (user.getUsername().equals(username)) {
                    this.currentUserId = user.getId();
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting user ID: " + e.getMessage());
        }
    }

    public void setCurrentUser(User user) {
        this.currentUserId = user.getId();
        this.currentUsername = user.getUsername();
        this.username.setText(user.getUsername());
    }

    @FXML
    public void onSearchClick() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadPosts(allPosts);
            return;
        }

        List<Post> searchResults = postService.searchByTitle(keyword);
        allPosts = searchResults;
        loadPosts(searchResults);
    }

    @FXML
    public void onClearFiltersClick() {
        searchField.clear();
        sortComboBox.getSelectionModel().clearSelection();
        sortOrderComboBox.getSelectionModel().clearSelection();
        allPosts = postService.getAll();
        loadPosts(allPosts);
    }

    private void applySorting() {
        String sortBy = sortComboBox.getValue();
        String order = sortOrderComboBox.getValue();

        if (sortBy == null || order == null) {
            return;
        }

        boolean ascending = "Ascending".equals(order);
        List<Post> sorted;

        switch (sortBy) {
            case "Date":
                sorted = postService.sortByDate(allPosts, ascending);
                break;
            case "Title":
                sorted = postService.sortByTitle(allPosts, ascending);
                break;
            default:
                sorted = allPosts;
        }

        loadPosts(sorted);
    }

    @FXML
    public void onNewPostClick() {
        try {
            FXMLLoader loader = new FXMLLoader(Launcher.class.getResource("post-editor.fxml"));
            Parent root = loader.load();
            PostEditorController controller = loader.getController();
            controller.setCurrentUser(currentUserId, currentUsername);
            controller.setHomeController(this);

            Stage stage = new Stage();
            stage.setTitle("Create New Post");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            showAlert("Error", "Failed to open post editor: " + e.getMessage());
        }
    }

    @FXML
    public void onAnalyticsClick() {
        try {
            FXMLLoader loader = new FXMLLoader(Launcher.class.getResource("analytics.fxml"));
            if (loader.getLocation() == null) {
                showAlert("Error", "Failed to find analytics.fxml resource file.");
                return;
            }
            Parent root = loader.load();
            AnalyticsController controller = loader.getController();
            if (controller != null) {
                controller.loadAnalytics();
            }

            Stage stage = new Stage();
            stage.setTitle("Performance Analytics");
            stage.setScene(new Scene(root));
            stage.setResizable(true);
            stage.setMinWidth(800);
            stage.setMinHeight(600);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open analytics: " + e.getMessage() + "\n\nCause: " + (e.getCause() != null ? e.getCause().getMessage() : "Unknown"));
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open analytics: " + e.getMessage() + "\n\nCause: " + (e.getCause() != null ? e.getCause().getMessage() : "Unknown"));
        }
    }

    public void refreshPosts() {
        // Refresh posts from database
        allPosts = postService.getAll();
        // Reload the list view
        loadPosts(allPosts);
        // Force UI update
        blogListView.refresh();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

