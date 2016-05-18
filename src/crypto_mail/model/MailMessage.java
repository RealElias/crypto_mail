package crypto_mail.model;

import crypto_mail.service.util.MailUtils;

import javax.mail.Address;
import javax.mail.Message;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class MailMessage implements Serializable {
    private String subject;
    private List<Address> from;
    private List<Address> recipients;
    private Date receivedDate;
    private String content;
    private Boolean unseen;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public List<Address> getFrom() {
        return from;
    }

    public void setFrom(List<Address> from) {
        this.from = from;
    }

    public List<Address> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<Address> recipients) {
        this.recipients = recipients;
    }

    public Date getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean isUnseen() {
        return unseen;
    }

    public void setUnseen(Boolean unseen) {
        this.unseen = unseen;
    }

    public String getContentTitle() {
        String clearContent = content.replaceAll("<[^>]*>","");
        if(clearContent == null)
            return "";
        if(clearContent.length() < 30)
            return clearContent;
        return clearContent.substring(0, 30);
    }

    public Boolean equals(MailMessage message) {
        return subject.equals(message.getSubject())
                && MailUtils.asString(from).equals(MailUtils.asString(message.getFrom()))
                && MailUtils.asString(recipients).equals(MailUtils.asString(message.getRecipients()))
                && content.equals(message.getContent())
                && receivedDate.equals(message.getReceivedDate());
    }
}
