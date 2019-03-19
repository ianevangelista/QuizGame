package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import java.io.IOException;


public class ChangeScene {

    public static void change(ActionEvent event, String fxml) {
        try{
            Parent tableViewParent = FXMLLoader.load(ChangeScene.class.getResource(fxml)); //henter inn klasse og fxml-fil
            Scene scene = new Scene(tableViewParent); //lager ny scene
            Stage window = (Stage)((Node)event.getSource()).getScene().getWindow(); //caster stage
            window.setScene(scene); //setter scene
            window.show(); //viser scene
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void changeVisibility(boolean value, Label test){
        test.setVisible(value);
    }
    public static void changeVisibilityBtn(boolean value, Button btn){
        btn.setVisible(value);
    }
}
