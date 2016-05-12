package crypto_mail.gui;

import crypto_mail.model.Account;
import crypto_mail.model.ComposeMailType;
import crypto_mail.model.MailMessage;
import crypto_mail.model.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

/**
 * Created by elias on 28.4.16.
 */
public class WindowController {

    public static void openMainWindow(Class<?> sourceClass, Window previousWindow, User user) {
        try {
            FXMLLoader loader = new FXMLLoader(sourceClass.getResource("main.fxml"));
            Stage stage = new Stage();
            stage.setTitle("CryptoMail");
            stage.setScene(new Scene(loader.load()));
            MainController controller = loader.getController();
            controller.setUser(user);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        previousWindow.hide();
    }

    public static void openNewUserWindow(Class<?> sourceClass, Window previousWindow) {
        openWindow(sourceClass, "newUser.fxml", "CryptoMail -- Create New User", 315, 150);
        previousWindow.hide();
    }

    public static void openLoginWindow(Class<?> sourceClass) {
        openWindow(sourceClass, "gui/login.fxml", "CryptoMail -- Login", 285, 110);
    }

    public static void openAccountSettingsWindow(Class<?> sourceClass, Account account, MainController mainController) {
        try {
            FXMLLoader loader = new FXMLLoader(sourceClass.getResource("accountSettings.fxml"));
            Stage stage = new Stage();
            stage.setTitle("CryptoMail -- Edit Account");
            stage.setScene(new Scene(loader.load()));
            AccountSettingsController controller = loader.getController();
            controller.initController(mainController, account);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void openComposeMailWindow(Class<?> sourceClass, Account account, ComposeMailType mailType, MailMessage message) {
        try {
            FXMLLoader loader = new FXMLLoader(sourceClass.getResource("composeMail.fxml"));
            Stage stage = new Stage();
            stage.setTitle("CryptoMail -- Compose Message");
            stage.setScene(new Scene(loader.load()));
            ComposeMailController controller = loader.getController();
            controller.initAccount(account);
            if (mailType == ComposeMailType.FORWARD) {
                controller.initForward(message);
            }
            if (mailType == ComposeMailType.REPLY) {
                controller.initReply(message);
            }
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void openWindow(Class<?> sourceClass,
                                   String resourceName,
                                   String title,
                                   Integer width,
                                   Integer height) {
        try {
            Parent root = FXMLLoader.load(sourceClass.getResource(resourceName));
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root, width, height));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
