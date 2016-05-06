package crypto_mail.model;

import javax.mail.Message;
import java.util.List;
import java.util.Map;

public class Account {

    private AccountSettings accountSettings;
    private Map<String, List<Message>> folders;

    public AccountSettings getAccountSettings() {
        return accountSettings;
    }

    public void setAccountSettings(AccountSettings accountSettings) {
        this.accountSettings = accountSettings;
    }

    public Map<String, List<Message>> getFolders() {
        return folders;
    }

    public void setFolders(Map<String, List<Message>> folders) {
        this.folders = folders;
    }
}
