package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


abstract class ChangeScene {
    public ChangeScene(){ }

    public void change(ActionEvent event, String fxml) throws IOException {
        Parent tableViewParent = FXMLLoader.load(getClass().getResource(fxml)); //henter inn klasse og fxml-fil
        Scene main = new Scene(tableViewParent); //lager ny scene
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow(); //caster stage
        window.setScene(main); //setter scene
        window.show(); //viser scene
    }
}
