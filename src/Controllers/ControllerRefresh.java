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

    @FXML
    public Button acc;
    public Button dec;
    public Label challenger;

    private static String username = getUserName();
    private static Connection connection = null;
    private static Statement statement = null;
    private static ResultSet rs = null;

    /**
     * This method runs when pressing the start button after logging in.
     * The method runs start.
     */

    public void initialize() {
        start(username);
    }

    /**
     * The method runs checks if you have been challenged.
     * @param user is the your username.
     * @return true if you have a gameId or false if not.
     */

    public boolean start(String user){
        int gameId = getGameId();

        String sqlOtherPlayer = "SELECT username FROM Player WHERE gameId = " + gameId + " AND username != '" + user + "';";

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            rs = statement.executeQuery(sqlOtherPlayer);

            //if the other player hasn't quitted before you open the game
            if(rs.next()) {
                String challengingPlayer = rs.getString("username");
                challenger.setText("You've been challenged by " + challengingPlayer);
                return true;
            } else {
                String sqlRemoveGameIdFromPlayer = "UPDATE Player SET gameId=NULL WHERE username ='" + user + "';";
                statement.executeUpdate(sqlRemoveGameIdFromPlayer);
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
     * The method refreshes the player's gameId.
     * It will check if you have challenged a player or have been challenged.
     * @param event is a neccessary paramater which is used in a method from the class ChangeScene.
     * @param user you username.
     * @return an int 1 if you have a gameId, are player1 and no category has been set.
     * If there is a category set return 0.
     * If you are not player1, return -1.
     * If there are no gameId, return -1.
     */

    public static int refresh(ActionEvent event, String user) {
        try {
            String sql = "SELECT gameId FROM Player WHERE username = '" + user + "';";
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            rs = statement.executeQuery(sql);
            rs.next();

            int playerGameId = rs.getInt(1);
            if(playerGameId != 0) {
                sql = "SELECT player1, categoryId FROM Game WHERE player1 = '" + user + "' OR player2 = '" + user + "' ;";
                rs = statement.executeQuery(sql);
                rs.next();
                if (rs.getString("player1").equals(user)) {
                    if(rs.getInt("categoryId") == 0){
                        ChangeScene.change(event, "/Scenes/Wait.fxml");
                        return 1;
                    }else{
                        ChangeScene.change(event, "/Scenes/Question.fxml");
                        return 0;
                    }
                }
                else {
                    ChangeScene.change(event, "/Scenes/Challenged.fxml");
                    return -1;
                }
            }else {
                ChangeScene.change(event, "/Scenes/ChallengeUser.fxml");
                return -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } finally {
            Cleaner.close(statement, rs, connection);
        }
    }

    /**
     * The method accepts a game request.
     * @param event is a neccessary paramater which is used in a method from the class ChangeScene.
     */
    public void accept(ActionEvent event) {
        ChangeScene.change(event, "/Scenes/Category.fxml");
    }

    /**
     * The method declines a game request.
     * Removes the gameId of both players in Player.
     * Deletes the entire game in Game.
     * @param event is a neccessary paramater which is used in a method from the class ChangeScene.
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
            rs.next();
            String player1 = rs.getString("player1");

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
     * @param event is a neccessary paramater which is used in a method from the class ChangeScene.
     */
    public void sceneHome(ActionEvent event) { //feedback knapp
        ChangeScene.change(event, "/Scenes/Game.fxml");
    }
}
