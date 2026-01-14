package com.amalitech.bloggingplatform.controller;

import com.amalitech.bloggingplatform.Launcher;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
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
    private Label messageLabel;

    @FXML
    protected void onLoginButtonClick() {
        String email = emailField.getText();
        String password = passwordField.getText();
        UserController userController = new UserController();
        try {
            var user = userController.login(email, password);
            if (user != null) {
                try {
                    messageLabel.setVisible(false);
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
                    messageLabel.setText("OPPS!!! Navigation broken. Try again.");
                    System.out.println(e.getMessage());
                    messageLabel.setVisible(true);
                }
            } else {
                messageLabel.setText("No user found with the provided email.");
                messageLabel.setVisible(true);
            }
        } catch (Exception e) {
            messageLabel.setText("Error: " + e.getMessage());
            messageLabel.setVisible(true);
            return;
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
}

