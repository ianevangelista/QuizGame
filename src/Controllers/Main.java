package Controllers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent main = FXMLLoader.load(getClass().getResource("/Scenes/Main.fxml"));
        primaryStage.setTitle("How Dumb R You?");
        primaryStage.setOnCloseRequest(e -> {
            e.consume();
            closeProgram(primaryStage);
        });
        primaryStage.setScene(new Scene(main, 600, 400));
        primaryStage.show();
    }

    private void closeProgram(Stage stage){
        Boolean answer = AlertBox.display("Exit game", "Are you sure you want to quit?");
        if(answer) stage.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
