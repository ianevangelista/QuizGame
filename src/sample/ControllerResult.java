package sample;

import static sample.ControllerHome.getUserName;
import static sample.ControllerQuestion.findUser;
import static sample.Logout.logOut;

import Connection.ConnectionPool;
import Connection.Cleaner;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import javax.swing.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

import static sample.ChooseOpponent.getGameId;

public class ControllerResult {

    private String username = getUserName();
    private int gameId = getGameId();
    private java.util.Timer timerR;
    private int teller = 0;
    private Connection connection = null;
    private Statement statement = null;
    private boolean finished = false;

    @FXML
    //result
    public Text totalScore;
    public Text resultText;
    public Button btnNext;
    public Button btnChallenge;
    public Text totalScoreText;
    public Text yourScore;
    public Text theirScore;

    public void sceneGame(ActionEvent event) { ChangeScene.change(event, "Game.fxml"); }

    public void sceneChallengeUser(ActionEvent event){ ChangeScene.change(event, "ChallengeUser.fxml");}

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

            //Removes gameId from player so that they can play a new game
            String sqlRemoveGameIdFromPlayer = "UPDATE Player SET gameId=NULL WHERE username ='" + username +"';";
            //slå av autocommit??? rollback osv?
            statement.executeUpdate(sqlRemoveGameIdFromPlayer);

            if(p1Finished == 1 && p2Finished == 1) {
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
                } else {
                    resultText.setText("You lost :(");
                }


                String sqlGetPlayerScore = "SELECT points FROM Player WHERE username = '" + username + "';";
                rs = statement.executeQuery(sqlGetPlayerScore);
                if(rs.next()){
                    String points = rs.getInt("points") + "p";
                    totalScore.setText(points);
                }


                //utfør sletting, blir gameId sletta på spilleren som spiller da?
            }


            /*//Temporaraly disable foreign key
            statement.executeUpdate("SET FOREIGN_KEY_CHECKS=0;");

            //Removes gameId from player so that they can play a new game
            String sqlRemoveGameIdFromPlayer = "UPDATE Player SET gameId=NULL WHERE username ='" + username +"';";
            //slå av autocommit??? rollback osv?
            statement.executeUpdate(sqlRemoveGameIdFromPlayer);

            //if both are finished
            if(p1Finished == 1 && p2Finished == 1) {
                String sqlDeleteGame = "DELETE FROM Game WHERE gameId =" + gameId + ";";
                statement.executeUpdate(sqlDeleteGame);
                //utfør sletting, blir gameId sletta på spilleren som spiller da?

                //Enable foreign key
                statement.executeUpdate("SET FOREIGN_KEY_CHECKS=1;");

                int p1Points = rs.getInt("p1Points");
                int p2Points = rs.getInt("p2Points");
                String player1Id = rs.getString("player1");
                String player2Id = rs.getString("player2");

                String sqlUpdatePlayerScore = "";

                if(player == "player1"){
                    if(p1Points > p2Points){
                        resultText.setText("You won! :)");
                        sqlUpdatePlayerScore = "UPDATE Player SET points= points +" + p1Points + " WHERE username ='" + player1Id +"';";
                    } else {
                        resultText.setText("You lost :(");
                        sqlUpdatePlayerScore = "UPDATE Player SET points= points +" + p2Points + " WHERE username ='" + player2Id +"';";
                    }
                } else {
                    if(p2Points > p1Points){
                        resultText.setText("You won! :)");
                        sqlUpdatePlayerScore = "UPDATE Player SET points= points +" + p1Points + " WHERE username ='" + player1Id +"';";
                    } else {
                        resultText.setText("You lost :(");
                        sqlUpdatePlayerScore = "UPDATE Player SET points= points +" + p2Points + " WHERE username ='" + player2Id +"';";
                    }
                }
                statement.executeUpdate(sqlUpdatePlayerScore);

                String sqlGetPlayerScore = "SELECT points FROM Player WHERE username = " + username;
                rs = statement.executeQuery(sqlGetPlayerScore);
                rs.next();

                String points = rs.getInt("points") + "p";
                totalScore.setText(points);*/

            else{
                ChangeScene.changeVisibilityBtn(false, btnChallenge);
                resultText.setText("Waiting for opponent to finish game");
                totalScoreText.setVisible(false);
                timerRes();

                //TODO make game know if waiting player won or lost (Use score delta)
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }finally {
            Cleaner.close(statement, rs, connection);
        }
    }

    public void showBtn(){
        ChangeScene.changeVisibilityBtn(true, btnNext);
    }

    public void sceneResult(ActionEvent event) { //hjemknapp
        ChangeScene.change(event, "Result.fxml");
        Connection connection = null;
        Statement statement = null;
        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            String sqlDeleteGame = "DELETE FROM Game WHERE gameId ='" + gameId + "';";
            statement.executeUpdate(sqlDeleteGame);

         } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            Cleaner.close(statement, null, connection);
            ChangeScene.changeVisibilityBtn(true, btnChallenge);
        }
    }


        public void timerRes(){
        timerR = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if(checkRes()) {
                    turnOfTimerR();
                    showBtn();
                    finished = true;
                    System.out.println("funker i run");
                }
            }
        };
        timerR.schedule(task, 10000, 3000);
    }

    public boolean checkRes() {
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
                    System.out.println("funker i checkRes");
                    return true;
                }
            }
            return false;
        }catch (SQLException e) {
            e.printStackTrace();
            return false;
        }finally {
            Cleaner.close(statement, rs, connection);
            System.out.println(opponentFinished);
        }
    }

    public void turnOfTimerR() {
        if (timerR != null) {
            timerR.cancel();
        }
    }
}