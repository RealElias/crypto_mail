package crypto_mail.gui;

import crypto_mail.model.User;
import crypto_mail.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.Optional;

public class LoginController {

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    private UserService userService;

    public LoginController() {
        userService = new UserService();
    }

    public void createNewUser() {
        WindowController.openNewUserWindow(getClass(), loginField.getScene().getWindow());
    }

    public void authorizeUser() {
        String login = loginField.getText();
        String password = passwordField.getText();
        if (!login.isEmpty() && !password.isEmpty()) {
            Optional<User> user = userService.checkCredentials(login, password);
            if(user.isPresent()) {
                WindowController.openMainWindow(getClass(), loginField.getScene().getWindow(), user.get());
            } else {
                new Alert(Alert.AlertType.WARNING, "Login or password is incorrect!", ButtonType.OK).show();
            }
        }
    }
}
