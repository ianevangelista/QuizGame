package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class testChooseOpponent extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent main = FXMLLoader.load(getClass().getResource("ChallangeUser.fxml"));
        primaryStage.setTitle("Challenge");
        primaryStage.setScene(new Scene(main, 600, 400));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
