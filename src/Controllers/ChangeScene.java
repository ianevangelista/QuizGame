package Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * The class ChangeScene is used when changing scenes.
 */

public class ChangeScene {

    /**
     * The method changes scenes by using fxml-files.
     * @param event is used to change the window by importing a fxml-file.
     * @param fxml is the imported file which is the scene that is switched to.
     */
    public static void change(ActionEvent event, String fxml) {
        /*
        Loads the fxml-file.
        Creates a new stage with fxml-file as the scene.
        Displays the window.
        */
        try{
            Parent tableViewParent = FXMLLoader.load(ChangeScene.class.getResource(fxml)); //imports class and fxml file
            Scene scene = new Scene(tableViewParent); //Creates new scene
            Stage window = (Stage)((Node)event.getSource()).getScene().getWindow(); //Casts stage
            window.setScene(scene); //Sets the stage
            window.show(); //Shows the scene
        }catch (IOException e){
            // Input output error when getting the fxml file
            e.printStackTrace();
        }
        catch (Exception e) {
            // If something else goes wrong
            e.printStackTrace();
        }
    }
}
