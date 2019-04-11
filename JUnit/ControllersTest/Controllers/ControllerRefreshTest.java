package Controllers;

import Connection.ConnectionPool;
import javafx.event.ActionEvent;
import org.junit.*;
import Connection.Cleaner;
import static Controllers.ControllerOpponent.setGameId;
import static Controllers.ControllerHome.setUserName;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import static org.junit.Assert.*;

/*
     JUnit tests for the ControllerRefresh class
 */
public class ControllerRefreshTest {

    static Connection connection = null;
    static Statement statement = null;
    static ControllerRefresh cr = new ControllerRefresh();
    static public String player1 = "juni";
    static public String player2 = "ian";
    static public int gameId = 1;

    /*
        creates a game with players before the tests
     */
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

    /*
        deletes the game and removes gameId from players after the tests
     */
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

    /*
        Test for the start method
     */
    @Test
    public void startTest() {
        //sets gameId and username
        setGameId(gameId);
        setUserName(player1);
        boolean result = cr.start();

        assertTrue(result);
    }

    /*
        Test for the refresh method
     */
    @Test
    public void refreshTest() {
        //sets username and test that the method return the correct scenename
        setUserName(player1);
        String result = cr.getCorrectScene();
        String expect = "/Scenes/Wait.fxml";
        assertEquals(expect, result);
    }

    /*
        Test for the decline method
     */
    @Test
    public void declineTest() {
        //sets username and creates a ActionEvent to use the method
        ActionEvent event = new ActionEvent();
        setUserName(player2);
        boolean result = cr.decline(event);
        assertTrue(result);
    }
}