package Controllers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The class Main covers the startup of the application How Dumb R U?
*/

public class Main extends Application {

    /**
     * The method creates stage and fetches a scene and displays it.
     * @param primaryStage the stage.
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent main = FXMLLoader.load(getClass().getResource("/Scenes/Main.fxml"));
        primaryStage.setTitle("How Dumb R You?");
        primaryStage.setOnCloseRequest(e -> {
            e.consume();
            closeProgram();
        });
        primaryStage.setScene(new Scene(main, 600, 400));
        primaryStage.show();
    }

    /**
     * The method closes the program.
     * Gives the user the option to confirm.
     */
    private void closeProgram(){
        Boolean answer = AlertBox.display("Exit game", "Are you sure you want to quit?");
        if(answer) System.exit(1);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
