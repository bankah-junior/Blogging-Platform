package com.amalitech.bloggingplatform.controller;

import com.amalitech.bloggingplatform.Launcher;
import com.amalitech.bloggingplatform.model.User;
import com.amalitech.bloggingplatform.service.UserService;
import com.amalitech.bloggingplatform.service.impl.UserServiceImpl;
import com.amalitech.bloggingplatform.utils.ValidationUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader; // Added import
import javafx.scene.Parent; // Added import
import javafx.scene.Scene; // Added import
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException; // Added import

public class ProfileSettingsController {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField currentPasswordField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;
    
    @FXML
    private Button logoutButton; // New logout button

    private User currentUser;
    private UserService userService;

    @FXML
    public void initialize() {
        this.userService = new UserServiceImpl();
    }

    public void setUser(User user) {
        this.currentUser = user;
        if (currentUser != null) {
            usernameField.setText(currentUser.getUsername());
            emailField.setText(currentUser.getEmail());
        }
    }

    @FXML
    private void onSaveClick() {
        String newUsername = usernameField.getText().trim();
        String newEmail = emailField.getText().trim();
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        boolean detailsChanged = !newUsername.equals(currentUser.getUsername()) || !newEmail.equals(currentUser.getEmail());
        boolean passwordChangeAttempted = !currentPassword.isEmpty() || !newPassword.isEmpty() || !confirmPassword.isEmpty();
        boolean success = false;

        // --- Update User Details ---
        if (detailsChanged) {
            if (!ValidationUtils.isUsernameValid(newUsername)) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "New username is not valid (must be 3-60 characters, letters, numbers, ., _, -).");
                return;
            }
            if (!ValidationUtils.isEmailValid(newEmail)) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "New email is not valid.");
                return;
            }
            
            // Temporarily update object to pass to service
            String oldUsername = currentUser.getUsername();
            String oldEmail = currentUser.getEmail();
            currentUser.setUsername(newUsername);
            currentUser.setEmail(newEmail);

            if (userService.updateUserDetails(currentUser)) {
                success = true;
            } else {
                // Revert on failure
                currentUser.setUsername(oldUsername);
                currentUser.setEmail(oldEmail);
                showAlert(Alert.AlertType.ERROR, "Update Failed", "Failed to update user details. The username or email might be taken.");
                return; 
            }
        }

        // --- Change Password ---
        if (passwordChangeAttempted) {
            if (!ValidationUtils.isNotBlank(currentPassword) || !ValidationUtils.isNotBlank(newPassword) || !ValidationUtils.isNotBlank(confirmPassword)) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "To change your password, all three password fields are required.");
                return;
            }
            if (!newPassword.equals(confirmPassword)) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "New passwords do not match.");
                return;
            }
            if (!ValidationUtils.isPasswordValid(newPassword)) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Password must be at least 8 characters and contain an uppercase letter, a lowercase letter, a digit, and a special character.");
                return;
            }
            if (userService.changePassword(currentUser.getId(), currentPassword, newPassword)) {
                success = true;
            } else {
                showAlert(Alert.AlertType.ERROR, "Password Change Failed", "Failed to change password. Please check your current password and try again.");
                return; 
            }
        }

        // --- Final Feedback ---
        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Your profile has been updated successfully.");
            closeWindow();
        } else {
            showAlert(Alert.AlertType.INFORMATION, "No Changes", "No changes were detected.");
        }
    }

    @FXML
    private void onCancelClick() {
        closeWindow();
    }
    
    // New Logout method
    @FXML
    public void onLogoutClick() {
        try {
            Stage stage = (Stage) logoutButton.getScene().getWindow(); // Get current stage
            FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("login.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Login");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load login screen.");
        }
    }
    
    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
