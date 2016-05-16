package crypto_mail.service;

import crypto_mail.model.Account;
import crypto_mail.model.User;
import crypto_mail.service.util.CryptoUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by elias on 6.5.16.
 */
public class AccountService {
    private static final String EXT = ".mail";
    private static final String ACCOUNTS_DIR = "./config/users/";
    private static final String ACCOUNTS_TEMP_FILE = "./config/temp.mail";

    public void writeAccounts(List<Account> accounts, User user) {
        File output = new File(ACCOUNTS_DIR + user.getName() + EXT);
        File decryptedOutput = new File(ACCOUNTS_TEMP_FILE);

        if (accounts.isEmpty() && output.exists()) {
            output.delete();
            return;
        }

        try {
            if (!output.exists()) {
                output.getParentFile().mkdirs();
                output.createNewFile();
            }
            decryptedOutput.getParentFile().mkdirs();
            decryptedOutput.createNewFile();
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(decryptedOutput));
            outputStream.writeObject(accounts);
            CryptoUtils.encrypt(user.getPassword(), decryptedOutput, output);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            decryptedOutput.delete();
        }
    }

    public List<Account> readAccounts(User user) {
        File input = new File(ACCOUNTS_DIR + user.getName() + EXT);
        File decryptedInput = new File(ACCOUNTS_TEMP_FILE);
        List<Account> accounts;

        if (!input.exists()) {
            return new ArrayList();
        }

        try {
            decryptedInput.getParentFile().mkdirs();
            decryptedInput.createNewFile();
            CryptoUtils.decrypt(user.getPassword(), input, decryptedInput);
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(decryptedInput));
            accounts = (List<Account>)inputStream.readObject();
            inputStream.close();
            return accounts;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        } finally {
            decryptedInput.delete();
        }
    }
}
