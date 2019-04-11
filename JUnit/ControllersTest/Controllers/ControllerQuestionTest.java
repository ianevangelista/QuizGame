package Controllers;

import org.junit.Test;
import Connection.Cleaner;
import Connection.ConnectionPool;
import static Controllers.ControllerOpponent.setGameId;
import java.sql.ResultSet;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.*;

/*
    JUnit tests for the ControllerQuestion class
 */
public class ControllerQuestionTest {
    ControllerQuestion cq = new ControllerQuestion();
    ControllerHome ch = new ControllerHome();
    Connection connection = null;
    Statement statement = null;
    ResultSet rs = null;

    /*
        Testing method for the findUser method
     */
    @Test
    public void findUserTest() {
        int gameId = 1;
        String username = "helenegj";
        String expAnswer = "player1";
        String sqlGame = "INSERT INTO Game(gameId, player1) VALUES (" + gameId + ", '" + username + "');";
        String sqlDelete = "DELETE FROM Game WHERE gameId=" + gameId + ";";
        ch.setUserName(username);
        setGameId(gameId);
        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();

            //creates game
            statement.executeUpdate(sqlGame);
            String ans = cq.findUser();

            //deletes game
            statement.executeUpdate(sqlDelete);
            assertEquals(expAnswer, ans);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Cleaner.close(statement, null, connection);
        }
    }

    /*
        Test for the questionInfo method
     */
    @Test
    public void questionInfoTest() {
        int question = 26;
        int category = 1;
        int gameId = 1;
        String expAns = "";
        String ans = "";
        String sql = "INSERT INTO Game(gameId, categoryId, question1) " +
                "VALUES (" + gameId + ", "  + category + ", " + question + ");";
        String sqlDelete = "DELETE FROM Game WHERE gameId=" + gameId + ";";
        String sqlQues = "SELECT questionText FROM Question WHERE questionId =" + question + ";";
        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();

            //creates a game with a category and a question
            statement.executeUpdate(sql);
            setGameId(gameId);

            //gets the question from the database and compares it to the method
            rs = statement.executeQuery(sqlQues);
            if(rs.next()) {
                expAns = rs.getString("questionText");
                ans = cq.questionInfo();

                //deletes the game
                statement.executeUpdate(sqlDelete);
                assertEquals(expAns, ans);
            }
        } catch (Exception e){
            e.printStackTrace();
            assertEquals(expAns, ans);
        } finally {
            Cleaner.close(statement, null, connection);
        }
    }

    /*
        Test for the findScore method
     */
    @Test
    public void findScoreTest() {
        int expScore = 0;
        int score = -1;
        int question = 26;
        int category = 1;
        int gameId = 1;
        String answer = "miami heat";
        String sql = "INSERT INTO Game(gameId, categoryId, question1, player1, player2) " +
                "VALUES (" + gameId + ", "  + category + ", " + question + ", 'juni', 'ian');";
        String sqlDelete = "DELETE FROM Game WHERE gameId=" + gameId + ";";
        String sqlQ = "SELECT * FROM Alternative WHERE answerId= 255;";

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();

            //creates game with category, question and players
            statement.executeUpdate(sql);
            setGameId(gameId);
            cq.questionInfo();

            //gets the score for the answer form the database and compares to the method
            rs = statement.executeQuery(sqlQ);
            if(rs.next()) {
                expScore = rs.getInt("score");
                score = cq.findScore(answer);
                statement.executeUpdate(sqlDelete);
                assertEquals(expScore, score);
            }
        } catch (Exception e){
            e.printStackTrace();
            assertEquals(expScore, score);
        } finally {
            Cleaner.close(statement, null, connection);
        }
    }
}