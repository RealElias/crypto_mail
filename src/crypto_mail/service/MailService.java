package crypto_mail.service;

import crypto_mail.gui.MainController;
import crypto_mail.model.Account;
import crypto_mail.model.MailMessage;
import crypto_mail.service.util.MailUtils;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.*;

public class MailService {

    public void getMail(Account account, MainController controller) {
        Properties props = new Properties();
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
                List<MailMessage> mailMessages = new ArrayList();
                for (int i = 0; i < folder.getMessageCount(); i++) {
                    mailMessages.add(MailService.asMailMessage(folder.getMessages()[i]));
                    controller.setProgress(i, folder.getMessageCount(), account.getName(), folder.getName());
                }
                folders.put(folder.getName(), mailMessages);
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
            mailMessage.setContent(getText(message));
        } catch (MessagingException | IOException | NullPointerException e) {
            mailMessage.setContent("Can't get content");
        }

        return mailMessage;
    }

    private static String getText(Part p) throws
            MessagingException, IOException, NullPointerException {
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

    public void sendMail(Account account, MailMessage mailMessage) {
        Properties props = new Properties();
        props.put("mail.smtps.host", account.getAccountSettings().getOutputHost());
        props.put("mail.smtps.port", account.getAccountSettings().getOutputPort());
        props.put("mail.transport.protocol", "smtps");
        props.put("mail.smtps.auth", "true");
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(account.getAccountSettings().getUser(), account.getAccountSettings().getPass());
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.addFrom(MailUtils.asAddressArray(mailMessage.getFrom()));
            message.setRecipients(Message.RecipientType.TO, MailUtils.asAddressArray(mailMessage.getRecipients()));
            message.setSubject(mailMessage.getSubject());
            message.setSentDate(new Date());
            message.setText(mailMessage.getContent());
            message.saveChanges();

            Transport transport = session.getTransport("smtps");
            transport.connect();
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
