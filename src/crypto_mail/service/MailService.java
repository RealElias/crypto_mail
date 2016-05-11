package crypto_mail.service;

import crypto_mail.model.Account;
import crypto_mail.model.MailMessage;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MailService {

    public void getMail(Account account) {
        Properties props = new Properties();
        props.put("mail.debug", "true");
        props.put("mail.store.protocol", "imaps");

        try {
            Session session = Session.getInstance(props);
            Store store = session.getStore();
            store.connect(account.getAccountSettings().getInputHost(),
                    account.getAccountSettings().getInputPort(),
                    account.getAccountSettings().getUser(),
                    account.getAccountSettings().getPass());

            Map<String, List<MailMessage>> folders = new HashMap<>();
            for (Folder folder : store.getDefaultFolder().list()) {
                folder.open(Folder.READ_ONLY);
                folders.put(folder.getName(), Stream.of(folder.getMessages()).map(MailService::asMailMessage).collect(Collectors.toList()));
            }

            account.setFolders(folders);
        } catch (MessagingException e) {
            account.setFolders(new HashMap<>());
            e.printStackTrace();
        }
    }

    private static MailMessage asMailMessage(Message message) {
        MailMessage mailMessage = new MailMessage();
        try {
            mailMessage.setSubject(message.getSubject());
            mailMessage.setFrom(Arrays.asList(message.getFrom()));
            if(message.getAllRecipients() != null) {
                mailMessage.setRecipients(Arrays.asList(message.getAllRecipients()));
            } else {
                mailMessage.setRecipients(new ArrayList<>());
            }
            mailMessage.setReceivedDate(message.getReceivedDate());
//            mailMessage.setContent(getText(message));
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return mailMessage;
    }

    private static String getText(Part p) throws
            MessagingException, IOException {
        if (p.isMimeType("text/*")) {
            String s = (String)p.getContent();
            return s;
        }

        if (p.isMimeType("multipart/alternative")) {
            // prefer html text over plain text
            Multipart mp = (Multipart)p.getContent();
            String text = null;
            for (int i = 0; i < mp.getCount(); i++) {
                Part bp = mp.getBodyPart(i);
                if (bp.isMimeType("text/plain")) {
                    if (text == null)
                        text = getText(bp);
                    continue;
                } else if (bp.isMimeType("text/html")) {
                    String s = getText(bp);
                    if (s != null)
                        return s;
                } else {
                    return getText(bp);
                }
            }
            return text;
        } else if (p.isMimeType("multipart/*")) {
            Multipart mp = (Multipart)p.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                String s = getText(mp.getBodyPart(i));
                if (s != null)
                    return s;
            }
        }

        return null;
    }

    public void sendMail(MailMessage message) {

    }

    public static String asString(List<Address> addresses) {
        StringJoiner joiner = new StringJoiner(", ");
        addresses.stream().forEach(address -> joiner.add(address.toString()));
        return joiner.toString();
    }

    public static List<Address> asAddressList(String... addressesLines) {
        List<Address> addresses = new ArrayList<>();
        try {
            for(String addressesLine : addressesLines) {
                for (String address : addressesLine.split(", ")) {
                    addresses.add(new InternetAddress(address));
                }
            }
        } catch (AddressException e) {
            e.printStackTrace();
        }
        return addresses;
    }
}
