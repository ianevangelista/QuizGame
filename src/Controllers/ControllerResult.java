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

/**
 * The class ControllerResult is used when checking the result of both players after a game.
 */
public class ControllerResult {

    //Static method from the class ControllerHome
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

    /**
     * This method runs when a player is finished with all three questions.
     * Checks player1 and player2 with checkResult.
     * If both are finished, compare and show result.
     * @return true if both finished, or false if none or one finished.
     */
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

            //get bool value of whether they are finished or not
            int p1Finished = rs.getInt("p1Finished");
            int p2Finished = rs.getInt("p2Finished");
            int mePoints = rs.getInt(me);
            int opponentPoints = rs.getInt(opponent);

            //Prints the result if both is finished
            if(p1Finished == 1 && p2Finished == 1) {
                totalScoreText.setVisible(true);
                theirScore.setVisible(true);
                yourScore.setVisible(true);
                theirScore.setText("Your opponent got " + opponentPoints + " points this round.");
                yourScore.setText("You got " + mePoints + " points this round!");
                bothFinished = true;

                checkResult(mePoints, opponentPoints);

                //Checks who won
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

                //prints the score
                if(rsPlayerScore.next()){
                    String points = rsPlayerScore.getInt(1) + "p";
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

    /**
     * This method compares the result of both players.
     * The players will get their statistics updated with addGamesWon and addGamesLost.
     * @param myScore your score.
     * @param opponentScore your opponent's score.
     * @return 1 if your score is higher than the opponent or 0 if not.
     */
    public int checkResult(int myScore, int opponentScore){

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();

            String sqlUpdatePlayerScore = "";
            // Checks if your score is higher and updates the database
            if (myScore > opponentScore) {
                sqlUpdatePlayerScore = "UPDATE Player SET points= points +" + myScore + " WHERE username ='" + username + "';";
                statement.executeUpdate(sqlUpdatePlayerScore);
                //adds to games won using addGamesWon
                addGamesWon(username);
                return 1;
            } else {
                //adds to games lost using addGamesLost
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

    /**
     * The method deletes the game.
     * @param game is the gameId.
     * @return true if the game still exists and deletes the game or false if does not exist.
     */
    public boolean deleteGame(int game){
        ResultSet rs = null;
        String sqlCheckIfOtherPlayerHasLeft = "SELECT gameId FROM Player WHERE gameId =" + game + ";";

        try{
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            rs = statement.executeQuery(sqlCheckIfOtherPlayerHasLeft);

            //if both players have left, the last til leave, will delete the game from the database
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

    /**
     * The method changes scene to Game.
     * Deletes the current game you are in if you are leaving and both players are finished.
     * @param event is a neccessary paramater which is used in a method from the class ChangeScene.
     * @return true both players are finished or false if not.
     */
    public boolean sceneGame(ActionEvent event) {
        if (bothFinished) {
            //Deletes game when changing scene
            deleteGame(gameId);
            ChangeScene.change(event, "/Scenes/Game.fxml");
            return true;
        }
        return false;
    }

    /**
     * The method changes scene to ChallengeUser.
     * Deletes the current game you are in if you are leaving and both players are finished.
     * @param event is a neccessary paramater which is used in a method from the class ChangeScene.
     * @return true both players are finished or false if not.
     */
    public boolean sceneChallengeUser(ActionEvent event){
        if (bothFinished) {
            //Deletes game when changing scene
            deleteGame(gameId);
            ChangeScene.change(event, "/Scenes/ChallengeUser.fxml");
            return true;
        }
        return false;
    }

    /**
     * The method changes scene to Result.
     * @param event is a neccessary paramater which is used in a method from the class ChangeScene.
     */
    public void sceneResult(ActionEvent event) {
        ChangeScene.change(event, "/Scenes/Result.fxml");
        btnChallenge.setVisible(true);
    }

    /**
     * The method schedules a timer if you are finished and are waiting for the other player to finish.
     * Uses checkFinish to check the opponent.
     * Starts five seconds after you have finished and repeats every three seconds.
     */
    public void timerRes(){
        timerR = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if(checkFinish(gameId, findUser())) {
                    turnOffTimerR();
                    btnNext.setVisible(true);
                    return;
                }
            }
        };
        timerR.schedule(task, 5000, 3000);
    }

    /**
     * The method checks if your opponent has finished
     * @param game is the gameId
     * @return true the opponent has finished or false if not
     */
    public boolean checkFinish(int game, String me) {
        ResultSet rs = null;
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

    /**
     * The method terminates the timer.
     */
    public void turnOffTimerR() {
        if (timerR != null) {
            timerR.cancel();
            timerR.purge();
            return;
        }
    }

    /**
     * The method adds total losses in Player.
     * @param user is the username.
     * @return true if total losses is updated or false if not.
     */
    public boolean addGamesLost(String user){

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();

            String sqlUpdatePlayerScore = "UPDATE Player SET gamesLost= gamesLost + 1 WHERE username ='" + user + "';";
            // adds games lost in the database
            statement.executeUpdate(sqlUpdatePlayerScore);
            return true;

        }catch (SQLException e) {
            e.printStackTrace();
            return false;
        }finally {
            Cleaner.close(statement, null, connection);
        }
    }

    /**
     * The method adds total wins in Player.
     * @param user is the username.
     * @return true if total wins is updated or false if not.
     */
    public boolean addGamesWon(String user){

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();

            String sqlUpdatePlayerScore = "UPDATE Player SET gamesWon= gamesWon + 1 WHERE username ='" + user + "';";
            //adds games won in the database
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