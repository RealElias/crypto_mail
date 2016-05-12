package crypto_mail.model;

import crypto_mail.service.util.MD5Util;

import java.io.Serializable;

public class User implements Serializable {
    private String name;
    private String password;

    public User(String name, String password) {
        this.name = name;
        this.password = MD5Util.md5Hash(password);
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return new StringBuilder("Name : ")
                .append(name)
                .append("Password : ")
                .append(password)
                .toString();
    }
}
