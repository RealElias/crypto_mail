package crypto_mail.service;

import crypto_mail.model.Account;

import javax.mail.*;
import java.util.*;

public class MailService {

    public void getMail(Account account) {
        Properties props = new Properties();
        props.put("mail.debug", "true");
        props.put("mail.store.protocol", "imaps");

        try {
            Session session = Session.getInstance(props);
            Store store = session.getStore();
            store.connect(account.getAccountSettings().getHost(),
                    account.getAccountSettings().getUser(),
                    account.getAccountSettings().getPass());

            Map<String, List<Message>> folders = new HashMap<>();
            for (Folder folder : store.getDefaultFolder().list()) {
                folders.put(folder.getName(), Arrays.asList(folder.getMessages()));
            }

            account.setFolders(folders);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
