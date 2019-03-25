package sample;
import javafx.event.ActionEvent;

public class ControllerGame {

    public void logout(ActionEvent event) { //HighScore knapp
        Logout.logOut();
        ChangeScene.change(event, "Main.fxml");
    }

    public void start(ActionEvent event) {
        ControllerRefresh.refresh(event);
    }

    public void sceneInfoLogin(ActionEvent event) { //trykker på infoknapp
        ChangeScene.change(event, "Info_Login.fxml");
    }

    public void sceneHome(ActionEvent event) { //hjemknapp
        ChangeScene.change(event, "Main.fxml");
    }

    public void sceneGame(ActionEvent event) { //hjemknapp
        ChangeScene.change(event, "Game.fxml");
    }

    public void highscore(ActionEvent event) { //HighScore knapp
        ChangeScene.change(event, "HighScore.fxml");
    }

    public void profile(ActionEvent event) {
        ChangeScene.change(event, "Profile.fxml");
    }
}