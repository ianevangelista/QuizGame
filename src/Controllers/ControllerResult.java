package Controllers;

import static Controllers.ControllerHome.getUserName;
import static Controllers.ControllerQuestion.findUser;
import Connection.ConnectionPool;
import Connection.Cleaner;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

import javax.swing.plaf.OptionPaneUI;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.Timer;
import java.util.TimerTask;
import static Controllers.ControllerOpponent.getGameId;
import static Controllers.ControllerOpponent.resetGameId;

public class ControllerResult {

    private String username = getUserName();
    private int gameId = getGameId();
    private Timer timerR;
    private boolean bothFinished = false;

    Connection connection = null;
    Statement statement = null;

    @FXML
    //result
    public Text totalScore;
    public Text resultText;
    public Button btnNext;
    public Button btnChallenge;
    public Text totalScoreText;
    public Text yourScore;
    public Text theirScore;

    public boolean initialize() {

        ResultSet rs = null;

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();

            String player = findUser();
            String me = player.equals("player1") ? "p1Points" : "p2Points";
            String opponent= player.equals("player1") ? "p2Points" : "p1Points";
            String sqlFinished = "SELECT * FROM Game WHERE gameId =" + gameId + ";";
            rs = statement.executeQuery(sqlFinished);
            rs.next();

            //get bool value of whether they are finnished or not
            int p1Finished = rs.getInt("p1Finished");
            int p2Finished = rs.getInt("p2Finished");
            int mePoints = rs.getInt(me);
            int opponentPoints = rs.getInt(opponent);

            if(p1Finished == 1 && p2Finished == 1) {
                totalScoreText.setVisible(true);
                theirScore.setVisible(true);
                yourScore.setVisible(true);
                theirScore.setText("Your opponent got " + opponentPoints + " points this round.");
                yourScore.setText("You got " + mePoints + " points this round!");
                bothFinished = true;

                checkResult(mePoints, opponentPoints);

                if(checkResult(mePoints, opponentPoints) == 1){
                    resultText.setText("You won! :)");
                }
                else{
                    resultText.setText("You lost :(");
                }
                username = getUserName();
                String sqlGetPlayerScore = "SELECT points FROM Player WHERE username = '" + username + "';";
                connection = ConnectionPool.getConnection();
                statement = connection.createStatement();
                ResultSet rsPlayerScore = statement.executeQuery(sqlGetPlayerScore);
                if(rsPlayerScore.next()){
                    String points = rsPlayerScore.getInt(1) + "p";
                    System.out.println(points);
                    totalScore.setText(points);
                }

                resetGameId(); // resets gameId to play new game

            }else{
                btnChallenge.setVisible(false);
                resultText.setText("Waiting for opponent to finish game");
                totalScoreText.setVisible(false);
                timerRes();
                return false;
            }
            return true;

        }catch (SQLException e) {
            e.printStackTrace();
            return false;

        }finally {
            Cleaner.close(statement, rs, connection);
        }
    }

    public int checkResult(int myScore, int opponentScore){

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();

            String sqlUpdatePlayerScore = "";
            if (myScore > opponentScore) {
                sqlUpdatePlayerScore = "UPDATE Player SET points= points +" + myScore + " WHERE username ='" + username + "';";
                statement.executeUpdate(sqlUpdatePlayerScore);
                addGamesWon(username);
                return 1;
            } else {
                addGamesLost(username);
                return 0;
            }
        }
        catch(SQLException e){
            e.printStackTrace();
            return -1;
        }
        finally {
            Cleaner.close(statement, null, connection);
        }
    }

    public boolean deleteGame(int game){
        ResultSet rs = null;
        String sqlCheckIfOtherPlayerHasLeft = "SELECT gameId FROM Player WHERE gameId =" + game + ";";

        try{
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            rs = statement.executeQuery(sqlCheckIfOtherPlayerHasLeft);

            if (!rs.next()) {
                String sqlDeleteGame = "DELETE FROM Game WHERE gameId =" + game + ";";
                statement.executeUpdate(sqlDeleteGame);
                return true;
            }
            return false;
        }
        catch(SQLException e){
            e.printStackTrace();
            return false;
        }
        finally {
            Cleaner.close(statement,rs,connection);
        }
    }

    public boolean sceneGame(ActionEvent event) {
        if (bothFinished) {
            deleteGame(gameId);
            ChangeScene.change(event, "/Scenes/Game.fxml");
            return true;
        }
        return false;
    }

    public boolean sceneChallengeUser(ActionEvent event){
        if (bothFinished) {
            deleteGame(gameId);
            ChangeScene.change(event, "/Scenes/ChallengeUser.fxml");
            return true;
        }
        return false;
    }

    public void sceneResult(ActionEvent event) {
        ChangeScene.change(event, "/Scenes/Result.fxml");
        btnChallenge.setVisible(true);
    }

    public void timerRes(){
        timerR = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if(checkFinish(gameId)) {
                    turnOfTimerR();
                    btnNext.setVisible(true);
                    return;
                }
            }
        };
        timerR.schedule(task, 5000, 3000);
    }

    public boolean checkFinish(int game) {
        ResultSet rs = null;
        String me = findUser();
        String opponentFinished = (me.equals("player1") ? "p2Finished" : "p1Finished");

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            String sqlCheck = "SELECT p1Finished, p2Finished FROM Game WHERE gameId = " + game + ";";
            rs = statement.executeQuery(sqlCheck);
            if(rs.next()){
                int user = rs.getInt(opponentFinished);
                if (user == 1) {
                    return true;
                }
            }
            return false;
        }catch (SQLException e) {
            e.printStackTrace();
            return false;
        }finally {
            Cleaner.close(statement, rs, connection);
        }
    }

    public void turnOfTimerR() {
        if (timerR != null) {
            timerR.cancel();
            timerR.purge();
            return;
        }
    }

    public boolean addGamesLost(String user){

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();

            String sqlUpdatePlayerScore = "UPDATE Player SET gamesLost= gamesLost + 1 WHERE username ='" + user + "';";
            statement.executeUpdate(sqlUpdatePlayerScore);
            return true;

        }catch (SQLException e) {
            e.printStackTrace();
            return false;
        }finally {
            Cleaner.close(statement, null, connection);
        }
    }
    public boolean addGamesWon(String user){

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();

            String sqlUpdatePlayerScore = "UPDATE Player SET gamesWon= gamesWon + 1 WHERE username ='" + user + "';";
            statement.executeUpdate(sqlUpdatePlayerScore);
            return true;

        }catch (SQLException e) {
            e.printStackTrace();
            return false;
        }finally {
            Cleaner.close(statement, null, connection);
        }
    }
}