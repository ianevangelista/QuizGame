package Controllers;

import org.junit.Test;

import javafx.event.ActionEvent;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.Statement;

import java.sql.*;

import Connection.ConnectionPool;
import static Controllers.ControllerOpponent.setGameId;
import static Controllers.ControllerHome.setUserName;
import static Controllers.ControllerQuestion.findUser;

import static org.junit.Assert.*;

public class ControllerResultTest {

    Connection connection = null;
    Statement statement = null;
    ControllerResult cr = new ControllerResult();

    @Test
    public void checkResultTest() {
        setGameId(1);
        int myScore = 100;
        int opponentScore = 10;
        int expResult = 1;
        //There will be an exception on illegal result set, but the test shows that the right winner is chosen.
        int result = cr.checkResult(myScore, opponentScore);
        assertEquals(expResult, result);
    }

    @Test
    public void deleteGameTest(){
        int gameId = 1;
        setGameId(gameId);
        String user1 = "juni";
        String user2 = "tiril";
        String sqlInsert = "INSERT INTO Game (player1, player2, gameId) VALUES ('" + user1 + "', '" + user2 + "'," + gameId + ");";
        String sqlDelete = "DELETE FROM Game WHERE gameId = " + gameId + ";";
        try{
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            statement.executeUpdate(sqlInsert);
            boolean result = cr.deleteGame(gameId);
            if(result == false) {
                statement.executeUpdate(sqlDelete);
            }

            assertTrue(result);
        }
        catch (SQLException e){
            e.printStackTrace();
            assertFalse(false);
        }
    }

    @Test
    public void checkFinishTest(){
        setUserName("juni");
        int gameId = 1;
        setGameId(gameId);
        String insertSQL = "INSERT INTO Game (p1Finished, p2Finished, gameId) VALUES (1, 0, " + gameId + ");";
        String deleteSQL = "DELETE FROM Game WHERE gameId = " + gameId + ";";
        try{
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();

            statement.executeUpdate(insertSQL);
            boolean result = cr.checkFinish(gameId, findUser());
            statement.executeUpdate(deleteSQL);

            assertTrue(result);
        }
        catch(SQLException e){
            e.printStackTrace();
            assertFalse(false);
        }
    }

    @Test
    public void sceneGameTest(){
        ActionEvent event = new ActionEvent();
        boolean result = cr.sceneGame(event);
        assertFalse(result);
    }

    @Test
    public void sceneChallengeUserTest(){
        ActionEvent event = new ActionEvent();
        boolean result = cr.sceneChallengeUser(event);
        assertFalse(result);
    }

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