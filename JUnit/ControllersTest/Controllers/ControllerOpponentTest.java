/*package Controllers;

import Connection.ConnectionPool;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static Controllers.ControllerHome.setUserName;
import static Controllers.ControllerOpponent.getGameId;


import static Controllers.ControllerOpponent.setGameId;
import static org.junit.Assert.*;

public class ControllerOpponentTest {

    ControllerOpponent co = new ControllerOpponent();
    Connection connection = null;
    Statement statement = null;

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
    }

    @Test
    public void setGameIdTest() {
        /*ResultSet rs = null;
        int gameid = 1;
        int expected = 2;
        String sqlInsert = "INSERT INTO Game (gameId) VALUES (" + gameid + ");";
        String sqlSelect = "SELECT gameId FROM Game WHERE gameId = " + expected + ";";
        String sqlDelete = "DELETE Game WHERE gameId = " + gameid + ";";

        try{
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            statement.executeUpdate(sqlInsert);

            setGameId(expected);

            rs = statement.executeQuery(sqlSelect);
            int result = rs.getInt("gameId");

            assertEquals(expected, result);

        }
        catch (SQLException e){
            e.printStackTrace();

        setGameId(1);
        int result = getGameId();
    }

    @Test
    public void getGameId() {
    }

    @Test
    public void chooseOnlineUser() {
    }

    @Test
    public void sceneCategory() {
    }
}*/