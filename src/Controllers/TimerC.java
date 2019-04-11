package Controllers;

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

import static Controllers.ControllerHome.getUserName;
import static Controllers.ControllerOpponent.getGameId;

/**
 * The class TimerC is used when a player waits for the opponent to choose a category.
 */

public class TimerC {

    private static Timer timer;
    private static int gameId = getGameId();
    private static String username = getUserName();
    private static boolean categoryChosen = false;
    private static boolean noGameId = false;

    @FXML
    public Label messageText;
    public Button btnNext;
    public Label waitingText;

    /**
     * The method runs when accessing the Wait scene.
     * The method runs timerCat.
     * @return true if executed.
     */

    public boolean initialize(){
        timerCat();
        return true;
    }

    /**
     * The method changes scene to Game.
     * It turns off the timer.
     * @param event is a neccessary paramater which is used in a method from the class ChangeScene.
     */
    public void sceneGame(ActionEvent event) {
        turnOffTimer();
        ChangeScene.change(event, "/Scenes/Game.fxml");
    }

    /**
     * The method schedules a timer.
     * Uses checkCat to check if the opponent has chosen a category or if the request was accepted.
     * Starts two seconds after you have challenged an opponent and it repeats every three seconds.
     */
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
                    return;
                }
            }
        };
        timer.schedule(task, 2000, 3000);
    }

    /**
     * The method shows a button if the opponent has chosen a category.
     * If not it will show a message that the opponent declined the request.
     */
    public void showMessage(){
        if(categoryChosen) { btnNext.setVisible(true); }
        else if(noGameId){
            waitingText.setVisible(false);
            messageText.setVisible(true);
        }
    }

    /**
     * The method changes scene to Question.
     * @param event is a neccessary paramater which is used in a method from the class ChangeScene.
     */
    public void sceneQuestion(ActionEvent event) { //hjemknapp
        ChangeScene.change(event, "/Scenes/Question.fxml");
    }

    /**
     * The method checks if the a category has been chosen and if the last question has been set.
     * @param gameId is the id of the game.
     * @return true if category and the last question has been set. False if only one or none is set.
     */
    public static boolean checkCat(int gameId) {
        String sqlCheck = "SELECT question3 FROM Game WHERE gameId = " + gameId + ";";
        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;

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

    /**
     * The method checks if the opponent declined the request and deleted the game.
     * @param username is your username.
     * @return true if the opponent declined the request or false if not.
     */
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

    /**
     * The method terminates the timer.
     */
    public static void turnOffTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
    }
}
