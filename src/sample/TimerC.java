package sample;

import Connection.Cleaner;
import Connection.ConnectionPool;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Timer;
import java.util.TimerTask;

import static sample.ControllerOpponent.getGameId;



public class TimerC {

    private static Timer timer;
    private static int gameId = getGameId();
    private static Connection connection = null;
    private static Statement statement = null;
    private static boolean categoryChosen = false;

    @FXML
    public Label messageText;
    public Button btnNext;

    public void sceneGame(ActionEvent event) { //hjemknapp
        turnOfTimer();
        ChangeScene.change(event, "Game.fxml");
    }

    public void initialize(){
        //ChangeScene.changeVisibility(false, messageText);
        timerCat();
    }

    public void timerCat(){
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if(checkCat()) {
                    turnOfTimer();
                    categoryChosen = true;
                    showBtn();
                }
            }
        };
        timer.schedule(task, 10000, 3000);
    }

    public void showBtn(){
        if(categoryChosen) {
            ChangeScene.changeVisibilityBtn(true, btnNext);
            //messageText.setText("Ready, set, go!");
        }
    }

    public void sceneQuestion(ActionEvent event) { //hjemknapp
        ChangeScene.change(event, "Question.fxml");
    }

    public static boolean checkCat() {
        ResultSet rs = null;
        String sqlCheck = "SELECT question3 FROM Game WHERE gameId = " + gameId + ";";

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();

            rs = statement.executeQuery(sqlCheck);
            if(rs.next()) {
                int qId = rs.getInt(1);
                if (qId != 0) return true;
            }
            return false;
        }catch (SQLException e) {
            e.printStackTrace();
            return false;
        }finally {
            Cleaner.close(statement, rs, connection);
        }
    }

    public static void turnOfTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            return;
        }
    }
}
