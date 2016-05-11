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
    private TextField inputHostField;

    @FXML
    private TextField inputPortField;

    @FXML
    private TextField outputHostField;

    @FXML
    private TextField outputPortField;

    private Account account;
    private MainController mainController;

    public void initController(MainController controller, Account account) {
        mainController = controller;
        this.account = account;
        if (account != null) {
            nameField.setText(account.getName());
            mailField.setText(account.getAccountSettings().getUser());
            passwordField.setText(account.getAccountSettings().getPass());
            inputHostField.setText(account.getAccountSettings().getInputHost());
            inputPortField.setText(account.getAccountSettings().getInputPort().toString());
            outputHostField.setText(account.getAccountSettings().getOutputHost());
            outputPortField.setText(account.getAccountSettings().getOutputPort().toString());
        }
    }

    public void saveChanges() {
        if (account == null) {
            account = new Account();
        }

        account.setName(nameField.getText());
        account.setAccountSettings(new AccountSettings(mailField.getText(),
                                                    passwordField.getText(),
                                                    inputHostField.getText(),
                                                    Integer.parseInt(inputPortField.getText()),
                                                    outputHostField.getText(),
                                                    Integer.parseInt(outputPortField.getText())));

        mainController.updateAccountSettings(account);

        nameField.getScene().getWindow().hide();
    }

    public void cancelChanges() {
        nameField.getScene().getWindow().hide();
    }
}
