package crypto_mail.gui;

import crypto_mail.model.Account;
import crypto_mail.model.MailMessage;
import crypto_mail.service.MailService;
import crypto_mail.service.util.MailUtils;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import javax.mail.Address;
import java.util.List;
import java.util.StringJoiner;

public class ComposeMailController {

    private MailService mailService;

    @FXML
    TextField subjectField;

    @FXML
    TextField fromField;

    @FXML
    TextField ccField;

    @FXML
    TextField toField;

    @FXML
    TextArea contentArea;

    private Account account;

    public ComposeMailController() {
        mailService = new MailService();
    }

    public void initAccount(Account account) {
        this.account = account;
        fromField.setText(account.getAccountSettings().getUser());
    }

    public void initForward(MailMessage message) {
        subjectField.setText("FWD:" + message.getSubject());
        if (message.getRecipients().size() > 1) {
            List<Address> ccAddresses = message.getRecipients();
            ccAddresses.remove(0);
            ccField.setText(MailUtils.asString(ccAddresses));
        }
        contentArea.setText(quoteText(message.getContent()));
    }

    public void initReply(MailMessage message) {
        subjectField.setText("RE:" + message.getSubject());
        if (message.getRecipients().size() > 1) {
            List<Address> ccAddresses = message.getRecipients();
            ccAddresses.remove(0);
            ccField.setText(MailUtils.asString(ccAddresses));
        }
        toField.setText(MailUtils.asString(message.getFrom()));
        contentArea.setText(quoteText(message.getContent()));
    }

    public void sendMail() {
        MailMessage message = composeMessage();
        mailService.sendMail(account, message);
        contentArea.getScene().getWindow().hide();
    }

    private MailMessage composeMessage() {
        MailMessage message = new MailMessage();
        message.setSubject(subjectField.getText());
        message.setFrom(MailUtils.asAddressList(fromField.getText()));
        message.setRecipients(MailUtils.asAddressList(toField.getText(), ccField.getText()));
        message.setContent(contentArea.getText());
        return message;
    }

    private static String quoteText(String text) {
        if (text == null) {
            return "";
        }

        StringJoiner joiner = new StringJoiner("\n");
        joiner.add("\n");
        for (String line : text.split("\n")) {
            joiner.add(">" + line);
        }

        return joiner.toString();
    }
}
