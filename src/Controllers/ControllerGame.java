package Controllers;
import javafx.event.ActionEvent;
import static Controllers.ControllerHome.getUserName;
import static Controllers.ControllerHome.setUserNull;

public class ControllerGame {

    private static String username = getUserName();

    public static String getUsername(){
        return username;
    }

    public void logout(ActionEvent event) {
        Logout.logOut();
        setUserNull();
        ChangeScene.change(event, "/Scenes/Main.fxml");
    }

    public void start(ActionEvent event) {
        ControllerRefresh.refresh(event, username);
    }

    public void sceneBack(ActionEvent event) {
        if( getUserName() == null) { ChangeScene.change(event, "/Scenes/Main.fxml"); }
        else{ ChangeScene.change(event, "/Scenes/Game.fxml"); }
    }

    public void sceneInfo(ActionEvent event) { //trykker på infoknapp
        ChangeScene.change(event, "/Scenes/Info.fxml");
    }

    public void sceneHome(ActionEvent event) { //hjemknapp
        ChangeScene.change(event, "/Scenes/Main.fxml");
    }

    public void highscore(ActionEvent event) { //HighScore knapp
        ChangeScene.change(event, "/Scenes/HighScore.fxml");
    }

    public void profile(ActionEvent event) {
        ChangeScene.change(event, "/Scenes/Profile.fxml");
    }

    public void feedback(ActionEvent event){
        ChangeScene.change(event, "/Scenes/Feedback.fxml");
    }
}