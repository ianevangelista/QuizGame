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

public class ControllerQuestionTest {
    ControllerQuestion cq = new ControllerQuestion();
    ControllerHome ch = new ControllerHome();
    Connection connection = null;
    Statement statement = null;
    ResultSet rs = null;


    @Test
    public void findUserTest() {
        int gameId = 1;
        String username = "helenegj";
        String expAnswer = "player1";
        String ans = "";
        String sqlGame = "INSERT INTO Game(gameId, player1) VALUES (" + gameId + ", '" + username + "');";
        String sqlDelete = "DELETE FROM Game WHERE gameId=" + gameId + ";";
        ch.setUserName(username);
        setGameId(gameId);
        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            statement.executeUpdate(sqlGame);
            ans = cq.findUser();
            statement.executeUpdate(sqlDelete);
            assertEquals(expAnswer, ans);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Cleaner.close(statement, null, connection);
        }
    }

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
            statement.executeUpdate(sql);
            setGameId(gameId);
            rs = statement.executeQuery(sqlQues);
            if(rs.next()) {
                expAns = rs.getString("questionText");
                ans = cq.questionInfo();
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

    @Test
    public void findScoreTest() {
        int expScore = 0;
        int score = -1;
        int question = 26;
        int category = 1;
        int gameId = 1;
        String answer = "miami heat";
        String sql = "INSERT INTO Game(gameId, categoryId, question1) " +
                "VALUES (" + gameId + ", "  + category + ", " + question + ");";
        String sqlDelete = "DELETE FROM Game WHERE gameId=" + gameId + ";";
        String sqlQ = "SELECT * FROM Alternative WHERE answerId= 255;";

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            statement.executeUpdate(sql);
            setGameId(gameId);
            cq.questionInfo();
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