package sample;

import Connection.ConnectionPool;
import Connection.Cleaner;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

import javax.swing.*;

import static sample.ControllerQuestion.findUser;
import static sample.ChooseOpponent.getGameId;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.TimerTask;
import java.util.Timer;



public class TimerC {

    private static Timer timerR;
    private static Timer timerC;
    private static int teller = 0;
    private static int gameId = getGameId();
    private static Connection connection = null;
    private static Statement statement = null;

    @FXML
    public static Text tellerText;

    public void sceneGame(ActionEvent event) { //hjemknapp
        turnOfTimerC();
        ChangeScene.change(event, "Game.fxml");
    }

    /*public static void timerRes(ActionEvent event){
        timerR = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if(checkRes(event)) turnOfTimerR();
            }
        };
        timerR.schedule(task, 0, +3000);
    }*/

    public void initialize(){
        timerCat();
    }

    public static void timerCat(){
        timerC = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if(checkCat()) {
                    turnOfTimerC();
                    //ChangeScene.change(event, "Question.fxml");
                    System.out.println("bytter til annen scene");
                }
                else {
                    //tellerText.setText(String.valueOf(teller));
                    System.out.println("hei");
                    teller++;
                }
            }
        };
        timerC.schedule(task, 0, 1000);
    }

    public static boolean checkCat() {
        ResultSet rs = null;
        String sqlCheck = "SELECT categoryId FROM Game WHERE gameId = " + gameId + ";";

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();

            rs = statement.executeQuery(sqlCheck);
            if(rs.next()) {
                int catId = rs.getInt(1);
                if (catId != 0) return true;
            }
            return false;
        }catch (SQLException e) {
            e.printStackTrace();
            return false;
        }finally {
            Cleaner.close(statement, rs, connection);
        }
    }

    /*public static boolean checkRes(ActionEvent event) {
        ResultSet rs = null;
        String me = findUser();
        String opponentFinished = (me.equals("player1") ? "p2Finished" : "p1Finished");
        String sqlCheck = "SELECT * FROM Game WHERE gameId = " + gameId + ";";

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();

            rs = statement.executeQuery(sqlCheck);
            rs.next();
            int opponent = rs.getInt(opponentFinished);
            if (opponent == 1) {
                ChangeScene.change(event, "Result.fxml");
                return true;
            }
            return false;
        }catch (SQLException e) {
            e.printStackTrace();
            return false;
        }finally {
            Cleaner.close(statement, rs, connection);
        }
    }

    public static void turnOfTimerR() {
        if (timerR != null) {
            timerR.cancel();
        }
    }*/

    public static void turnOfTimerC() {
        if (timerC != null) {
            timerC.cancel();
        }
    }
}
