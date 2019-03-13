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

    private static Timer timer;
    private static int teller;
    private static int gameId = getGameId();
    private static Connection connection = null;
    private static Statement statement = null;

    public static void timerRes(ActionEvent event){
        teller = 30;
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if(teller == 0) turnOfTimer();
                if(checkRes(event)) turnOfTimer();
                teller--;
            }
        };
        timer.schedule(task, 0, +1000);
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
                ChangeScene.change(event, "result.fxml");
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

    public static void turnOfTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }
}
