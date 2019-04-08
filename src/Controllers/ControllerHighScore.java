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

/**
 * The class ControllerHighScore is the home page after logging in.
 * It will displays top 5 players with the highest points.
 */

public class ControllerHighScore {

    @FXML
    public ListView userCol;
    public ListView scoreCol;

    /**
     * This method runs when your access the profile function in the program.
     * The method runs highscoreTable().
     */
    public void initialize(){ highscoreTable(); }

    /**
     * The method changes scene to Game.
     * @param event is a neccessary paramater which is used in a method from the class ChangeScene.
     */
    public void sceneGame(ActionEvent event) { //hjemknapp
        ChangeScene.change(event, "/Scenes/Game.fxml");
    }

    /**
     * The private method connects to the database and fetches the top 5 players with the most points.
     * It displays the users in a ListView.
     */
    private void highscoreTable(){

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
