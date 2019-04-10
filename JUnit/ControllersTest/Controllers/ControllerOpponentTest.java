package Controllers;

import Connection.ConnectionPool;
import Connection.Cleaner;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static Controllers.ControllerHome.setUserName;


import static Controllers.ControllerOpponent.setGameId;
import static Controllers.ControllerOpponent.getGameId;
import static Controllers.ControllerHome.getUserName;
import static org.junit.Assert.*;

public class ControllerOpponentTest {

    ControllerOpponent co = new ControllerOpponent();
    Connection connection = null;
    Statement statement = null;
    //int gameId = getGameId();

    @Test
    public void checkOpponentTest() {
        setUserName("juni");
        String opponent = "ian";
        String sqlUpdate1 = "UPDATE Player SET online = 1 WHERE username = '" + opponent + "';";
        String sqlUpdate2 = "UPDATE Player SET online = 0 WHERE username = '" + opponent + "';";

        try{
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            statement.executeUpdate(sqlUpdate1);
            int result =  co.checkOpponent(opponent);
            int expectedResult = 1;
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

            boolean result = co.makeGame(player1, player2);

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

    @Test
    public void alreadyChallengedTest(){
        String username = "juni";
        int expected = 1;
        String sqlInsert = "INSERT INTO Game (gameId) VALUES (1);";
        String sqlUpdate = "UPDATE Player SET gameId = 1 WHERE username = '" + username + "';";
        String sqlUpdateAgain = "UPDATE Player SET gameId = NULL WHERE username = '" + username + "';";
        String sqlDelete = "DELETE FROM Game WHERE gameId = 1";
        try{
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();

            statement.executeUpdate(sqlInsert);
            statement.executeUpdate(sqlUpdate);
            int result = co.alreadyChallenged(username);
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

    @Test
    public void setGameIdTest() {
        int expected = 1;
        setGameId(expected);
        int result = getGameId();

        assertEquals(expected, result);

    }

    @Test
    public void checkGameIdTest(){
        String username = setUserName("juni");
        String sqlUpdate1 = "UPDATE Player SET gameId = 1 WHERE username = '" + username + "';";
        String sqlUpdate2 = "UPDATE Player SET gameId = null WHERE username = '" + username + "';";

        try{
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();

            statement.executeUpdate(sqlUpdate1);
            boolean result = co.checkGameId(getUserName());
            statement.executeUpdate(sqlUpdate2);

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