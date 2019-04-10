package Controllers;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import static Controllers.ControllerHome.getUserName;

/**
 * The class AlertBox is used when exiting the window.
 */

public class AlertBox {
    private static boolean answer;

    /**
     * This method runs when exiting the window.
     * It will check if user is logged in or not.
     * It gives the user the choice of confirming to leave or not.
     * @return if confirmed leaving return true, else return false.
     */
    public static boolean display(String title, String message){
        /*
          Creates a new window which gives the user two options.
          Either the user can exit the entire game or stay.
         */
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(250);
        window.setMinHeight(200);
        Label label = new Label();
        label.setText(message);
        Button closeBtn = new Button("Yes, leave");
        Button regretBtn = new Button("No, stay");
        /*
          Checks if the user is logged in.
          If logged in the user will be logged out if wanting to exit.
         */
        closeBtn.setOnAction(event -> {
            if(getUserName() != null) {
                Logout.logOut();
            }
            answer = true;
            window.close();
        });


        // If not wanting to exit, the created window will close.
        regretBtn.setOnAction(event -> {
            answer = false;
            window.close();
        });


        // Displays the window to the user.
        VBox layout = new VBox(10);
        layout.getChildren().addAll(label, closeBtn, regretBtn);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
        return answer;
    }
}
