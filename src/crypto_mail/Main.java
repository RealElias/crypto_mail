package crypto_mail;

import crypto_mail.gui.WindowController;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        WindowController.openLoginWindow(getClass());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
