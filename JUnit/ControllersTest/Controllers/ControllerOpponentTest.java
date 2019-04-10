package Controllers;

import Connection.ConnectionPool;
import Connection.Cleaner;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static Controllers.ControllerHome.setUserName;
import static Controllers.ControllerHome.getUserName;
import static Controllers.ControllerOpponent.setGameId;
import static Controllers.ControllerOpponent.getGameId;
import static org.junit.Assert.*;

/*
    JUnit tests for ControllerOpponent class
 */
public class ControllerOpponentTest {

    ControllerOpponent co = new ControllerOpponent();
    Connection connection = null;
    Statement statement = null;

    /*
        Test for checkOpponent method
     */
    @Test
    public void checkOpponentTest() {
        setUserName("juni");
        String opponent = "ian";
        String sqlUpdate1 = "UPDATE Player SET online = 1 WHERE username = '" + opponent + "';";
        String sqlUpdate2 = "UPDATE Player SET online = 0 WHERE username = '" + opponent + "';";

        try{
            //sets the opponent to online
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            statement.executeUpdate(sqlUpdate1);
            //An error will appear here because it enters makeGame
            int result =  co.checkOpponent(opponent);
            int expectedResult = 1;
            //sets opponent offline
            statement.executeUpdate(sqlUpdate2);
            assertEquals(expectedResult, result);
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            Cleaner.close(statement, null, connection);
        }
    }

    /*
        Testing the makeGame method
     */
    @Test
    public void makeGameTest(){
        String player1 = "juni";
        String player2 = "ian";
        String sqlUpdate1 = "UPDATE Player SET gameId = NULL WHERE username = '" + player1 + "';";
        String sqlUpdate2 = "UPDATE Player SET gameId = NULL WHERE username = '" + player2 + "';";
        String sqlDelete = "DELETE FROM Game WHERE player1 = '" + player1 + "';";

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            //makes a game
            boolean result = co.makeGame(player1, player2);

            //deletes the game
            statement.executeUpdate(sqlUpdate1);
            statement.executeUpdate(sqlUpdate2);
            statement.executeUpdate(sqlDelete);
            assertTrue(result);
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            Cleaner.close(statement, null, connection);
        }
    }

    /*
        Testing for the alreadyChallenged method
     */
    @Test
    public void alreadyChallengedTest(){
        String username = "juni";
        int expected = 1;
        String sqlInsert = "INSERT INTO Game (gameId, player1, player2) VALUES (1, 'juni', 'ian');";
        String sqlUpdate = "UPDATE Player SET gameId = 1 WHERE username = '" + username + "';";
        String sqlUpdateAgain = "UPDATE Player SET gameId = NULL WHERE username = '" + username + "';";
        String sqlDelete = "DELETE FROM Game WHERE gameId = 1";
        try{
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();

            //makes a game and updates player with gameId
            statement.executeUpdate(sqlInsert);
            statement.executeUpdate(sqlUpdate);
            int result = co.alreadyChallenged(username);

            //deletes game and players gameId
            statement.executeUpdate(sqlUpdateAgain);
            statement.executeUpdate(sqlDelete);

            assertEquals(expected, result);
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            Cleaner.close(statement, null, connection);
        }
    }

    /*
        Testing for the set and get methods for gameId
     */
    @Test
    public void gameIdTest() {
        int expected = 1;
        setGameId(expected);
        int result = getGameId();

        assertEquals(expected, result);

    }

    /*
        Testing for the checkGameId
     */
    @Test
    public void checkGameIdTest(){
        String username = setUserName("juni");
        String sqlInsert = "INSERT INTO Game (gameId, player1, player2) VALUES (1, 'juni', 'ian');";
        String sqlUpdate1 = "UPDATE Player SET gameId = 1 WHERE username = '" + username + "';";
        String sqlUpdate2 = "UPDATE Player SET gameId = null WHERE username = '" + username + "';";
        String sqlDelete = "DELETE FROM Game WHERE gameId = 1";

        try{
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();

            //makes a game and updates the players gameId
            statement.executeUpdate(sqlInsert);
            statement.executeUpdate(sqlUpdate1);

            boolean result = co.checkGameId(getUserName());

            //deletes game and players gameId
            statement.executeUpdate(sqlUpdate2);
            statement.executeUpdate(sqlDelete);

            assertTrue(result);
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            Cleaner.close(statement, null, connection);
        }
    }
}