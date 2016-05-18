package crypto_mail.gui;

import crypto_mail.model.Account;
import crypto_mail.model.ComposeMailType;
import crypto_mail.model.MailMessage;
import crypto_mail.model.User;
import crypto_mail.service.AccountService;
import crypto_mail.service.MailService;
import crypto_mail.service.util.MailUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.text.TextBuilder;
import javafx.scene.web.WebView;

import javax.mail.Address;
import javax.mail.internet.MimeUtility;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
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

    private String selectedFolderName;

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
        accountsList.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            selectAccount((Integer) newValue);
        });
        foldersList.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            selectFolder((Integer) newValue);
        });
        messagesList.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            selectMessage((Integer) newValue);
        });
        getMailButton.setOnAction(e -> {
            getMailTask = createTask(this);
            new Thread(getMailTask).start();
        });
    }

    private Task createTask(MainController controller) {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                progressBar.setVisible(true);
                progressLabel.setVisible(true);
                progressBar.setProgress(0.0);

                mailService.getMail(selectedAccount, controller);
                writeAccounts();

                progressBar.setVisible(false);
                progressLabel.setVisible(false);
                Platform.runLater(() -> selectAccount(accounts.indexOf(selectedAccount)));

                return true;
            }
        };
    }

    public void updateProgressBar(int cur, int total, String folder) {
        Runnable updateBar = createUpdateProgressBarTask(cur + 1, total, folder);
        Platform.runLater(updateBar);
    }

    private Runnable createUpdateProgressBarTask(int cur, int total, String folder) {
        return () -> {
            progressLabel.setText(folder + ":" + cur + "/" + total);
            progressBar.setProgress((double) cur/total);
        };
    }

    private void selectMessage(Integer index) {
        if(index < 0)
            return;

        try {
            selectedMessage = selectedFolder.get(selectedFolder.size() - index - 1);
            readMessage(index);

            subjectField.setText(selectedMessage.getSubject());
            fromField.setText(MimeUtility.decodeText(MailUtils.asString(selectedMessage.getFrom())));

            if (selectedMessage.getRecipients().size() > 1) {
                List<Address> ccAddresses = selectedMessage.getRecipients();
                ccAddresses.remove(0);
                ccField.setText(MimeUtility.decodeText(MailUtils.asString(ccAddresses)));
            }

            LocalDateTime dateTime = LocalDateTime.ofInstant(selectedMessage.getReceivedDate().toInstant(), ZoneId.systemDefault());
            dateField.setText(dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            toField.setText(MimeUtility.decodeText(selectedMessage.getRecipients().get(0).toString()));
            contentArea.getEngine().loadContent(selectedMessage.getContent());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void readMessage(Integer index) {
        if(selectedMessage.isUnseen()) {
            selectedMessage.setUnseen(false);
            ((Text) messagesList.getItems().get(index)).setStyle("");
            Integer folderIndex = Arrays.asList(selectedAccount.getFolders().keySet().toArray()).indexOf(selectedFolderName);
            Long unseenCount = selectedFolder.stream()
                    .filter(MailMessage::isUnseen)
                    .count();

            ((Text) foldersList.getItems().get(foldersList.getItems().size() - folderIndex - 1)).setText(selectedFolderName + "(" + unseenCount + ")");

            new Thread(new Task() {
                @Override
                protected Object call() throws Exception {
                    mailService.setMessageRead(selectedAccount, selectedFolderName, selectedMessage);
                    accountService.writeAccounts(accounts, user);
                    return true;
                }
            });
        }
    }

    private void selectFolder(Integer index) {
        if(index < 0)
            return;

        selectedFolderName = (String) selectedAccount.getFolders().keySet().toArray()[selectedAccount.getFolders().keySet().size() - index - 1];
        selectedFolder = selectedAccount.getFolders().get(selectedFolderName);
        List<Text> reversedMessages = selectedFolder.stream()
                .map(message -> TextBuilder.create()
                        .text(message.getSubject() + "\n" + message.getContentTitle())
                        .style(message.isUnseen() ? "-fx-font-weight:bold;" : "")
                        .build())
                .collect(Collectors.toList());
        ObservableList<Text> messages = FXCollections.observableArrayList(new ArrayList<>());
        IntStream.range(0, reversedMessages.size()).forEach(i -> messages.add(reversedMessages.get(reversedMessages.size() - i - 1)));
        messagesList.setItems(messages);
    }

    private void selectAccount(Integer index) {
        if(index < 0)
            return;

        this.selectedAccount = accounts.get(index);
        ObservableList<Text> folders = FXCollections.observableArrayList(new ArrayList<>());
        if(selectedAccount.getFolders() != null) {
            List<Text> reversedFolders = selectedAccount
                    .getFolders()
                    .entrySet()
                    .stream()
                    .map(entry -> new Text(entry.getKey() + "(" + entry.getValue().stream().filter(MailMessage::isUnseen).count() + ")"))
                    .collect(Collectors.toList());
            IntStream.range(0, reversedFolders.size()).forEach(i -> folders.add(reversedFolders.get(reversedFolders.size() - i - 1)));
        }
        foldersList.setItems(folders);
        clearMessageList();
        editAccountButton.setDisable(false);
        removeAccountButton.setDisable(false);
        getMailButton.setDisable(false);
        composeMailButton.setDisable(false);
    }

    private void clearMessageList() {
        messagesList.setItems(FXCollections.observableArrayList(new ArrayList<>()));
        subjectField.clear();
        toField.clear();
        fromField.clear();
        ccField.clear();
        dateField.clear();
        contentArea.getEngine().loadContent("");
    }

    public void addAccount() {
        WindowController.openAccountSettingsWindow(getClass(), null, this);
        updateAccountsList();
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
