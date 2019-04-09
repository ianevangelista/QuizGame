package Controllers;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import Connection.Cleaner;
import Connection.ConnectionPool;
import javafx.scene.control.ListView;

/**
 * The class ControllerHighScore is the home page after logging in.
 * It will displays top 5 players with the highest points.
 */

public class ControllerHighScore {

    // Fxml elements
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
        // Set up connection variables
        Connection connection = null;
        Statement statement = null;
        ResultSet hs = null;

        // get top 5 players from the database
        String sqlHighScore = "SELECT username, points FROM `Player` ORDER BY points desc LIMIT 5;";

        ObservableList<String> usernameList = FXCollections.observableArrayList();
        ObservableList<String> pointsList = FXCollections.observableArrayList();

        try {
            // Set up connection to database
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();

            //Add usernames and points of top 5 playes to ObersvableLists
            hs = statement.executeQuery(sqlHighScore);
            while(hs.next()){
                usernameList.add( hs.getString("username"));
                pointsList.add( Integer.toString(hs.getInt("points")));
            }

            // Add the Observable lists to the coloumns in ListView in the scene
            userCol.setItems(usernameList);
            scoreCol.setItems(pointsList);

        }  catch(SQLException sqle){
            // Database access error
            System.out.println("Database access error");
            sqle.printStackTrace();
        } catch (Exception e) {
            // If something else goes wrong
            e.printStackTrace();
        } finally {
            // Close connection
            Cleaner.close(statement, hs, connection);
        }
    }
}
