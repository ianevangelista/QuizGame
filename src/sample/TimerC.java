package sample;

import Connection.ConnectionPool;
import Connection.Cleaner;
import javafx.event.ActionEvent;

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
    private static int tellerR;
    private static Timer timerC;
    private static int tellerC;
    private static int gameId = getGameId();
    private static Connection connection = null;
    private static Statement statement = null;

    public static void timerRes(ActionEvent event){
        tellerR = 30;
        timerR = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if(tellerR == 0) turnOfTimerR();
                if(checkRes(event)) turnOfTimerR();
                tellerR--;
            }
        };
        timerR.schedule(task, 0, +3000);
    }

    public static void timerCat(ActionEvent event){
        tellerC = 30;
        timerC = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if(tellerC == 0) turnOfTimerC();
                if(checkCat(event)) turnOfTimerC();
                tellerC--;
            }
        };
        timerC.schedule(task, 0, +3000);
    }

    public static boolean checkCat(ActionEvent event) {
        ResultSet rs = null;
        String sqlCheck = "SELECT * FROM Game WHERE gameId = " + gameId + ";";

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();

            rs = statement.executeQuery(sqlCheck);
            rs.next();
            int catId = rs.getInt("categoryId");
            if (catId != 0) {
                ChangeScene.change(event, "Question.fxml");
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

    public static boolean checkRes(ActionEvent event) {
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
    }

    public static void turnOfTimerC() {
        if (timerC != null) {
            timerC.cancel();
        }
    }
}
