package Controllers;


import Connection.Cleaner;
import Connection.ConnectionPool;
import java.sql.ResultSet;
import javafx.event.ActionEvent;
import org.junit.Test;

import java.sql.Connection;
import java.sql.Statement;

import static org.junit.Assert.assertTrue;

import static Controllers.ControllerOpponent.setGameId;

/*
    JUnit tests for ControllerCategory class
 */
public class ControllerCategoryTest {

    ControllerCategory cc = new ControllerCategory();
    Connection connection = null;
    Statement statement = null;
    ResultSet rs = null;

    /*
        Test for the method chooseCategory1
     */
    @Test
    public void chooseCategory1Test() {
        //creates ActionEvent and runs the initializing method
        ActionEvent event = new ActionEvent();
        cc.initialize();
        boolean ans = cc.chooseCategory1(event);
        assertTrue(ans);
    }

    /*
        Test for the method chooseCategory2
     */
    @Test
    public void chooseCategory2Test() {
        //creates ActionEvent and runs the initializing method
        ActionEvent event = new ActionEvent();
        cc.initialize();
        boolean ans = cc.chooseCategory2(event);
        assertTrue(ans);
    }

    /*
        Test for the method chooseCategory1
     */
    @Test
    public void chooseCategory3Test() {
        //creates ActionEvent and runs the initializing method
        ActionEvent event = new ActionEvent();
        cc.initialize();
        boolean ans = cc.chooseCategory3(event);
        assertTrue(ans);
    }

    /*
        Test for updateCategory method
     */
    @Test
    public void updateCategoryTest() {
        int category = 1;
        int gameId = 1;
        boolean resultat = false;
        String sql = "INSERT INTO Game(gameId, player1, player2) VALUES (" + gameId + ", 'juni', 'ian');";
        String sqlCategory = "SELECT categoryId FROM Game WHERE gameId=" + gameId + ";";
        String sqlDelete = "DELETE FROM Game WHERE gameId=" + gameId + ";";
        try {
            //creates game with gameId 1
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            statement.executeUpdate(sql);
            // Adds questions from the category to the game
            resultat = cc.updateCategory(category, gameId);

            //checks if category got updated and deletes game
            rs = statement.executeQuery(sqlCategory);
            if(rs.next()) {
                if((rs.getInt(1) ==category)) {
                    statement.executeUpdate(sqlDelete);
                    assertTrue(resultat);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            assertTrue(resultat);
        } finally {
            Cleaner.close(statement, null, connection);
        }
    }

    /*
        Test for initialize method
     */
    @Test
    public void initialize() {
        //tests for gameid 1
        setGameId(1);
        boolean ans = cc.initialize();
        assertTrue(ans);
    }
}
