package crypto_mail.model;

import javax.mail.Message;
import java.util.List;

public class Account {

    private AccountSettings accountSettings;
    private List<Message> inboxMessages;
    private List<Message> outboxMessages;

    public AccountSettings getAccountSettings() {
        return accountSettings;
    }

    public List<Message> getInboxMessages() {
        return inboxMessages;
    }

    public List<Message> getOutboxMessages() {
        return outboxMessages;
    }
}
