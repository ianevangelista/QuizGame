package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import static sample.ChooseOpponent.getGameId;
import static sample.ControllerHome.getUserName;

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
    public TextField questionField;

    public void sceneQuestion(ActionEvent event) { //clicks submit button
        String sceneNavn;
        boolean riktig = questionCheck(gameId, username);   //checks answer

        if(riktig) sceneNavn = "CorrectAnswer.fxml";
        else sceneNavn = "IncorrectAnswer.fxml";
        if(questionCount == 3) {
            try{
                connection = ConnectionPool.getConnection();
                statement = connection.createStatement();
                String player = findUser();
                String finished = player.equals("player1") ? "p1Finished" : "p2Finished";
                String sqlUpdate = "UPDATE Game SET " + finished + "=1 WHERE gameId=" + gameId + ";";
                statement.executeUpdate(sqlUpdate);
            }catch(SQLException e) {
                e.printStackTrace();
            }finally {
                Cleaner.close(statement, null,connection);
            }
            //sett p1/p2finish == true
            sceneNavn = "result.fxml";
        }
        ChangeScene.change(event, sceneNavn);              //changes scene
    }

    public void questionDisplay() { //displays questions
        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();

            String sqlGetText = "SELECT questionText FROM Game JOIN Question ON questionId = question"; //sqlstatement for question text

            ResultSet rsQuestionText = statement.executeQuery(sqlGetText + (questionCount+1) + ";");        //
            rsQuestionText.next();
            String qText = rsQuestionText.getString("questionText");
            questionField.setText(qText);
            questionCount++;

        }catch (SQLException e) {
            e.printStackTrace();
        }finally {
            Cleaner.close(statement, null, connection);
        }
    }

    public boolean questionCheck(int gameId, String username) {
        boolean riktig = false;

        String sqlGetAlt = "SELECT answer FROM Alternative WHERE questionId=";
        String sqlGetQId = "FROM Game WHERE gameId=" + gameId + ";";
        String[] sqlQuestionName = {"question1", "question2", "question3"};
        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();

            String user = findUser();       //find user
            String answer = (answerField.getText()).toLowerCase();          //get answer in lowercase

            ResultSet rsQId = statement.executeQuery("SELECT " + sqlQuestionName[questionCount] + sqlGetQId);
            rsQId.next();
            int QId = rsQId.getInt(1);

            //goes through all the alternatives to the question
            ResultSet rsAlterative = statement.executeQuery(sqlGetAlt + QId + ";");
            ArrayList <String> alternative = new ArrayList<>();
            while(rsAlterative.next()) alternative.add(new String(rsAlterative.getString("answer")));

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
            if(rsScore.next()) score = rsScore.getInt("score");

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
        String[] players = {"player1", "player2"};
        String sqlPlayer = "FROM Game WHERE gameId=" + gameId + ";";
        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();

            for(String p:players) {
                rs = statement.executeQuery("SELECT " + p + sqlPlayer);
                rs.next();
                String playerName = rs.getString(p);
                if(username.equals(playerName)){return p;}
            }
            return null;
        }catch (SQLException e) {
            e.printStackTrace();
            return "ex";
        }finally {
            Cleaner.close(statement, rs, connection);
        }
    }

    public void questionPicker() { //helene
        String sqlCategory = "SELECT categoryID FROM Game WHERE gameID ='" + gameId + "';"; //finner hvilken kategori spiller har valgt
        ResultSet rs = null;
        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();

            //henter
            rs = statement.executeQuery(sqlCategory);               //lager restultset med kategorinr
            rs.next();                                                        //henter første i rsCategoryNumber
            int categoryId = rs.getInt("categoryId");             //lager en int med categoryId

            int[] questionId = new int[3];
            String sqlGetText = "SELECT questionId FROM Question WHERE categoryId=" + categoryId + " ORDER BY questionId;";
            rs = statement.executeQuery(sqlGetText);
            ArrayList<Integer> listQuestion = new ArrayList<Integer>();
            while(rs.next()) {
                listQuestion.add(new Integer(rs.getInt("questionId")));
            }
            Collections.shuffle(listQuestion);
            for (int i=0; i<3; i++) {
                questionId[i] = listQuestion.get(i);
            }

            String sqlUpdate = "UPDATE Game SET question1='" + questionId[0] + "', question2 ='" + questionId[1] + "' , question3='" + questionId[2] + "' WHERE gameId=" + gameId + ";";

            statement.executeUpdate(sqlUpdate);

        }catch (SQLException e) {
            e.printStackTrace();
        }finally {
            Cleaner.close(statement, rs, connection);
        }
    }
}