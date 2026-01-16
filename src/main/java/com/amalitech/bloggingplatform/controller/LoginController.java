package com.amalitech.bloggingplatform.controller;

import com.amalitech.bloggingplatform.Launcher;
import com.amalitech.bloggingplatform.utils.ValidationUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    protected void onLoginButtonClick() {
        String email = emailField.getText();
        String password = passwordField.getText();

        // --- Input Validation ---
        if (!ValidationUtils.isNotBlank(email) || !ValidationUtils.isNotBlank(password)) {
            showAlert("Validation Error", "Email and password are required.");
            return;
        }
        if (!ValidationUtils.isEmailValid(email)) {
            showAlert("Validation Error", "Please enter a valid email address.");
            return;
        }
        // --- End of Validation ---

        UserController userController = new UserController();
        try {
            var user = userController.login(email, password);
            if (user != null) {
                try {
                    Stage stage = (Stage) emailField.getScene().getWindow();
                    FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("home.fxml"));
                    Scene scene = new Scene(fxmlLoader.load());
                    HomeController homeController = fxmlLoader.getController();
                    homeController.setCurrentUser(user);
                    stage.setTitle("Home Screen");
                    stage.setScene(scene);
                    stage.setMinWidth(800);
                    stage.setMinHeight(600);
                    stage.centerOnScreen();
                    stage.show();
                } catch (IOException e) {
                    showAlert("Navigation Error", "Oops! Navigation to home screen failed.");
                }
            } else {
                showAlert("Login Failed", "Invalid email or password.");
            }
        } catch (Exception e) {
            showAlert("Login Error", "An error occurred during login: " + e.getMessage());
        }
    }

    @FXML
    protected void onRegisterButtonClick() throws IOException {
        Stage stage = (Stage) emailField.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("register.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Register");
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

