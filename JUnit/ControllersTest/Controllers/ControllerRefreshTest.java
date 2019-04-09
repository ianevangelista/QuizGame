package Controllers;

import Connection.ConnectionPool;
import javafx.event.ActionEvent;
import org.junit.Before;
import org.junit.Test;
import Connection.Cleaner;
import static Controllers.ControllerOpponent.setGameId;
import org.mockito.Mockito;
import org.mockito.Matchers;



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
    private ControllerRefresh mock;
    public String player1 = "juni";
    public String player2 = "ian";
    public int gameId = 1;


    @Before
    public void createGame() {
        String sqlInsertGame = "INSERT INTO Game (gameId, player1, player2) VALUES (" + gameId + ", '" + player1 + "', '" + player2 + "');";
        String sqlUpdateP1 = "UPDATE Player SET gameId = " + gameId + " WHERE username = '" + player1 + "';";
        String sqlUpdateP2 = "UPDATE Player SET gameId = " + gameId + " WHERE username = '" + player2 + "';";
        String sqlDeleteGameIdP1 = "UPDATE Player SET gameId = NULL WHERE username = '" + player1 + "';";
        String sqlDeleteGameIdP2 = "UPDATE Player SET gameId = NULL WHERE username = '" + player2 + "';";
        try{
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            setGameId(gameId);
            statement.executeUpdate(sqlInsertGame);
            statement.executeUpdate(sqlUpdateP1);
            statement.executeUpdate(sqlUpdateP2);
            statement.executeUpdate(sqlDeleteGameIdP1);
            statement.executeUpdate(sqlDeleteGameIdP2);
        }
        catch (SQLException e){
            e.printStackTrace();
        } finally {
            Cleaner.close(statement, null, connection);
        }
    }

    @Test
    public void startTest() {

        boolean result = false;
        String sqlDeleteGame = "DELETE Game WHERE gameId = " + gameId + ";";

        try{
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            setGameId(gameId);
            result = cr.start(player1);
            statement.executeUpdate(sqlDeleteGame);

            assertTrue(result);
        }
        catch (SQLException e){
            e.printStackTrace();
            assertTrue(result);
        } finally {
            Cleaner.close(statement, null, connection);
        }
    }

    @Test
    public void refreshTest() {
        String sqlDeleteGame = "DELETE Game WHERE gameId = " + gameId + ";";
        try{
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();

            String result = cr.getCorrectScene();
            System.out.println(result);
            String expect = "hei";
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