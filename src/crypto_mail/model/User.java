package crypto_mail.model;

import crypto_mail.service.util.MD5Util;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String name;
    private String password;
    private List<Account> accounts;

    public User(String name, String password) {
        this.name = name;
        this.password = MD5Util.md5Hash(password);
        this.accounts = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public List<Account> getAccounts() {
        return accounts;
    }
}
