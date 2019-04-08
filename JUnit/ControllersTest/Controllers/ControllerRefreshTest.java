package Controllers;

import Connection.ConnectionPool;
import javafx.event.ActionEvent;
import org.junit.Test;

import javax.swing.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.*;

public class ControllerRefreshTest {

    Connection connection = null;
    Statement statement = null;
    ControllerRefresh cr = new ControllerRefresh();
    ControllerHome ch = new ControllerHome();

    @Test
    public void startTest() {
        String username = "juni";
        String p2 = "ian";
        int gameid = 18;
        String sqlInsertGame = "INSERT INTO Game (gameId, player1, player2, p1Points, p2Points) VALUES (" + gameid + ", '" + username + "', '" + p2 + "', 0, 0);";
        String sqlUpdate = "UPDATE Player SET gameId = " + gameid + " WHERE username = '" + username + "' AND username = '" + p2 + "';";
        String sqlDeleteGameId = "UPDATE Player SET gameId = NULL WHERE username = '" + username + "' AND username = '" + p2 + "';";
        String sqlDeleteGame = "DELETE Game WHERE gameId = " + gameid + ";";

        try{
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            statement.executeUpdate(sqlInsertGame);
            statement.executeUpdate(sqlUpdate);
            boolean result = cr.start(username);
            statement.executeUpdate(sqlDeleteGameId);
            statement.executeUpdate(sqlDeleteGame);

            assertTrue(result);
        }
        catch (SQLException e){
            e.printStackTrace();
            assertFalse(true);
        }
    }

    @Test
    public void refreshTest() {
        String username = "juni";
        int gameid = 118;
        String sqlInsertGame = "INSERT INTO Game (gameId, player1, player2, categoryId) VALUES (" + gameid + ", '" + username + "', 'ian', 1);";
        String sqlUpdate = "UPDATE Player SET gameId = " + gameid + " WHERE username = '" + username + "';";
        String sqlDelete = "UPDATE Player SET gameId = null WHERE username = '" + username + "';";
        String sqlDeleteGame = "DELETE Game WHERE gameId = " + gameid + ";";
        ActionEvent event = new ActionEvent();
        try{
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            statement.executeUpdate(sqlInsertGame);
            statement.executeUpdate(sqlUpdate);
            int result = cr.refresh(event, username);
            int expect = 0;
            statement.executeUpdate(sqlDelete);
            statement.executeUpdate(sqlDeleteGame);
            assertEquals(result, expect);
        }
        catch (SQLException e){
            e.printStackTrace();
            assertFalse(true);
        }
    }

    @Test
    public void decline() {
        ActionEvent event = new ActionEvent();
        ResultSet rs = null;
        String username = ch.setUserName("juni");
        String p2 = "ian";
        int gameid = 60;
        boolean result = false;
        String sqlInsert = "INSERT INTO Game(gameId, player1, player2, p1Points, p2Points) VALUES(" + gameid + ", '"+ p2 + "', '" + username + "', 0, 0);";
        String sqlUpdate1 = "UPDATE Player SET gameId = " + gameid + "WHERE username = '" + username + "';";
        String sqlUpdate2 = "UPDATE Player SET gameId = " + gameid + "WHERE username = '" + p2 + "';";
        String sqlSelect = "SELECT player1, player2 FROM Game WHERE gameId = " + gameid + ";";
        try{
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            statement.executeUpdate(sqlInsert);
            statement.executeUpdate(sqlUpdate1);
            statement.executeUpdate(sqlUpdate2);
            cr.decline(event);
            rs = statement.executeQuery(sqlSelect);

            if(!(rs.next())){
                result = true;
            }
            assertTrue(result);
        }
        catch (SQLException e){
            e.printStackTrace();
            assertTrue(false);
        }
    }
}