package Controllers;

import static Controllers.ControllerHome.getUserName;
import static Controllers.ControllerQuestion.findUser;
import Connection.ConnectionPool;
import Connection.Cleaner;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

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

    @FXML
    //result
    public Text totalScore;
    public Text resultText;
    public Button btnNext;
    public Button btnChallenge;
    public Text totalScoreText;
    public Text yourScore;
    public Text theirScore;

    public void initialize() {
        Connection connection = null;
        Statement statement = null;
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
                bothFinished = true;

                String sqlUpdatePlayerScore = "";
                totalScoreText.setVisible(true);
                theirScore.setVisible(true);
                yourScore.setVisible(true);
                theirScore.setText("Your opponent got " + opponentPoints + " points this round.");
                yourScore.setText("You got " + mePoints + " points this round!");
                if (mePoints > opponentPoints) {
                    resultText.setText("You won! :)");
                    sqlUpdatePlayerScore = "UPDATE Player SET points= points +" + mePoints + " WHERE username ='" + username + "';";
                    statement.executeUpdate(sqlUpdatePlayerScore);
                    addGamesWon();
                } else {
                    resultText.setText("You lost :(");
                    addGamesLost();
                }

                resetGameId(); // resets gameId to play new game

                String sqlGetPlayerScore = "SELECT points FROM Player WHERE username = '" + username + "';";
                rs = statement.executeQuery(sqlGetPlayerScore);
                if(rs.next()){
                    String points = rs.getInt("points") + "p";
                    totalScore.setText(points);
                }

            }else{
                btnChallenge.setVisible(false);
                resultText.setText("Waiting for opponent to finish game");
                totalScoreText.setVisible(false);
                timerRes();
            }

        }catch (SQLException e) {
            e.printStackTrace();
        }finally {
            Cleaner.close(statement, rs, connection);
        }
    }

    public void sceneGame(ActionEvent event) {
        if (bothFinished) {
            Connection connection = null;
            Statement statement = null;
            ResultSet rs = null;
            try {
                connection = ConnectionPool.getConnection();
                statement = connection.createStatement();
                String sqlCheckIfOtherPlayerHasLeft = "SELECT gameId FROM Player WHERE gameId =" + gameId + ";";
                rs = statement.executeQuery(sqlCheckIfOtherPlayerHasLeft);

                if (!rs.next()) {
                    String sqlDeleteGame = "DELETE FROM Game WHERE gameId =" + gameId + ";";
                    statement.executeUpdate(sqlDeleteGame);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                Cleaner.close(statement, rs, connection);
                ChangeScene.change(event, "/Scenes/Game.fxml");
            }
        }
    }

    public void sceneChallengeUser(ActionEvent event){
        if (bothFinished) {
            Connection connection = null;
            Statement statement = null;
            ResultSet rs = null;
            try {
                connection = ConnectionPool.getConnection();
                statement = connection.createStatement();
                String sqlCheckIfOtherPlayerHasLeft = "SELECT gameId FROM Player WHERE gameId =" + gameId + ";";
                rs = statement.executeQuery(sqlCheckIfOtherPlayerHasLeft);

                if (!rs.next()) {
                    String sqlDeleteGame = "DELETE FROM Game WHERE gameId =" + gameId + ";";
                    statement.executeUpdate(sqlDeleteGame);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                Cleaner.close(statement, rs, connection);
                ChangeScene.change(event, "/Scenes/ChallengeUser.fxml");
            }
        }
    }

    public void showBtn(){
        btnNext.setVisible(true);
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
                if(checkRes()) {
                    turnOfTimerR();
                    showBtn();
                    return;
                }
            }
        };
        timerR.schedule(task, 5000, 3000);
    }

    public boolean checkRes() {
        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;
        String me = findUser();
        String opponentFinished = (me.equals("player1") ? "p2Finished" : "p1Finished");

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            String sqlCheck = "SELECT p1Finished, p2Finished FROM Game WHERE gameId = " + gameId + ";";
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

    public void addGamesLost(){
        Connection connection = null;
        Statement statement = null;

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();

            String sqlUpdatePlayerScore = "";
            sqlUpdatePlayerScore = "UPDATE Player SET gamesLost= gamesLost + 1 WHERE username ='" + username + "';";
            statement.executeUpdate(sqlUpdatePlayerScore);

        }catch (SQLException e) {
            e.printStackTrace();
        }finally {
            Cleaner.close(statement, null, connection);
        }
    }
    public void addGamesWon(){
        Connection connection = null;
        Statement statement = null;

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();

            String sqlUpdatePlayerScore = "";
            sqlUpdatePlayerScore = "UPDATE Player SET gamesWon= gamesWon + 1 WHERE username ='" + username + "';";
            statement.executeUpdate(sqlUpdatePlayerScore);

        }catch (SQLException e) {
            e.printStackTrace();
        }finally {
            Cleaner.close(statement, null, connection);
        }
    }
}