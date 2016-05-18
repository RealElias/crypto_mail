package crypto_mail.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Account implements Serializable {

    private String name;
    private AccountSettings accountSettings;
    private Map<String, List<MailMessage>> folders;

    public Account() {
        folders = new HashMap<>();
    }

    public AccountSettings getAccountSettings() {
        return accountSettings;
    }

    public void setAccountSettings(AccountSettings accountSettings) {
        this.accountSettings = accountSettings;
    }

    public Map<String, List<MailMessage>> getFolders() {
        return folders;
    }

    public void setFolders(Map<String, List<MailMessage>> folders) {
        this.folders = folders;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return new StringBuilder("Name: ")
                .append(name)
                .append("Settings: ")
                .append(accountSettings)
                .append("Folders: ")
                .append(folders)
                .toString();
    }
}
