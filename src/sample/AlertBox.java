package sample;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import static sample.ControllerHome.getUserName;

public class AlertBox {
    static boolean answer;
    public static boolean display(String title, String message){
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(250);
        window.setMinHeight(200);
        Label label = new Label();
        label.setText(message);
        Button closeBtn = new Button("Yes, leave");
        Button regretBtn = new Button("No, stay");

        closeBtn.setOnAction(event -> {
            if(getUserName() != null) {
                Logout.logOut();
                answer = true;
                window.close();
            }
        });

        regretBtn.setOnAction(event -> {
            answer = false;
            window.close();
        });
        VBox layout = new VBox(10);
        layout.getChildren().addAll(label, closeBtn, regretBtn);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
        return answer;
    }
}
