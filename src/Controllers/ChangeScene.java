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
            //imports class and fxml file
            Parent tableViewParent = FXMLLoader.load(ChangeScene.class.getResource(fxml));
            //Creates new scene
            Scene scene = new Scene(tableViewParent);
            //Casts stage
            Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
            //Sets the stage
            window.setScene(scene);
            //Shows the scene
            window.show();
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
