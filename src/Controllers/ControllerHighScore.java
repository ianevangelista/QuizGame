package Controllers;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import Connection.Cleaner;
import Connection.ConnectionPool;
import javafx.scene.control.ListView;

public class ControllerHighScore {

    @FXML
    public ListView userCol;
    public ListView scoreCol;

    public void initialize(){ highscoreTable(); }

    public void sceneGame(ActionEvent event) { //hjemknapp
        ChangeScene.change(event, "/Scenes/Game.fxml");
    }

    public void highscoreTable(){

        Connection connection = null;
        Statement statement = null;
        ResultSet hs = null;

        String sqlHighScoreUser = "SELECT username FROM `Player` ORDER BY points desc LIMIT 5;";
        String sqlHighScorePoints = "SELECT points FROM `Player` ORDER BY points desc LIMIT 5;";

        ObservableList<String> usernameList = FXCollections.observableArrayList();
        ObservableList<String> pointsList = FXCollections.observableArrayList();

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();

            //Legger navn i tabellen highScoreList
            hs = statement.executeQuery(sqlHighScoreUser);
            while(hs.next()){
                usernameList.add( hs.getString("username"));
            }

            //Legger til poeng i highScoreList
            hs = statement.executeQuery(sqlHighScorePoints);

            while(hs.next()){
                pointsList.add( Integer.toString(hs.getInt("points")));
            }

            userCol.setItems(usernameList);
            scoreCol.setItems(pointsList);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Cleaner.close(statement, hs, connection);
        }
    }
}
