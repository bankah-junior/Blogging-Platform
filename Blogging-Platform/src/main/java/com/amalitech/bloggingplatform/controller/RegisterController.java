package com.amalitech.bloggingplatform.controller;

import com.amalitech.bloggingplatform.Launcher;
import com.amalitech.bloggingplatform.model.entity.User;
import com.amalitech.bloggingplatform.utils.ValidationUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class RegisterController {
    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    protected void onRegisterButtonClick() {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // --- Input Validation ---
        if (!ValidationUtils.isNotBlank(username) || !ValidationUtils.isNotBlank(email) || !ValidationUtils.isNotBlank(password)) {
            showAlert("Validation Error", "All fields are required.");
            return;
        }
        if (!ValidationUtils.isUsernameValid(username)) {
            showAlert("Validation Error", "Username must be 3-20 characters long and can only contain letters, numbers, and underscores.");
            return;
        }
        if (!ValidationUtils.isEmailValid(email)) {
            showAlert("Validation Error", "Please enter a valid email address.");
            return;
        }
        if (!ValidationUtils.isPasswordValid(password)) {
            showAlert("Validation Error", "Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one digit, and one special character (@$!%*?&).");
            return;
        }
        if (!password.equals(confirmPassword)) {
            showAlert("Validation Error", "Passwords do not match.");
            return;
        }
        // --- End of Validation ---

        UserController userController = new UserController();
        User newUser = new User(null, username, email, password, null, null);
        try {
            var user = userController.register(newUser);
            if (user != null) {
                try {
                    Stage stage = (Stage) usernameField.getScene().getWindow();
                    FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("home.fxml"));
                    Scene scene = new Scene(fxmlLoader.load());
                    HomeController homeController = fxmlLoader.getController();
                    homeController.setUsername(user.getUsername());
                    stage.setTitle("Home Screen");
                    stage.setScene(scene);
                    stage.setMinWidth(800);
                    stage.setMinHeight(600);
                    stage.centerOnScreen();
                    stage.show();
                } catch (IOException e) {
                    showAlert("Navigation Error", "Oops! Navigation to home screen failed. Please try logging in.");
                }
            } else {
                showAlert("Registration Failed", "Registration failed. The email or username might already be taken.");
            }
        } catch (Exception e) {
            showAlert("Registration Error", "An error occurred during registration: " + e.getMessage());
        }
    }

    @FXML
    protected void onLoginButtonClick() throws IOException {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.show();
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

