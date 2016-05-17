package crypto_mail.gui;

import crypto_mail.model.Account;
import crypto_mail.model.ComposeMailType;
import crypto_mail.model.MailMessage;
import crypto_mail.model.User;
import crypto_mail.service.AccountService;
import crypto_mail.service.MailService;
import crypto_mail.service.util.MailUtils;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.web.WebView;

import javax.mail.Address;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    WebView contentArea;

    @FXML
    ProgressBar progressBar;

    @FXML
    Label progressLabel;

    @FXML
    Button getMailButton;

    @FXML
    Button composeMailButton;

    private Task getMailTask;

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
        getMailButton.setOnAction(e -> {
            getMailTask = createTask(accounts);
            progressBar.setVisible(true);
            progressBar.setProgress(-1.0);
            new Thread(getMailTask).start();
            getMailTask.setOnSucceeded(event -> progressBar.setVisible(false));
        });
    }

    private Task createTask(List<Account> accounts) {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                accounts.forEach(mailService::getMail);
                return true;
            }
        };
    }

    private void selectMessage(Integer index) {
        if(index < 0)
            return;

        selectedMessage = selectedFolder.get(selectedFolder.size() - index - 1);

        subjectField.setText(selectedMessage.getSubject());
        fromField.setText(MailUtils.asString(selectedMessage.getFrom()));

        if (selectedMessage.getRecipients().size() > 1) {
            List<Address> ccAddresses = selectedMessage.getRecipients();
            ccAddresses.remove(0);
            ccField.setText(MailUtils.asString(ccAddresses));
        }

        LocalDateTime dateTime = LocalDateTime.ofInstant(selectedMessage.getReceivedDate().toInstant(), ZoneId.systemDefault());
        dateField.setText(dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        toField.setText(selectedMessage.getRecipients().get(0).toString());
        contentArea.getEngine().loadContent(selectedMessage.getContent());
    }

    private void selectFolder(Integer index) {
        if(index < 0)
            return;

        String folderName = (String) selectedAccount.getFolders().keySet().toArray()[selectedAccount.getFolders().keySet().size() - index - 1];
        selectedFolder = selectedAccount.getFolders().get(folderName);

        List<String> reversedMessages = selectedFolder.stream()
                .map(message -> message.getSubject() + "\n" + message.getContentTitle())
                .collect(Collectors.toList());
        ObservableList<String> messages = FXCollections.observableArrayList(new ArrayList<>());
        IntStream.range(0, reversedMessages.size()).forEach(i -> messages.add(reversedMessages.get(reversedMessages.size() - i - 1)));
        messagesList.setItems(messages);
    }

    private void selectAccount(Integer index) {
        if(index < 0)
            return;

        this.selectedAccount = accounts.get(index);
        ObservableList<String> folders = FXCollections.observableArrayList(new ArrayList<>());
        if(selectedAccount.getFolders() != null) {
            List<String> reversedFolders = selectedAccount
                    .getFolders()
                    .entrySet()
                    .stream()
                    .map(entry -> entry.getKey() + "(" + entry.getValue().size() + ")")
                    .collect(Collectors.toList());
            IntStream.range(0, reversedFolders.size()).forEach(i -> folders.add(reversedFolders.get(reversedFolders.size() - i - 1)));
        }
        foldersList.setItems(folders);
        editAccountButton.setDisable(false);
        removeAccountButton.setDisable(false);
        getMailButton.setDisable(false);
        composeMailButton.setDisable(false);
    }

    public void addAccount() {
        WindowController.openAccountSettingsWindow(getClass(), null, this);
    }

    private void updateAccountsList() {
        ObservableList<String> accountNames = FXCollections.observableArrayList(accounts.stream().map(Account::getName).collect(Collectors.toList()));
        accountsList.setItems(accountNames);
        editAccountButton.setDisable(true);
        removeAccountButton.setDisable(true);
        getMailButton.setDisable(true);
        composeMailButton.setDisable(true);
    }

    public void editAccount() {
        WindowController.openAccountSettingsWindow(getClass(), selectedAccount, this);
        accountService.writeAccounts(accounts, user);
    }

    public void removeAccount() {
        accounts.remove(selectedAccount);
        accountService.writeAccounts(accounts, user);
        updateAccountsList();
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
