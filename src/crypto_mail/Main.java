package crypto_mail;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("templates/login.fxml"));
        primaryStage.setTitle("Crypto Mail");
        primaryStage.setScene(new Scene(root, 285, 110));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
