package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import Connection.Cleaner;
import Connection.ConnectionClass;

import java.util.*;

public class ControllerQuestion {
    private int questionCount = 0;
    public ChangeScene sceneChanger = new ChangeScene();
    int gameId;
    String username;

    @FXML
    public TextField answerField;
    public TextField questionField;

    public void sceneQuestion(ActionEvent event) { //clicks submitbutton
        String sceneNavn;
        boolean riktig = questionCheck(gameId, username);   //checks answer

        if(riktig) sceneNavn = "CorrectAnswer.fxml";
        else sceneNavn = "IncorrectAnswer.fxml";
        if(questionCount == 3) sceneNavn = "result.fxml";
        sceneChanger.change(event, sceneNavn);              //changes scene
    }

    public void setGameId(int newGameId) {
        gameId = newGameId;
    }

    public void setUsername(String newUsername) {
        username = newUsername;
    }

    public void questionDisplay(int gameId, String username) { //displays questions
        Connection connection = null;
        Statement statement = null;
        Cleaner cleaner = new Cleaner();
        try {
            ConnectionClass connectionClass = new ConnectionClass();
            connection = connectionClass.getConnection();
            statement = connection.createStatement();

            String sqlGetText = "SELECT questionText FROM Game JOIN Question ON questionId = question"; //sqlstatement for question text

            if(questionCount == 0) {                        //displays question one
                ResultSet rsQuestionOneText = statement.executeQuery(sqlGetText + "1;");        //
                rsQuestionOneText.next();
                String qOneText = rsQuestionOneText.getString("questionText");
                questionField.setText(qOneText);                         //printer ut spørsmålet

            }
            else if(questionCount == 1) {                   //displays question two
                ResultSet rsQuestionTwoText = statement.executeQuery(sqlGetText + "2;");
                rsQuestionTwoText.next();
                String qTwoText = rsQuestionTwoText.getString("questionText");
                questionField.setText(qTwoText);
            }
            else if(questionCount == 2) {                   //displays question three
                ResultSet rsQuestionThreeText = statement.executeQuery(sqlGetText + "3;");
                rsQuestionThreeText.next();
                String qThreeText = rsQuestionThreeText.getString("questionText");
                questionField.setText(qThreeText);
            }
            questionCount++;


        }catch (SQLException e) {
            e.printStackTrace();
        }finally {
            cleaner.close(statement, null, connection);
        }
    }

    public boolean questionCheck(int gameId, String username) {
        Connection connection = null;
        Statement statement = null;
        Cleaner cleaner = new Cleaner();
        boolean riktig = false;

        String sqlGetAlt = "SELECT answer FROM Alternative WHERE questionId=";
        String sqlGetQId = "FROM Game WHERE gameId=" + gameId + ";";
        try {
            ConnectionClass connectionClass = new ConnectionClass();
            connection = connectionClass.getConnection();
            statement = connection.createStatement();

            String user = findUser(gameId, username);
            int QId;
            String answer = answerField.getText();
            if(questionCount == 0) {
                ResultSet rsQId1 = statement.executeQuery("SELECT question1 " + sqlGetQId);
                rsQId1.next();
                 QId = rsQId1.getInt("question1");
            }
            else if(questionCount == 1) {
                ResultSet rsQId2 = statement.executeQuery("SELECT question2 " + sqlGetQId);
                rsQId2.next();
                 QId = rsQId2.getInt("question2");
            }
            else{
                ResultSet rsQId3 = statement.executeQuery("SELECT question3 " + sqlGetQId);
                rsQId3.next();
                 QId = rsQId3.getInt("question3");
            }

            ResultSet rsAlterative = statement.executeQuery(sqlGetAlt + QId + ";");
            rsAlterative.next();
            ArrayList <String> alternative = new ArrayList<>();
            while(rsAlterative.next()) {
                alternative.add(new String(rsAlterative.getString("answer")));
            }
            String sqlGetScore = "SELECT score FROM Alternative WHERE questionId=" + QId + " AND answer=";

            ResultSet rsScore = null;
            for(String a:alternative) {
                if(a.equals(answer)) {
                    rsScore = statement.executeQuery(sqlGetScore + a + ";");
                    riktig = true;
                    break;
                }
            }
            int score = 0;
            if(rsScore.next()){ score = rsScore.getInt("score"); }

            String points = "";
            if(user.equals("player1")) { points = "p1Points"; }
            else if(user.equals("player2")) {points = "p2Points";}

            String sqlUpdate = "UPDATE Game SET " + points + " = " + points + " + " + score + " WHERE gameId=" + gameId + ";";
            statement.execute(sqlUpdate);
            return riktig;

        }catch (SQLException e) {
            e.printStackTrace();
            return riktig;
        }finally {
            cleaner.close(statement, null, connection);
        }
    }

    private String findUser(int gameId, String username) {
        Connection connection = null;
        Statement statement = null;
        Cleaner cleaner = new Cleaner();

        String sqlP1 = "SELECT player1 FROM Game WHERE gameId=" + gameId + ";";
        String sqlP2 = "SELECT player2 FROM Game WHERE gameId =" + gameId + ";";
        try {
            ConnectionClass connectionClass = new ConnectionClass();
            connection = connectionClass.getConnection();
            statement = connection.createStatement();

            ResultSet rsPlayer1 = statement.executeQuery(sqlP1);
            rsPlayer1.next();
            String player1 = rsPlayer1.getString("player1");
            if(username.equals(player1)){return "player1";}

            ResultSet rsPlayer2 = statement.executeQuery(sqlP2);
            rsPlayer2.next();
            String player2 = rsPlayer2.getString("player2");
            if(username.equals(player2)) {return "player2";}
            return null;
        }catch (SQLException e) {
            e.printStackTrace();
            return "ex";
        }finally {
            cleaner.close(statement, null, connection);
        }
    }
}
