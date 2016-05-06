package crypto_mail.gui;

import crypto_mail.model.Account;
import crypto_mail.model.AccountSettings;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class AccountSettingsController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField mailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField hostField;

    @FXML
    private TextField portField;

    private Account account;
    private MainController mainController;

    public void initController(MainController controller, Account account) {
        mainController = controller;
        this.account = account;
        if (account != null) {
            nameField.setText(account.getName());
            mailField.setText(account.getAccountSettings().getUser());
            passwordField.setText(account.getAccountSettings().getPass());
            hostField.setText(account.getAccountSettings().getHost());
            portField.setText(account.getAccountSettings().getPort().toString());
        }
    }

    public void saveChanges() {
        if (account == null) {
            account = new Account();
        }

        account.setName(nameField.getText());
        account.setAccountSettings(new AccountSettings(mailField.getText(),
                                                    passwordField.getText(),
                                                    hostField.getText(),
                                                    Integer.parseInt(portField.getText())));

        mainController.updateAccountSettings(account);

        nameField.getScene().getWindow().hide();
    }

    public void cancelChanges() {
        nameField.getScene().getWindow().hide();
    }
}
