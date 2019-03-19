package sample;

import javafx.event.ActionEvent;
import Connection.ConnectionPool;
import Connection.Cleaner;
import javafx.scene.control.Button;
import javafx.fxml.FXML;
import java.sql.*;

import static sample.ControllerHome.getUserName;

public class ControllerRefresh {

    @FXML
    public Button acc;
    public Button dec;

    private static String username = getUserName();

    public static void refresh(ActionEvent event) {
        Connection connection = null;
        Statement statement = null;
        ResultSet rsSelect = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT gameId FROM Player WHERE username = '" + username + "';";

            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            rsSelect = statement.executeQuery(sql);
            rsSelect.next();

            int playerGameId = rsSelect.getInt(1);

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
            Cleaner.close(statement, null, connection);
        }
    }

    /*public void challenged() {
        acc.setStyle("-fx-background-color: #a3f267");
        dec.setStyle("-fx-background-color: #F29B7F");
    }*/

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
