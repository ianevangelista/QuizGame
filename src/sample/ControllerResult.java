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
import static sample.ChooseOpponent.getGameId;

public class ControllerResult {

    private String username = getUserName();
    private int gameId = getGameId();

    @FXML
    public Button refresh;
    //result
    public Text totalScore;
    public Text resultText;
    public TextField resultHeading;

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
                    String sqlDeleteGame = "DELETE FROM Game WHERE gameId ='" + gameId + "';";
                    statement.executeUpdate(sqlDeleteGame);
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
                resultText.setText("Waiting for opponent to finish game");

                //TODO make game know if waiting player won or lost (Use score delta)
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }finally {
            Cleaner.close(statement, rs, connection);
        }
    }
}