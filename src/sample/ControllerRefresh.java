package sample;

import javafx.event.ActionEvent;
import Connection.ConnectionPool;
import Connection.Cleaner;
import javafx.scene.control.Button;
import javafx.fxml.FXML;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.sql.*;

import static sample.ControllerHome.getUserName;
import static sample.ChooseOpponent.getGameId;

public class ControllerRefresh {

    @FXML
    public Button acc;
    public Button dec;

    private static String username = getUserName();

    public static void refresh(ActionEvent event) {
        Connection connection = null;
        Statement statement = null;

        String sql = "SELECT gameId FROM Player WHERE username = '" + username + "';";


        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            rs.next();

            int challenge = rs.getInt(1);

            if (challenge != 0) {
                ChangeScene.change(event, "Challenged.fxml");
            } else {
                ChangeScene.change(event, "ChallangeUser.fxml");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Cleaner.close(statement, null, connection);
        }
    }

    public void challenged() {
        /*acc.setStyle("-fx-background-color: #a3f267");
        dec.setStyle("-fx-background-color: #F29B7F");*/

    }

    public void accept(ActionEvent event) {
        ChangeScene.change(event, "Category.fxml");
    }

    public void decline(ActionEvent event) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;

        try{
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();

            //Updates both players with a gameId that points to the new game
            String sqlGetPlayer1 = "SELECT player1 FROM Game WHERE player1 = '" + username + "';";
            rs = statement.executeQuery(sqlGetPlayer1);
            rs.next();
            String player1 = rs.getString("player1");

            String sqlInsert = "UPDATE `Player` SET `gameId` = NULL WHERE `Player`.`username` = '" + player1 + "'";
            statement.executeUpdate(sqlInsert);

            sqlInsert = "UPDATE `Player` SET `gameId` = NULL WHERE `Player`.`username` = '" + username + "';";
            statement.executeUpdate(sqlInsert);

            String sqlDeleteGame = "DELETE FROM Game WHERE player1 = '" + username + "';";
            statement.executeUpdate(sqlDeleteGame);

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            Cleaner.close(statement, null, connection);
            ChangeScene.change(event, "ChallangeUser.fxml");
        }
    }

    public void sceneHome(ActionEvent event) { //feedback knapp
        ChangeScene.change(event, "Game.fxml"); //bruker super-metode
    }
}
