package Controllers;

import Connection.Cleaner;
import Connection.ConnectionPool;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javax.xml.bind.SchemaOutputResolver;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Timer;
import java.util.TimerTask;

import static Controllers.ControllerHome.getUserName;
import static Controllers.ControllerOpponent.getGameId;



public class TimerC {

    private static Timer timer;
    private static int gameId = getGameId();
    private static String username = getUserName();
    private static Connection connection = null;
    private static Statement statement = null;
    private static boolean categoryChosen = false;
    private static boolean noGameId = false;

    @FXML
    public Label messageText;
    public Button btnNext;
    public Label waitingText;

    public boolean initialize(){
        timerCat();
        return true;
    }

    public void sceneGame(ActionEvent event) { //hjemknapp
        turnOffTimer();
        ChangeScene.change(event, "/Scenes/Game.fxml");
    }

    public void timerCat(){
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if(checkCat(gameId)) {
                    turnOffTimer();
                    categoryChosen = true;
                    showMessage();
                    return;
                }
                else if(checkGameId(username)){
                    turnOffTimer();
                    noGameId = true;
                    showMessage();
                    System.out.println("checkgameid funker");
                    return;
                }
            }
        };
        timer.schedule(task, 2000, 3000);
    }

    public void showMessage(){
        if(categoryChosen) { btnNext.setVisible(true); }
        else if(noGameId){
            System.out.println("show label");
            waitingText.setVisible(false);
            System.out.println("hide label");
            messageText.setVisible(true);
        }
    }

    public void sceneQuestion(ActionEvent event) { //hjemknapp
        ChangeScene.change(event, "/Scenes/Question.fxml");
    }


    public static boolean checkCat(int gameId) {
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

    public boolean checkGameId(String username) {
        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            String sql = "SELECT gameId FROM Game WHERE player1 = '" + username + "';";
            rs = statement.executeQuery(sql);
            if(rs.next()){
                return false;
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            Cleaner.close(statement, rs, connection);
        }
    }

    public static void turnOffTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
    }
}
