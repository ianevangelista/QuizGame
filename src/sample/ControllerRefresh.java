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

    private String username = getUserName();

    public void refresh(ActionEvent event) {
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
        ChooseCategory category = new ChooseCategory();
        category.initialize();
        ChangeScene.change(event, "Category.fxml");
    }

    public void decline(ActionEvent event) throws SQLException {
        Connection connection = null;
        Statement statement = null;

        String sql = "DELETE FROM Game WHERE player2 = '" + username + "';";

        try{
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();


            System.out.println(username);
            statement.executeUpdate(sql);

            ChangeScene.change(event, "ChallangeUser.fxml");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            Cleaner.close(statement, null, connection);
        }
    }
}
