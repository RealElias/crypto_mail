package crypto_mail.model;

import java.io.Serializable;

public class AccountSettings implements Serializable {
    private String user;
    private String pass;
    private String host;
    private Integer port;

    public AccountSettings(String user, String pass, String host, Integer port) {
        this.user = user;
        this.pass = pass;
        this.host = host;
        this.port = port;
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

    public Integer getPort() {
        return port;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("User: ")
                .append(user)
                .append("Password: ")
                .append(pass)
                .append("Host: ")
                .append(host)
                .append("Port: ")
                .append(port)
                .toString();
    }
}
