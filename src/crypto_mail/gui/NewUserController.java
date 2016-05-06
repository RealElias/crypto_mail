package crypto_mail.gui;

import crypto_mail.model.User;
import crypto_mail.service.user.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class NewUserController {

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    private UserService userService;

    public NewUserController() {
        userService = new UserService();
    }

    public void createNewUser() {
        String login = loginField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (!login.matches("([a-zA-Z]+[\\w!@#$%^&*()_+-=]*){4,}")) {
            new Alert(Alert.AlertType.WARNING,
                    "* Login have to be 4 symbols and more\n* Login have to start from letter\n* Can contains only A-Z, a-z, 0-9\n   and !@#$%^&*()_+-= symbols",
                    ButtonType.OK).show();
            return;
        }
        if (!password.matches("[\\w!@#$%^&*()_+-=]{8,}")) {
            new Alert(Alert.AlertType.WARNING,
                    "* Password have to be 8 symbols and more\n* Can contains only A-Z, a-z, 0-9\n   and !@#$%^&*()_+-= symbols",
                    ButtonType.OK).show();
            return;
        }
        if (!password.equals(confirmPassword)) {
            new Alert(Alert.AlertType.WARNING, "Passwords are different!", ButtonType.OK).show();
            return;
        }
        if (!userService.addUser(login, password)) {
            new Alert(Alert.AlertType.WARNING, "User with such login name already\n exist!", ButtonType.OK).show();
        }

        WindowController.openMainWindow(getClass(), loginField.getScene().getWindow(), new User(login, password));
    }
}
