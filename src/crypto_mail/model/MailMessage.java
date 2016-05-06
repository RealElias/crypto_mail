package crypto_mail.model;

import javax.mail.Address;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class MailMessage implements Serializable {
    private String subject;
    private List<Address> from;
    private List<Address> recipients;
    private Date receivedDate;
    private String content;

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

    public String getContentTitle() {
        if(content == null)
            return "";
        if(content.length() < 12)
            return content;
        return content.substring(0, 12);
    }
}
