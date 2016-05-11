package crypto_mail.model;

import java.io.Serializable;

public class AccountSettings implements Serializable {
    private String user;
    private String pass;
    private String inputHost;
    private Integer inputPort;
    private String outputHost;
    private Integer outputPort;

    public AccountSettings(String user, String pass, String inputHost, Integer inputPort, String outputHost, Integer outputPort) {
        this.user = user;
        this.pass = pass;
        this.inputHost = inputHost;
        this.inputPort = inputPort;
        this.outputHost = outputHost;
        this.outputPort = outputPort;
    }

    public String getUser() {
        return user;
    }

    public String getPass() {
        return pass;
    }

    public String getInputHost() {
        return inputHost;
    }

    public Integer getInputPort() {
        return inputPort;
    }

    public String getOutputHost() {
        return outputHost;
    }

    public Integer getOutputPort() {
        return outputPort;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("User: ")
                .append(user)
                .append("Password: ")
                .append(pass)
                .append("InputSettings - ")
                .append("Host:")
                .append(inputHost)
                .append("Port: ")
                .append(inputPort)
                .append("OutputSettings - ")
                .append("Host:")
                .append(outputHost)
                .append("Port: ")
                .append(outputPort)
                .toString();
    }
}
