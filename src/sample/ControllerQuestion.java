package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import static sample.ChooseOpponent.getGameId;
import static sample.ControllerHome.getUserName;
import static sample.TimerC.timerRes;

import Connection.Cleaner;
import Connection.ConnectionPool;

import java.util.*;

public class ControllerQuestion {
    private int questionCount = 0;
    private static int gameId = getGameId();
    private static String username = getUserName();
    private Connection connection = null;
    private Statement statement = null;

    @FXML
    public TextField answerField;
    public Label questionField;

    public void sceneHome(ActionEvent event) { //feedback knapp
        ChangeScene.change(event, "Game.fxml"); //bruker super-metode
    }

    public void confirmAnswer(ActionEvent event) { //clicks submit button
        String sceneNavn;
        boolean riktig = questionCheck(gameId);   //checks answer

        //checks if all the quesitons have been displayed
        if(questionCount == 3) {
            questionCount = 0;
            try{
                connection = ConnectionPool.getConnection();
                statement = connection.createStatement();

                String player = findUser();
                String finished = player.equals("player1") ? "p1Finished" : "p2Finished";

                //puts the finished variable true
                String sqlUpdate = "UPDATE Game SET " + finished + "=1 WHERE gameId=" + gameId + ";";
                statement.executeUpdate(sqlUpdate);

                //starts timer
                timerRes(event);
            }catch(SQLException e) {
                e.printStackTrace();
            }finally {
                Cleaner.close(statement, null,connection);
            }
            //sett p1/p2finish == true
            sceneNavn = "result.fxml";
        }
        else{
            if(riktig) sceneNavn = "CorrectAnswer.fxml";
            else sceneNavn = "IncorrectAnswer.fxml";
        }
        //changes scene
        ChangeScene.change(event, sceneNavn);
    }

    public void questionDisplay() { //displays questions
        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();

            //sql to get question text
            String sqlGetText = "SELECT questionText FROM Game JOIN Question ON questionId = question";
            ResultSet rsQuestionText = statement.executeQuery(sqlGetText + (questionCount+1) + ";");
            rsQuestionText.next();
            String qText = rsQuestionText.getString("questionText");

            //displays question
            questionField.setText(qText);
            questionCount++;

        }catch (SQLException e) {
            e.printStackTrace();
        }finally {
            Cleaner.close(statement, null, connection);
        }
    }

    public boolean questionCheck(int gameId) {
        boolean riktig = false;
        ResultSet rs = null;

        String[] sqlQuestionName = {"question1", "question2", "question3"};
        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();

            String user = findUser();       //find user
            String answer = answerField.getText().toLowerCase();  //get answer in lowercase

            //gets the answered questionid
            String sqlGetQId = " FROM Game WHERE gameId=" + gameId + ";";
            rs = statement.executeQuery("SELECT " + sqlQuestionName[questionCount-1] + sqlGetQId);
            rs.next();
            int QId = rs.getInt(1);

            //selects all alternatives to the question
            String sqlGetAlt = "SELECT answer, score FROM Alternative WHERE questionId = " + QId +";";
            rs = statement.executeQuery(sqlGetAlt);
            int score = 0;
            while(rs.next()){
                String realAns = rs.getString("answer");
                System.out.println(realAns);
                if(answer.equals(realAns.toLowerCase())){
                    score = rs.getInt("score");
                    riktig = true;
                }
            }

            //chooses correct players score
            String points = (user.equals("player1") ? "p1Points" : "p2Points");

            //updates score in database
            String sqlUpdate = "UPDATE Game SET " + points + " = " + points + " + " + score + " WHERE gameId=" + gameId + ";";
            statement.execute(sqlUpdate);
            return riktig;

        }catch (SQLException e) {
            e.printStackTrace();
            return riktig;
        }finally {
            Cleaner.close(statement, null, connection);
        }
    }

    public static String findUser() {
        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;
        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            String sqlPlayer = "SELECT player1 FROM Game WHERE gameId=" + gameId + ";";
            rs = statement.executeQuery(sqlPlayer);
            rs.next();
            String player1Name = rs.getString("player1");
            if(username.equals(player1Name)){return "player1";}
            else return "player2";
        }catch (SQLException e) {
            e.printStackTrace();
            return "ex";
        }finally {
            Cleaner.close(statement, rs, connection);
        }
    }

    public void initialize(){ questionDisplay(); }
}