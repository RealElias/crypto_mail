package crypto_mail.gui;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    public void createNewUser() {

    }

    public void authorizeUser() {
        System.out.println(loginField.getText());
        System.out.println(passwordField.getText());
    }

}
