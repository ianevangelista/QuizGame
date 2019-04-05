package Controllers;

import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import Connection.ConnectionPool;

import static org.junit.Assert.*;

public class ControllerResultTest {

    Connection connection = null;
    Statement statement = null;
    ControllerResult cr;

    @Before
    public void setUp() {
        cr = new ControllerResult();
    }

    @Test
    public void checkResultTest() {
        int myScore = 100;
        int opponentScore = 10;
        int expResult = 1;
        int result = cr.checkResult(myScore, opponentScore);
        assertEquals(expResult, result);
    }

    /*@Test
    public void initialize() {
    }

    @Test
    public void timerRes() {
    }

    @Test
    public void checkRes() {
    }

    @Test
    public void turnOfTimerR() {
    }

    @Test
    public void addGamesLost() {
    }*/

    @Test
    public void addGamesWonTest() {
        ResultSet rsBefore = null;
        ResultSet rsAfter = null;

        String username = "juni";
        String selectSQL = "SELECT gamesWon FROM Player WHERE username ='" + username + "';";
        String deleteSQL = "UPDATE Player SET gamesWon= gamesWon - 1 WHERE username = '" + username + "';";

        try{
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();

            rsBefore = statement.executeQuery(selectSQL);
            int selectBefore = rsBefore.getInt("gamesWon");
            boolean result = cr.addGamesWon(username);
            rsAfter = statement.executeQuery(selectSQL);
            int selectAfter = rsAfter.getInt("gamesWon");

            if((selectBefore + 1) == selectAfter){
                statement.executeUpdate(deleteSQL);
                assertTrue(result);
            }
        }
        catch(SQLException e){
            e.printStackTrace();
            assertTrue(false);
        }

    }
}