package Controllers;

import org.junit.Test;
import Connection.Cleaner;
import Connection.ConnectionPool;
import java.sql.ResultSet;
import javafx.event.ActionEvent;
import org.junit.Test;

import java.sql.Connection;
import java.sql.Statement;

import static org.junit.Assert.assertTrue;

import static org.junit.Assert.*;

public class ControllerQuestionTest {
    ControllerQuestion cq = new ControllerQuestion();
    Connection connection = null;
    Statement statement = null;
    ResultSet rs = null;


    @Test
    public void nextQuestion() {
    }

    @Test
    public void confirmAnswer() {
    }

    @Test
    public void findUser() {
    }

    @Test
    public void findScoreTest() {
        int category = 1;
        int gameId = 1;
        int question = 26;
        String answer = "miami heat";
        String sql = "INSERT INTO Game(gameId) VALUES (" + gameId + ");";
        String sqlQ = "SELECT * FROM Alternative WHERE answerId= 255;";
        String sqlDelete = "DELETE FROM Game WHERE gameId=" + gameId + ";";
        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            statement.executeUpdate(sql);
            // Adds questions from the category to the game
            /*
            rs = statement.executeQuery(sqlCategory);
            if(rs.next()) {
                if((rs.getInt(1) ==category)) {
                    statement.executeUpdate(sqlDelete);
                    assertTrue(resultat);
                }
            }*/
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            Cleaner.close(statement, null, connection);
        }
    }

    @Test
    public void timerCountdown() {
    }
}