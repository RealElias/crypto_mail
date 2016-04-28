package crypto_mail.gui;

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

    public static void openMainWindow(Class<?> sourceClass, Window previousWindow) {
        openWindow(sourceClass, "main.fxml", "CryptoMail", 800, 600);
        previousWindow.hide();
    }

    public static void openNewUserWindow(Class<?> sourceClass, Window previousWindow) {
        openWindow(sourceClass, "newUser.fxml", "CryptoMail -- Create New User", 315, 150);
        previousWindow.hide();
    }

    public static void openLoginWindow(Class<?> sourceClass) {
        openWindow(sourceClass, "gui/login.fxml", "CryptoMail -- Login", 285, 110);
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
