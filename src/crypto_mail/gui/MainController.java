package crypto_mail.gui;

import crypto_mail.model.Account;
import crypto_mail.model.ComposeMailType;
import crypto_mail.model.MailMessage;
import crypto_mail.model.User;
import crypto_mail.service.AccountService;
import crypto_mail.service.MailService;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import javax.mail.Address;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class MainController {
    private User user;
    private List<Account> accounts;
    private AccountService accountService;
    private MailService mailService;

    @FXML
    ListView accountsList;

    @FXML
    ListView foldersList;

    @FXML
    ListView messagesList;

    @FXML
    Button addAccountButton;

    @FXML
    Button editAccountButton;

    @FXML
    Button removeAccountButton;

    @FXML
    TextField subjectField;

    @FXML
    TextField fromField;

    @FXML
    TextField ccField;

    @FXML
    TextField toField;

    @FXML
    TextField dateField;

    @FXML
    TextArea contentArea;

    private Account selectedAccount;

    private List<MailMessage> selectedFolder;

    private MailMessage selectedMessage;

    public MainController() {
        accountService = new AccountService();
        mailService = new MailService();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        accounts = accountService.readAccounts(user);
        initUI();
    }

    private void initUI() {
        updateAccountsList();
        accountsList.getSelectionModel().selectedIndexProperty().addListener((ChangeListener) (observable, oldValue, newValue) -> {
            selectAccount((Integer) newValue);
        });
        foldersList.getSelectionModel().selectedIndexProperty().addListener((ChangeListener) (observable, oldValue, newValue) -> {
            selectFolder((Integer) newValue);
        });
        messagesList.getSelectionModel().selectedIndexProperty().addListener((ChangeListener) (observable, oldValue, newValue) -> {
            selectMessage((Integer) newValue);
        });
    }

    private void selectMessage(Integer index) {
        if(index < 0)
            return;

        selectedMessage = selectedFolder.get(index);

        subjectField.setText(selectedMessage.getSubject());
        fromField.setText(MailService.asString(selectedMessage.getFrom()));

        if (selectedMessage.getRecipients().size() > 1) {
            List<Address> ccAddresses = selectedMessage.getRecipients();
            ccAddresses.remove(0);
            ccField.setText(MailService.asString(ccAddresses));
        }

        LocalDateTime dateTime = LocalDateTime.ofInstant(selectedMessage.getReceivedDate().toInstant(), ZoneId.systemDefault());
        dateField.setText(dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        toField.setText(selectedMessage.getRecipients().get(0).toString());
        contentArea.setText(selectedMessage.getContent());
    }

    private void selectFolder(Integer index) {
        if(index < 0)
            return;

        String folderName = (String) selectedAccount.getFolders().keySet().toArray()[index];
        selectedFolder = selectedAccount.getFolders().get(folderName);

        ObservableList<String> messages = FXCollections.observableArrayList(selectedFolder.stream()
                .map(message -> message.getSubject() + "\n" + message.getContentTitle())
                .collect(Collectors.toList()));
        messagesList.setItems(messages);
    }

    private void selectAccount(Integer index) {
        if(index < 0)
            return;

        this.selectedAccount = accounts.get(index);
        ObservableList<String> folders = FXCollections.observableArrayList(selectedAccount
                .getFolders()
                .entrySet()
                .stream()
                .map(entry -> entry.getKey() + "(" + entry.getValue().size() + ")")
                .collect(Collectors.toList()));
        foldersList.setItems(folders);
        editAccountButton.setDisable(false);
        removeAccountButton.setDisable(false);
    }

    public void getMail() {
        accounts.forEach(mailService::getMail);
        writeAccounts();
    }

    public void addAccount() {
        WindowController.openAccountSettingsWindow(getClass(), null, this);
    }

    private void updateAccountsList() {
        ObservableList<String> accountNames = FXCollections.observableArrayList(accounts.stream().map(Account::getName).collect(Collectors.toList()));
        accountsList.setItems(accountNames);
    }

    public void editAccount() {
        WindowController.openAccountSettingsWindow(getClass(), selectedAccount, this);
        accountService.writeAccounts(accounts, user);
    }

    public void removeAccount() {
        accounts.remove(selectedAccount);
        accountService.writeAccounts(accounts, user);
    }

    public void updateAccountSettings(Account changedAccount) {
        Optional<Account> account = accounts.stream().filter(acc -> acc.getName().equals(changedAccount.getName())).findFirst();
        if (account.isPresent()) {
            account.get().setName(changedAccount.getName());
            account.get().setAccountSettings(changedAccount.getAccountSettings());
        } else {
            accounts.add(changedAccount);
        }
        writeAccounts();
    }

    private void writeAccounts() {
        accountService.writeAccounts(accounts, user);
        updateAccountsList();
    }

    public void composeNewMail() {
        WindowController.openComposeMailWindow(getClass(), selectedAccount, ComposeMailType.NEW, null);
    }

    public void composeForward() {
        WindowController.openComposeMailWindow(getClass(), selectedAccount, ComposeMailType.FORWARD, selectedMessage);
    }

    public void composeReply() {
        WindowController.openComposeMailWindow(getClass(), selectedAccount, ComposeMailType.REPLY, selectedMessage);
    }
}
