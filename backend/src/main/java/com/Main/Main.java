package com.Main;

import com.Repository.Manual.UserRepository;
import com.Service.UserService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main extends Application {
    private Stage primaryStage;
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        showLoginView();
    }

    @Override
    public void stop() {
        logger.info("Application is closing");
        System.exit(0);
    }

    public void showLoginView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Main/login-view.fxml"));
            Parent root = loader.load();

            UserService userService = new UserService(new UserRepository());
            LoginController loginController = loader.getController();
            loginController.setUserService(userService);
            loginController.setMainApp(this);

            primaryStage.setTitle("Login");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (Exception e) {
            logger.error("Error loading login view: {}", e.getMessage());
        }
    }

    public void showMainView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Main/main-view.fxml"));
            Parent root = loader.load();
            Stage mainStage = new Stage();
            mainStage.setTitle("Competition Management");
            mainStage.setScene(new Scene(root));
            mainStage.show();
            primaryStage.close();
        } catch (Exception e) {
            logger.error("Error loading main view: {}", e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args); // creates an instance of 'Main' class and calls 'start' method
    }
}
