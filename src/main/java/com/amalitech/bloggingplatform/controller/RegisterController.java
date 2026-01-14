package com.amalitech.bloggingplatform.controller;

import com.amalitech.bloggingplatform.Launcher;
import com.amalitech.bloggingplatform.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
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
    private Label messageLabel;

    @FXML
    protected void onRegisterButtonClick() {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        UserController userController = new UserController();
        User newUser = new User(null, username, email, password, null, null);
        try {
            var user = userController.register(newUser);
            if (user != null) {
                try {
                    messageLabel.setVisible(false);
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
                    messageLabel.setText("OPPS!!! Navigation broken. Try again.");
                    messageLabel.setVisible(true);
                }
            } else {
                messageLabel.setText("Registration failed. Please try again.");
                messageLabel.setVisible(true);
            }
        } catch (Exception e) {
            messageLabel.setText("Error: " + e.getMessage());
            messageLabel.setVisible(true);
            return;
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
}

