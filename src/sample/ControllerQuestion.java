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

    public void setGameId(int newGameId) {      //set GameId
        gameId = newGameId;
    }

    public void setUsername(String newUsername) {       //set username
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

            //displays question one
            if(questionCount == 0) {
                ResultSet rsQuestionOneText = statement.executeQuery(sqlGetText + "1;");        //
                rsQuestionOneText.next();
                String qOneText = rsQuestionOneText.getString("questionText");
                questionField.setText(qOneText);                         //printer ut spørsmålet

            }
            //displays question two
            else if(questionCount == 1) {
                ResultSet rsQuestionTwoText = statement.executeQuery(sqlGetText + "2;");
                rsQuestionTwoText.next();
                String qTwoText = rsQuestionTwoText.getString("questionText");
                questionField.setText(qTwoText);
            }
            //displays question three
            else if(questionCount == 2) {
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

            String user = findUser(gameId, username);       //find user
            int QId;
            String answer = answerField.getText();          //get answwer

            //checks q1
            if(questionCount == 0) {
                ResultSet rsQId1 = statement.executeQuery("SELECT question1 " + sqlGetQId);
                rsQId1.next();
                 QId = rsQId1.getInt("question1");
            }
            //checks q2
            else if(questionCount == 1) {
                ResultSet rsQId2 = statement.executeQuery("SELECT question2 " + sqlGetQId);
                rsQId2.next();
                 QId = rsQId2.getInt("question2");
            }
            //checks q3
            else{
                ResultSet rsQId3 = statement.executeQuery("SELECT question3 " + sqlGetQId);
                rsQId3.next();
                 QId = rsQId3.getInt("question3");
            }

            //goes through all the alternatives to the question
            ResultSet rsAlterative = statement.executeQuery(sqlGetAlt + QId + ";");
            rsAlterative.next();
            ArrayList <String> alternative = new ArrayList<>();
            while(rsAlterative.next()) {
                alternative.add(new String(rsAlterative.getString("answer")));
            }
            String sqlGetScore = "SELECT score FROM Alternative WHERE questionId=" + QId + " AND answer=";

            //checks if the answer equals any of the alternatives and in that case saves the score
            ResultSet rsScore = null;
            for(String a:alternative) {
                if(a.equals(answer)) {
                    rsScore = statement.executeQuery(sqlGetScore + a + ";");
                    riktig = true;
                    break;
                }
            }
            int score = 0;
            //prints score to scorevariable
            if(rsScore.next()){ score = rsScore.getInt("score"); }

            //variable for sql
            String points = "";
            if(user.equals("player1")) { points = "p1Points"; }
            else if(user.equals("player2")) {points = "p2Points";}

            //updates score in database
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

        //finds player1 & 2
        String sqlP1 = "SELECT player1 FROM Game WHERE gameId=" + gameId + ";";
        String sqlP2 = "SELECT player2 FROM Game WHERE gameId =" + gameId + ";";
        try {
            ConnectionClass connectionClass = new ConnectionClass();
            connection = connectionClass.getConnection();
            statement = connection.createStatement();

            //check if player1 equals username
            ResultSet rsPlayer1 = statement.executeQuery(sqlP1);
            rsPlayer1.next();
            String player1 = rsPlayer1.getString("player1");
            if(username.equals(player1)){return "player1";}

            //check if player2 equals username
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
