package com.Main;

import com.Service.UserService;
import com.Domain.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.Setter;
import java.util.Optional;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    @Setter private UserService userService;
    @Setter private Main mainApp;

    @FXML
    public void initialize() {
        errorLabel.setText("");
    }

    @FXML
    private void onLoginButtonClicked() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        Optional<User> userOpt = userService.authenticate(username, password);
        if (userOpt.isPresent()) {
            Stage currentStage = (Stage) errorLabel.getScene().getWindow();
            currentStage.close();
            mainApp.showMainView();
        } else {
            errorLabel.setText("Invalid credentials!");
        }
    }
}
