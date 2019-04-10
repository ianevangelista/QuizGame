package Controllers;

import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import Connection.ConnectionPool;

import static org.junit.Assert.*;

public class TimerCTest {
    TimerC timerc;
    Connection connection;
    Statement statement;
    ResultSet rs;

    @Before
    public void setUp() throws Exception {
        connection = null;
        statement = null;
        rs = null;
        timerc = new TimerC();
    }

    @Test
    public void initialize() {
        boolean ans = timerc.initialize();
        assertTrue(ans);
    }

    @Test
    public void checkCat() {
        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            int gameId = 1; //depends on gameId
            String sqlInsert = "INSERT INTO Game (gameId, question1, question2, question3) VALUES (1, 1, 2, 3);";
            statement.executeUpdate(sqlInsert);
            boolean result = timerc.checkCat(gameId);
            String sqlDelete = "DELETE FROM Game WHERE gameId=" + gameId + ";";
            statement.executeUpdate(sqlDelete);
            assertTrue(result);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void checkGameId() {
        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            String username = "ian"; //depends if the user has a game and is player 1
            String sqlInsert = "INSERT INTO Game (gameId, player1) VALUES (1, username);";
            statement.executeUpdate(sqlInsert);
            boolean result = timerc.checkGameId(username);
            String sqlDelete = "DELETE FROM Game WHERE gameId=" + 1 + ";";
            statement.executeUpdate(sqlDelete);
            assertFalse(result);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}