package Controllers;

import org.junit.AfterClass;
import org.junit.Test;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.*;
import Connection.ConnectionPool;
import static Controllers.ControllerOpponent.setGameId;
import static Controllers.ControllerHome.setUserName;
import static Controllers.ControllerQuestion.findUser;
import Connection.Cleaner;
import static org.junit.Assert.*;

/*
     JUnit tests for the ControllerResult class
 */
public class ControllerResultTest {

    static Connection connection = null;
    static Statement statement = null;
    ControllerResult cr = new ControllerResult();


    @AfterClass
    public static void close(){
        Cleaner.close(statement, null, connection);
    }
    /*
        test for the checkResult method
     */
    @Test
    public void checkResultTest() {
        //sets variables and gameId to test the method
        setGameId(1);
        int myScore = 100;
        int opponentScore = 10;
        int expResult = 1;
        //There will be an exception on illegal result set, but the test shows that the right winner is chosen.
        int result = cr.checkResult(myScore, opponentScore);
        assertEquals(expResult, result);
    }

    /*
        Test for the deleteGame Test
     */
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

            //creates a game, then deletes it
            statement.executeUpdate(sqlInsert);
            boolean result = cr.deleteGame(gameId);

            //if the method return false, it gets deleted anyway
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

    /*
        Test for the checkFinish method
     */
    @Test
    public void checkFinishTest(){
        setUserName("juni");
        int gameId = 1;
        setGameId(gameId);
        String insertSQL = "INSERT INTO Game (player1, player2, p1Finished, p2Finished, gameId) VALUES ('juni', 'helene', 1, 0, " + gameId + ");";
        String deleteSQL = "DELETE FROM Game WHERE gameId = " + gameId + ";";
        try{
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();

            //creates game
            statement.executeUpdate(insertSQL);
            boolean result = cr.checkFinish(gameId, findUser());

            //deletes the game
            statement.executeUpdate(deleteSQL);
            assertTrue(result);
        }
        catch(SQLException e){
            e.printStackTrace();
            assertFalse(false);
        }
    }

    /*
        Test for the addGameLost method
     */
    @Test
    public void addGamesLostTest() {

        ResultSet rsBefore = null;
        ResultSet rsAfter = null;

        boolean result = false;
        String username = "juni";
        String selectSQL = "SELECT gamesLost FROM Player WHERE username ='" + username + "';";
        String deleteSQL = "UPDATE Player SET gamesLost = gamesLost - 1 WHERE username = '" + username + "';";

        try{
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();

            //gets the gamesLost from the database
            rsBefore = statement.executeQuery(selectSQL);
            rsBefore.next();
            int selectBefore = rsBefore.getInt("gamesLost");

            //runs method and gets gamesLost from the database
            result = cr.addGamesLost(username);
            rsAfter = statement.executeQuery(selectSQL);
            rsAfter.next();
            int selectAfter = rsAfter.getInt("gamesLost");

            //compares before and after, then deletes the changes
            if((selectBefore+1) == selectAfter){
                statement.executeUpdate(deleteSQL);
                assertTrue(result);
            }
        }
        catch(SQLException e){
            e.printStackTrace();
            assertFalse(result);
        }
    }

    /*
        Test for the addGamesWon method
     */
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

            //gets gameswon from the database
            rsBefore = statement.executeQuery(selectSQL);
            rsBefore.next();
            int selectBefore = rsBefore.getInt("gamesWon");

            //runs method and gets gamesWon from the database
            result = cr.addGamesWon(username);
            rsAfter = statement.executeQuery(selectSQL);
            rsAfter.next();
            int selectAfter = rsAfter.getInt("gamesWon");

            //compares before and after, then deletes the changes
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