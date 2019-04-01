package sample;

import javafx.event.ActionEvent;
import Connection.ConnectionPool;
import Connection.Cleaner;
import javafx.scene.control.Button;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.sql.*;

import static sample.ControllerHome.getUserName;
import static sample.ControllerOpponent.getGameId;

public class ControllerRefresh {

    @FXML
    public Button acc;
    public Button dec;

    public Label challenger;

    private static String username = getUserName();

    private static Connection connection = null;
    private static Statement statement = null;
    private static ResultSet rs = null;


    public static void refresh(ActionEvent event) {

        try {
            String sql = "SELECT gameId FROM Player WHERE username = '" + username + "';";

            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            rs = statement.executeQuery(sql);
            rs.next();

            int playerGameId = rs.getInt(1);

            if(playerGameId != 0) {
                sql = "SELECT player1, categoryId FROM Game WHERE player1 = '" + username + "' OR player2 = '" + username + "' ;";
                rs = statement.executeQuery(sql);
                rs.next();
                if (rs.getString("player1").equals(username)) {
                    if(rs.getInt("categoryId") == 0){
                        ChangeScene.change(event, "Wait.fxml");
                    }else{
                        ChangeScene.change(event, "Question.fxml");
                    }
                }
                else {
                    ChangeScene.change(event, "Challenged.fxml");
                }
            }else {
                ChangeScene.change(event, "ChallengeUser.fxml");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Cleaner.close(statement, rs, connection);
        }
    }

    public void initialize() {
        acc.setStyle("-fx-background-color: #a3f267");
        dec.setStyle("-fx-background-color: #F29B7F");

        int gameId = getGameId();

        String sqlOtherPlayer = "SELECT username FROM Player WHERE gameId = " + gameId + " AND username != '" + username + "';";

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            rs = statement.executeQuery(sqlOtherPlayer);

            //if the other player hasn't quitted before you open the game
            if(rs.next()) {
                String challengingPlayer = rs.getString("username");
                challenger.setText("You've been challenged by " + challengingPlayer);
            } else {
                String sqlRemoveGameIdFromPlayer = "UPDATE Player SET gameId=NULL WHERE username ='" + username + "';";
                statement.executeUpdate(sqlRemoveGameIdFromPlayer);
                String sqlDeleteGame = "DELETE FROM Game WHERE gameId =" + gameId + ";";
                statement.executeUpdate(sqlDeleteGame);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Cleaner.close(statement, rs, connection);
        }
    }

    public void accept(ActionEvent event) {
        ChangeScene.change(event, "Category.fxml");
    }

    public void decline(ActionEvent event) {
        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;

        try{
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();

            //Updates both players with a gameId that points to the new game
            String sqlGetPlayer1 = "SELECT player1 FROM Game WHERE player2 = '" + username + "';";
            rs = statement.executeQuery(sqlGetPlayer1);
            rs.next();
            String player1 = rs.getString("player1");

            String sqlInsert = "UPDATE `Player` SET `gameId` = NULL WHERE `Player`.`username` = '" + player1 + "'";
            statement.executeUpdate(sqlInsert);

            sqlInsert = "UPDATE `Player` SET `gameId` = NULL WHERE `Player`.`username` = '" + username + "';";
            statement.executeUpdate(sqlInsert);

            String sqlDeleteGame = "DELETE FROM Game WHERE player2 = '" + username + "';";
            statement.executeUpdate(sqlDeleteGame);

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            Cleaner.close(statement, null, connection);
            ChangeScene.change(event, "ChallengeUser.fxml");
        }
    }

    public void sceneHome(ActionEvent event) { //feedback knapp
        ChangeScene.change(event, "Game.fxml"); //bruker super-metode
    }
}
