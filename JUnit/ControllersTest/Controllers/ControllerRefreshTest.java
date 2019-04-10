package Controllers;

import Connection.ConnectionPool;
import javafx.event.ActionEvent;
import org.junit.*;
import Connection.Cleaner;
import static Controllers.ControllerOpponent.setGameId;
import static Controllers.ControllerHome.setUserName;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.*;

public class ControllerRefreshTest {

    static Connection connection = null;
    static Statement statement = null;
    static ControllerRefresh cr = new ControllerRefresh();
    static public String player1 = "juni";
    static public String player2 = "ian";
    static public int gameId = 1;


    @BeforeClass
    public static void createGame() {
        String sqlInsertGame = "INSERT INTO Game (gameId, player1, player2) VALUES (" + gameId + ", '" + player1 + "', '" + player2 + "');";
        String sqlUpdateP1 = "UPDATE Player SET gameId = " + gameId + " WHERE username = '" + player1 + "';";
        String sqlUpdateP2 = "UPDATE Player SET gameId = " + gameId + " WHERE username = '" + player2 + "';";
        try{
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            setGameId(gameId);
            statement.executeUpdate(sqlInsertGame);
            statement.executeUpdate(sqlUpdateP1);
            statement.executeUpdate(sqlUpdateP2);
        }
        catch (SQLException e){
            e.printStackTrace();
        } finally {
            Cleaner.close(statement, null, connection);
        }
    }

    @AfterClass
    public static void deleteGame() {
        String sqlDeleteGameIdP1 = "UPDATE Player SET gameId = NULL WHERE username = '" + player1 + "';";
        String sqlDeleteGameIdP2 = "UPDATE Player SET gameId = NULL WHERE username = '" + player2 + "';";
        String sqlDeleteGame = "DELETE FROM Game WHERE gameId = " + gameId + ";";
        try{
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            statement.executeUpdate(sqlDeleteGameIdP1);
            statement.executeUpdate(sqlDeleteGameIdP2);
            statement.executeUpdate(sqlDeleteGame);
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
            setUserName(player1);
            result = cr.start();

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
        String result = "result";
        String expect = "expected";
        String sqlDeleteGame = "DELETE Game WHERE gameId = " + gameId + ";";
        try{
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            setUserName(player1);
            result = cr.getCorrectScene();
            expect = "/Scenes/Wait.fxml";
            assertEquals(expect, result);
        }
        catch (SQLException e){
            e.printStackTrace();
            assertEquals(expect, result);
        } finally {
            Cleaner.close(statement, null, connection);
        }
    }

    @Test
    public void decline() {
        ActionEvent event = new ActionEvent();
        ResultSet rs = null;
        boolean result = false;
        String sqlSelect = "SELECT player1, player2 FROM Game WHERE gameId = " + gameId + ";";
        setUserName(player2);
        try{
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            result = cr.decline(event);
            assertTrue(result);
        }
        catch (SQLException e){
            e.printStackTrace();
            assertTrue(result);
        }
    }
}