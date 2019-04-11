package Controllers;

import org.junit.Before;
import org.junit.Test;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import Connection.ConnectionPool;
import Connection.Cleaner;
import static org.junit.Assert.*;
import org.junit.*;


/*
     JUnit tests for the TimerC class
 */
public class TimerCTest {
    TimerC timerc;
    Connection connection;
    Statement statement;
    ResultSet rs;

    /*
        sets up the connections
     */
    @Before
    public void setUp() throws Exception {
        connection = null;
        statement = null;
        rs = null;
        timerc = new TimerC();
    }

    /*
        closes the connections after all the tests
     */
    @AfterClass
    public void closing() {
        Cleaner.close(statement, rs, connection);
    }

    /*
        Test for the initialize method
     */
    @Test
    public void initializeTest() {
        boolean ans = timerc.initialize();
        assertTrue(ans);
    }

    /*
        Test for the checkCat method
     */
    @Test
    public void checkCatTest() {
        int gameId = 1;
        String sqlInsert = "INSERT INTO Game (gameId, question1, question2, question3) VALUES (1, 1, 2, 3);";
        String sqlDelete = "DELETE FROM Game WHERE gameId=" + gameId + ";";
        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            //creates a game
            statement.executeUpdate(sqlInsert);

            boolean result = timerc.checkCat(gameId);

            //deletes the game
            statement.executeUpdate(sqlDelete);

            assertTrue(result);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*
        Test for the checkGameId method
     */
    @Test
    public void checkGameIdTest() {
        String username = "ian"; //depends if the user has a game and is player 1
        String sqlInsert = "INSERT INTO Game (gameId, player1) VALUES (1, username);";
        String sqlDelete = "DELETE FROM Game WHERE gameId=" + 1 + ";";

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();

            //creates a game
            statement.executeUpdate(sqlInsert);

            boolean result = timerc.checkGameId(username);

            //deleted the game
            statement.executeUpdate(sqlDelete);
            assertFalse(result);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}