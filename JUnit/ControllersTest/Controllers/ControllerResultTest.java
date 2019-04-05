package Controllers;

import org.junit.Test;

import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.Statement;

import java.sql.*;

import Connection.ConnectionPool;

import static org.junit.Assert.*;

public class ControllerResultTest {

    Connection connection = null;
    Statement statement = null;
    ControllerResult cr = new ControllerResult();

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
    }*/

    @Test
    public void checkFinishTest(){
        int gameId = 2;
        String insertSQL = "INSERT INTO Game (p1Finished, p2Finished, gameId) VALUES (1, 1, " + gameId + ");";
        String deleteSQL = "DELETE FROM Game WHERE gameId = " + gameId + ";";
        try{
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();

            statement.executeUpdate(insertSQL);
            boolean result = cr.checkFinish(gameId);
            statement.executeUpdate(deleteSQL);

            assertTrue(result);
        }
        catch(SQLException e){
            e.printStackTrace();
            assertFalse(false);
        }
    }

    /*@Test
    public void turnOfTimerR() {
    }*/

    @Test
    public void addGamesLost() {

        ResultSet rsBefore = null;
        ResultSet rsAfter = null;

        boolean result = false;
        String username = "juni";
        String selectSQL = "SELECT gamesLost FROM Player WHERE username ='" + username + "';";
        String deleteSQL = "UPDATE Player SET gamesWon= gamesWon + 1 WHERE username = '" + username + "';";

        try{
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();

            rsBefore = statement.executeQuery(selectSQL);
            rsBefore.next();
            int selectBefore = rsBefore.getInt("gamesLost");
            result = cr.addGamesLost(username);
            rsAfter = statement.executeQuery(selectSQL);
            rsAfter.next();
            int selectAfter = rsAfter.getInt("gamesLost");

            if((selectBefore - 1) == selectAfter){
                statement.executeUpdate(deleteSQL);
                assertTrue(result);
            }
        }
        catch(SQLException e){
            e.printStackTrace();
            assertFalse(result);
        }
    }

    @Test
    public void addGamesWonTest() {

        ResultSet rsBefore = null;
        ResultSet rsAfter = null;

        boolean result = false;
        String username = "juni";
        String selectSQL = "SELECT gamesWon FROM Player WHERE username ='" + username + "';";
        String deleteSQL = "UPDATE Player SET gamesWon= gamesWon - 1 WHERE username = '" + username + "';";

        try{
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();

            rsBefore = statement.executeQuery(selectSQL);
            rsBefore.next();
            int selectBefore = rsBefore.getInt("gamesWon");
            result = cr.addGamesWon(username);
            rsAfter = statement.executeQuery(selectSQL);
            rsAfter.next();
            int selectAfter = rsAfter.getInt("gamesWon");

            if((selectBefore + 1) == selectAfter){
                statement.executeUpdate(deleteSQL);
                assertTrue(result);
            }
        }
        catch(SQLException e){
            e.printStackTrace();
            assertFalse(result);
        }

    }
}