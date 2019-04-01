package sample;
import javafx.event.ActionEvent;
import static sample.ControllerHome.getUserName;
import static sample.ControllerHome.setUserNull;

public class ControllerGame {

    private static String username = getUserName();

    public static String getUsername(){
        return username;
    }

    public void logout(ActionEvent event) {
        Logout.logOut();
        setUserNull();
        ChangeScene.change(event, "Main.fxml");
    }

    public void start(ActionEvent event) {
        ControllerRefresh.refresh(event);
    }

    public void sceneBack(ActionEvent event) {
        if(getUserName() == null) {
            ChangeScene.change(event, "Main.fxml");
        }
        else{
            ChangeScene.change(event, "Game.fxml");
        }
    }

    public void sceneInfo(ActionEvent event) { //trykker på infoknapp
        ChangeScene.change(event, "Info.fxml");
    }

    public void sceneHome(ActionEvent event) { //hjemknapp
        ChangeScene.change(event, "Main.fxml");
    }

    public void highscore(ActionEvent event) { //HighScore knapp
        ChangeScene.change(event, "HighScore.fxml");
    }

    public void profile(ActionEvent event) {
        ChangeScene.change(event, "Profile.fxml");
    }

    public void feedback(ActionEvent event){
        ChangeScene.change(event, "Feedback.fxml");
    }
}