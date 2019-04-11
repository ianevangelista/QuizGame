package Controllers;

import javafx.event.ActionEvent;
import Connection.ConnectionPool;
import Connection.Cleaner;
import javafx.scene.control.Button;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.sql.*;

import static Controllers.ControllerHome.getUserName;
import static Controllers.ControllerOpponent.getGameId;

/**
 * The class ControllerRefresh is used to check if have been challenged when pressing start after logging in.
 * It will also provide you the choice of accepting or declining the request if you are challenged.
 */

public class ControllerRefresh {

    //FXML
    @FXML
    public Button acc;
    public Button dec;
    public Label challenger;


    // Static method from ControllerHome
    private static String username = getUserName();

    /**
     * This method runs when pressing the start button after logging in.
     * The method runs start.
     */

    public void initialize() {
        start();
    }

    /**
     * The method runs checks if you have been challenged.
     * @return true if you have a gameId or false if not.
     */

    public boolean start(){
        // Connection objects
        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;

        int gameId = getGameId();
        String username = getUserName();
        String sqlOtherPlayer = "SELECT username FROM Player WHERE gameId = " + gameId + " AND username != '" + username + "';";

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            rs = statement.executeQuery(sqlOtherPlayer);
            //if the other player has not quit before you open the game
            if(rs.next()) {
                String challengingPlayer = rs.getString("username");
                //Prints text at the label
                setChallengeText("You've been challenged by " + challengingPlayer);
                return true;
            } else {
                // Updating the player in the database
                String sqlRemoveGameIdFromPlayer = "UPDATE Player SET gameId=NULL WHERE username ='" + username + "';";
                statement.executeUpdate(sqlRemoveGameIdFromPlayer);
                // Deleting the game from the database
                String sqlDeleteGame = "DELETE FROM Game WHERE gameId =" + gameId + ";";
                statement.executeUpdate(sqlDeleteGame);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            Cleaner.close(statement, rs, connection);
        }
    }

    /**
     * This method prints a text to the label
     * @param text the text you want to print
     */
    public void setChallengeText(String text) {
        if(challenger != null) {
            challenger.setText(text);
        }
    }

    /**
     * The method refreshes the player's gameId.
     * It will check if you have challenged a player or have been challenged.
     * @param event is a necessary parameter which is used in a method from the class ChangeScene.
     * @return an int 1 if you have a gameId, are player1 and no category has been set.
     * If there is a category set return 0.
     * If you are not player1, return -1.
     * If there are no gameId, return -1.
     */

    /**
     * This method changes the scene to the right one depending on getCorrectScene
     * @param event is a necessary parameter which is used in a method from the class ChangeScene.
     */
    public static void refresh(ActionEvent event) {
        String filename = getCorrectScene();
        ChangeScene.change(event, filename);
    }

    /**
     * This method finds out what condition the player is in, and finds out what the consequences of this.
     * @return a string depending on what the next scene should be
     */
    public static String getCorrectScene() {
        // Connection objects
        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;

        try {
            String username = getUserName();
            String sql = "SELECT gameId FROM Player WHERE username = '" + username + "';";
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            rs = statement.executeQuery(sql);
            rs.next();

            int playerGameId = rs.getInt(1);
            //If the player is online
            if(playerGameId != 0) {
                sql = "SELECT player1, categoryId FROM Game WHERE player1 = '" + username + "' OR player2 = '" + username + "' ;";
                rs = statement.executeQuery(sql);
                rs.next();
                // If you are player1
                if (rs.getString("player1").equals(username)) {
                    // If the category is not already chosen
                    if(rs.getInt("categoryId") == 0){
                        return "/Scenes/Wait.fxml";
                    }else{
                        return "/Scenes/Question.fxml";
                    }
                }
                else {
                    return "/Scenes/Challenged.fxml";
                }
            }else {
                return "/Scenes/ChallengeUser.fxml";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        } finally {
            Cleaner.close(statement, rs, connection);
        }
    }

    /**
     * The method accepts a game request.
     * @param event is a necessary parameter which is used in a method from the class ChangeScene.
     */
    public void accept(ActionEvent event) {
        ChangeScene.change(event, "/Scenes/Category.fxml");
    }

    /**
     * The method declines a game request.
     * Removes the gameId of both players in Player.
     * Deletes the entire game in Game.
     * @param event is a necessary parameter which is used in a method from the class ChangeScene.
     */
    public boolean decline(ActionEvent event) {
        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;

        try{
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();

            //Updates both players with a gameId that points to the new game
            String sqlGetPlayer1 = "SELECT player1 FROM Game WHERE player2 = '" + username + "';";
            rs = statement.executeQuery(sqlGetPlayer1);
            String player1 = "";
            if(rs.next()) {
                player1 = rs.getString("player1");
            }
            String sqlInsert = "UPDATE `Player` SET `gameId` = NULL WHERE `Player`.`username` = '" + player1 + "'";
            statement.executeUpdate(sqlInsert);

            sqlInsert = "UPDATE `Player` SET `gameId` = NULL WHERE `Player`.`username` = '" + username + "';";
            statement.executeUpdate(sqlInsert);

            String sqlDeleteGame = "DELETE FROM Game WHERE player2 = '" + username + "';";
            statement.executeUpdate(sqlDeleteGame);
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        finally {
            Cleaner.close(statement, null, connection);
            ChangeScene.change(event, "/Scenes/ChallengeUser.fxml");
        }
    }

    /**
     * The method changes scene to Game.
     * @param event is a necessary parameter which is used in a method from the class ChangeScene.
     */
    public void sceneHome(ActionEvent event) { //feedback knapp
        ChangeScene.change(event, "/Scenes/Game.fxml");
    }
}
