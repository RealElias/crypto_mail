package crypto_mail.model;

public class AccountSettings {
    private String user;
    private String pass;
    private String host;

    public AccountSettings(String user, String pass, String host) {
        this.user = user;
        this.pass = pass;
        this.host = host;
    }

    public String getUser() {
        return user;
    }

    public String getPass() {
        return pass;
    }

    public String getHost() {
        return host;
    }
}
