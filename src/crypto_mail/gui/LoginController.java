package crypto_mail.gui;

import crypto_mail.service.user.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

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
            if (userService.checkCredentionals(login, password)) {
                WindowController.openMainWindow(getClass(), loginField.getScene().getWindow());
            } else {
                new Alert(Alert.AlertType.WARNING, "Login or password is incorrect!", ButtonType.OK).show();
            }
        }
    }
}
