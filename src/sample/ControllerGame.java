package sample;

import Connection.ConnectionClass;
import Connection.Cleaner;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;

public class ControllerGame {

    @FXML
    public TextField user_challenge;
    public Label usernameWrong;
    public Button category1;
    public Button category2;
    public Button category3;
    //Correct answer
    public TextField newPoints;
    public TextField player1Pt;
    public TextField player2Pt;
    //Incorrect answer
    public TextField player1PtW;
    public TextField player2PtW;
    private String userEmail;


    public ChangeScene sceneChanger = new ChangeScene();

    public void chooseOpponent(ActionEvent event) {
        ConnectionClass connectionClass = new ConnectionClass();
        Connection connection = connectionClass.getConnection();
        ResultSet rs = null;

        String sql = "SELECT username FROM Player WHERE username ='" + user_challenge.getText() + "';";

        try{
            Statement statement = connection.createStatement();
            rs = statement.executeQuery(sql);

            String realUsername = null;
            if (rs.next()) {
                realUsername = rs.getString("user_challenge");
            } else {
                realUsername = "-1";
            }
            if (realUsername.equals(user_challenge.getText())){
                rs.close();
                statement.close();
                connection.close();
                sceneChanger.change(event, "Categories.fxml");
            } else {
                rs.close();
                statement.close();
                connection.close();
                sceneChanger.changeVisibility(true, usernameWrong);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void chooseCategories(ActionEvent event) {
        ConnectionClass connectionClass1 = new ConnectionClass();
        Connection connectionCategory = connectionClass1.getConnection();
        Cleaner cleaner = new Cleaner();
        ResultSet rs = null;

        ArrayList categoriesList = new ArrayList();
        int[] randList = new int[3];
        String sql = "";

        for(Object i : categoriesList) {
            sql = "SELECT categoryId FROM Category WHERE categoryId = " + i + ";";
            categoriesList.add(sql);
        }

        try {
            Statement statement = connectionCategory.createStatement();
            rs = statement.executeQuery(sql);

            ArrayList<Integer> numberList = new ArrayList<Integer>();
            for (int i = 1; i < categoriesList.size(); i++) {
                numberList.add(new Integer(i));
            }
            Collections.shuffle(numberList);
            for (int i = 0; i < 3; i++) {
                randList[i] = numberList.get(i);
            }

            String[] chosenCategories = new String[3];
            for (int i = 0; i < 3; i++) {
                chosenCategories[i] = "SELECT name FROM Category WHERE categoryId = " + randList[i] + ";";
            }

            category1.setText(chosenCategories[0]);
            category2.setText(chosenCategories[1]);
            category3.setText(chosenCategories[2]);

            sceneChanger.change(event, "Question.fxml");
            cleaner.closeTwo(statement, connectionCategory);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void questionPicker(int gameId) { //helene
        Connection connection = null;
        Statement statement = null;
        Cleaner cleaner = new Cleaner();

        String sqlCategory = "SELECT categoryID FROM Game WHERE gameID ='" + gameId + "';"; //finner hvilken kategori spiller har valgt

        String sqlNumberQuestion = "SELECT COUNT(questionId) FROM Question WHERE categoryId ='"; //teller ant spørsmål

        try {
            ConnectionClass connectionClass = new ConnectionClass();
            connection = connectionClass.getConnection();
            statement = connection.createStatement();

            //henter
            ResultSet rsCategoryNumber = statement.executeQuery(sqlCategory);               //lager restultset med kategorinr
            rsCategoryNumber.next();                                                        //henter første i rsCategoryNumber
            int categoryId = rsCategoryNumber.getInt("categoryId");             //lager en int med categoryId
            rsCategoryNumber.close();

            ResultSet rsNumberQuestion = statement.executeQuery(sqlNumberQuestion + categoryId + "';"); //henter ant spørsmål i kategorien
            rsNumberQuestion.next();                                                        //henter første i rsNumberQuestion
            int antQuestion = rsNumberQuestion.getInt("COUNT(questionId");      //lager en int med ant spøsmål i kategorien
            rsNumberQuestion.close();

            int[] questionId = new int[3];

            ArrayList<Integer> listQuestion = new ArrayList<Integer>();
            for (int i=1; i < antQuestion+1; i++) {
                listQuestion.add(new Integer(i));
            }
            Collections.shuffle(listQuestion);
            for (int i=0; i<3; i++) {
                questionId[i] = listQuestion.get(i);
            }

            String sqlGetText = "SELECT questionId FROM Question WHERE categoryId='" + categoryId + "'ORDER BY questionId LIMIT 1 OFFSET ";
            int[] questionText = new int[3];
            ResultSet rsText;
            for(int i = 0; i < questionText.length; i++) {
                rsText = statement.executeQuery(sqlGetText + (questionId[i]-1) + ";");
                rsText.next();
                questionText[i] = rsText.getInt("questionId");
            }

            String sqlUpdate = "UPDATE Game SET question1='" + questionText[0] + "', question2 ='" + questionText[1] + "' , question3='" + questionText[2] + "' WHERE gameId=" + gameId + ";";

            statement.executeQuery(sqlUpdate);

        }catch (SQLException e) {
            e.printStackTrace();
        }finally {
            cleaner.closeTwo(statement, connection);
        }
    }

    public void correctAnswer(ActionEvent event, int gameId, int poeng) {
        ConnectionClass connectionClass = new ConnectionClass();
        Connection connection = connectionClass.getConnection();
        Statement statement = null;

        Cleaner cleaner = new Cleaner();

        String sqlEmailP1 = "SELECT player1 FROM Game WHERE gameId =" + gameId + ";";
        String sqlPlayer1 = "SELECT p1Points FROM Game WHERE gameId =" + gameId + ";";
        String sqlPlayer2 = "SELECT p2Points FROM Game WHERE gameId =" + gameId + ";";


        try {
            statement = connection.createStatement();

            //Henter ut mail til spiller 1
            ResultSet p1 = statement.executeQuery(sqlEmailP1);
            p1.next();
            String email = p1.getString("emailP1");
            p1.close();

            //Henter ut resultat til spiller 1
            ResultSet pt1 = statement.executeQuery(sqlPlayer1);
            pt1.next();
            int points1 = pt1.getInt("p1Points");
            pt1.close();

            //Henter ut resultat til spiller 2
            ResultSet pt2 = statement.executeQuery(sqlPlayer2);
            pt2.next();
            int points2 = pt2.getInt("p2Points");
            pt2.close();

            //Sjekker om det er spiller 1 eller 2 som er "Hovedspiller" og skriver poeng i passende rekkefølge
            /*if (email.equals(userEmail)) {
                player1Pt.setText(Integer.toString(points1));
                player2Pt.setText(Integer.toString(points2));
            } else {
                player1Pt.setText(Integer.toString(points2));
                player2Pt.setText(Integer.toString(points1));
            }*/ //her må man fikse userEmail - ian

            //Skriver ut nye poeng
            newPoints.setText(String.valueOf(poeng));

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            cleaner.closeTwo(statement, connection);
        }
    }

    public void incorrectAnswer(ActionEvent event, int gameId, int poeng) {
        ConnectionClass connectionClass = new ConnectionClass();
        Connection connection = connectionClass.getConnection();
        Statement statement = null;

        Cleaner cleaner = new Cleaner();

        String sqlEmailP1 = "SELECT player1 FROM Game WHERE gameId =" + gameId + ";";
        String sqlPlayer1 = "SELECT p1Points FROM Game WHERE gameId =" + gameId + ";";
        String sqlPlayer2 = "SELECT p2Points FROM Game WHERE gameId =" + gameId + ";";


        try {
            statement = connection.createStatement();

            //Henter ut mail til spiller 1
            ResultSet p1 = statement.executeQuery(sqlEmailP1);
            p1.next();
            String email = p1.getString("emailP1");
            p1.close();

            //Henter ut resultat til spiller 1
            ResultSet pt1 = statement.executeQuery(sqlPlayer1);
            pt1.next();
            int points1 = pt1.getInt("p1Points");
            pt1.close();

            //Henter ut resultat til spiller 2
            ResultSet pt2 = statement.executeQuery(sqlPlayer2);
            pt2.next();
            int points2 = pt2.getInt("p2Points");
            pt2.close();

            /*
            //Sjekker om det er spiller 1 eller 2 som er "Hovedspiller" og skriver poeng i passende rekkefølge
            if (email.equals(userEmail)) {
                player1PtW.setText(Integer.toString(points1));
                player2PtW.setText(Integer.toString(points2));
            } else {
                player1PtW.setText(Integer.toString(points2));
                player2PtW.setText(Integer.toString(points1));
            }*/ // fikse userEmail - ian

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            cleaner.closeTwo(statement, connection);
        }
    }

    public void highscore(ActionEvent event) { //HighScore knapp
        sceneChanger.change(event, "HighScore.fxml");
    }

    public void start(ActionEvent event) {
        sceneChanger.change(event, "Category.fxml");
    }

    public void sceneInfo(ActionEvent event) { //trykker på infoknapp
        sceneChanger.change(event, "Info.fxml");
    }

    public void sceneInfoLogin(ActionEvent event) { //trykker på infoknapp
        sceneChanger.change(event, "Info_Login.fxml");
    }

    public void sceneHome(ActionEvent event) { //hjemknapp
        sceneChanger.change(event, "Main.fxml");
    }

    public void sceneGame(ActionEvent event) { //hjemknapp
        sceneChanger.change(event, "Game.fxml");
    }

}