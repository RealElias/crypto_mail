package crypto_mail.gui;

import crypto_mail.model.Account;
import crypto_mail.model.User;
import crypto_mail.service.user.AccountService;

import java.util.List;

public class MainController {
    private User user;
    private List<Account> accounts;
    AccountService accountService;

    public MainController() {
        accountService = new AccountService();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        accounts = accountService.readAccounts(user);
    }

    public void addAccount(Account account) {
        accounts.add(account);
        updateAccounts();
    }

    public void removeAccount(Account account) {
        accounts.remove(account);
        updateAccounts();
    }

    public void updateAccounts() {
        accountService.writeAccounts(accounts, user);
    }
}
