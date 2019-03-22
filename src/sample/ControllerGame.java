package sample;

import static sample.ControllerHome.getUserName;
import static sample.ControllerQuestion.findUser;
import Connection.ConnectionPool;
import Connection.Cleaner;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import static sample.ChooseOpponent.getGameId;

public class ControllerGame {

    private String username = getUserName();
    private int gameId = getGameId();

    @FXML
    public Button refresh;
    //result
    public TextField totalScore;
    public TextField resultText;
    public TextField resultHeading;
    //highscore
    public Label userCol;
    public Label scoreCol;


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

    public void sceneChallengeUser(ActionEvent event){
        ChangeScene.change(event, "ChallengeUser.fxml");
        System.out.println("gameid: " + gameId);
    }

}