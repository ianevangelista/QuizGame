package sample;

import Connection.ConnectionClass;
import Connection.Cleaner;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import javax.swing.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;

public class ControllerGame {

    private String userEmail;
    private int gameId = 899;
    private int questionCount;

    @FXML
    public TextField user_challenge;
    public Label usernameWrong;
    //Category
    public Button category1;
    public Button category2;
    public Button category3;
    public Button categoyTest;
    public int[] randList = new int[3];

    //Correct answer
    public TextField newPoints;
    public TextField player1Pt;
    public TextField player2Pt;
    //Incorrect answer
    public TextField player1PtW;
    public TextField player2PtW;
    //result
    public TextField totalScore;
    public TextField resultText;
    public TextField resultHeading;



    public ChangeScene sceneChanger = new ChangeScene();
    public Cleaner cleaner = new Cleaner();

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
                cleaner.close(statement, rs, connection);
                sceneChanger.change(event, "Categories.fxml");
            } else {
                cleaner.close(statement, rs, connection);
                sceneChanger.changeVisibility(true, usernameWrong);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void chooseCategories() { //juni

        Connection connection = null;
        Statement statement = null;

        ArrayList <Integer> categoriesList = new ArrayList<>();
        String[] chosenCategories = new String[3];

        String sql =  "SELECT categoryId FROM Category;";

        try {
            ConnectionClass connectionClass = new ConnectionClass();
            connection = connectionClass.getConnection();
            ResultSet rs = null;

            statement = connection.createStatement();
            rs = statement.executeQuery(sql);

            while(rs.next()){
                categoriesList.add(new Integer(rs.getInt("categoryId")));
            }

            Collections.shuffle(categoriesList);
            for (int i = 0; i < 3; i++) {
                randList[i] = categoriesList.get(i);
            }

            //Category 1
            ResultSet rs1 = null;
            statement = connection.createStatement();

            String sql1 = "SELECT name FROM Category WHERE categoryId = " + randList[0] + ";";

            rs1 = statement.executeQuery(sql1);
            rs1.next();
            chosenCategories[0] = rs1.getString("name");

            //Category 2
            ResultSet rs2 = null;
            statement = connection.createStatement();

            String sql2 = "SELECT name FROM Category WHERE categoryId = " + randList[1] + ";";

            rs2 = statement.executeQuery(sql2);
            rs2.next();
            chosenCategories[1] = rs2.getString("name");

            //Category 3
            ResultSet rs3 = null;
            statement = connection.createStatement();

            String sql3 = "SELECT name FROM Category WHERE categoryId = " + randList[2] + ";";

            rs3 = statement.executeQuery(sql3);
            rs3.next();
            chosenCategories[2] = rs3.getString("name");

            System.out.println(randList[2]);


            category1.setText(chosenCategories[0]);
            category2.setText(chosenCategories[1]);
            category3.setText(chosenCategories[2]);

        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally {
            cleaner.close(statement, null, connection);
        }
    }

    public void button1(ActionEvent event){
        categoryChosen(randList[0]);
        questionPicker();
        sceneChanger.change(event, "Question.fxml");
    }
    public void button2(ActionEvent event){
        categoryChosen(randList[1]);
        questionPicker();
        sceneChanger.change(event, "Question.fxml");
    }
    public void button3(ActionEvent event){
        categoryChosen(randList[2]);
        questionPicker();
        sceneChanger.change(event, "Question.fxml");
    }

    public void categoryChosen(int categoryID){
        Connection connection = null;
        Statement statement = null;

        String sql = "UPDATE Game SET categoryId = " + categoryID + " WHERE gameId = " + gameId + ";";

        try {
            ConnectionClass connectionClass = new ConnectionClass();
            connection = connectionClass.getConnection();
            ResultSet rs = null;
            statement = connection.createStatement();
            statement.execute(sql);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cleaner.close(statement, null, connection);
        }


    }

    public void questionPicker() { //helene
        Connection connection = null;
        Statement statement = null;

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
            cleaner.close(null, rsCategoryNumber, null);

            ResultSet rsNumberQuestion = statement.executeQuery(sqlNumberQuestion + categoryId + "';"); //henter ant spørsmål i kategorien
            rsNumberQuestion.next();//henter første i rsNumberQuestion
            int antQuestion = rsNumberQuestion.getInt("COUNT(questionId)");      //lager en int med ant spøsmål i kategorien
            cleaner.close(null, rsNumberQuestion, null);

            int[] questionId = new int[3];
            String sqlGetText = "SELECT questionId FROM Question WHERE categoryId=" + categoryId + " ORDER BY questionId;";
            ResultSet rsText;
            rsText = statement.executeQuery(sqlGetText);
            ArrayList<Integer> listQuestion = new ArrayList<Integer>();
            for (int i=1; i < antQuestion+1; i++) {
                rsText.next();
                listQuestion.add(new Integer(rsText.getInt("questionId")));

            }
            Collections.shuffle(listQuestion);
            for (int i=0; i<3; i++) {
                questionId[i] = listQuestion.get(i);
            }

            String sqlUpdate = "UPDATE Game SET question1='" + questionId[0] + "', question2 ='" + questionId[1] + "' , question3='" + questionId[2] + "' WHERE gameId=" + gameId + ";";

            statement.execute(sqlUpdate);

        }catch (SQLException e) {
            e.printStackTrace();
        }finally {
            cleaner.close(statement, null, connection);
        }
    }

    public void correctAnswer(ActionEvent event, int gameId, int poeng) {
        ConnectionClass connectionClass = new ConnectionClass();
        Connection connection = connectionClass.getConnection();
        Statement statement = null;

        String sqlEmailP1 = "SELECT player1 FROM Game WHERE gameId =" + gameId + ";";
        String sqlPlayer1 = "SELECT p1Points FROM Game WHERE gameId =" + gameId + ";";
        String sqlPlayer2 = "SELECT p2Points FROM Game WHERE gameId =" + gameId + ";";


        try {
            statement = connection.createStatement();

            //Henter ut mail til spiller 1
            ResultSet p1 = statement.executeQuery(sqlEmailP1);
            p1.next();
            String email = p1.getString("emailP1");
            cleaner.close(statement, null, null);

            //Henter ut resultat til spiller 1
            ResultSet pt1 = statement.executeQuery(sqlPlayer1);
            pt1.next();
            int points1 = pt1.getInt("p1Points");
            cleaner.close(null, pt1, null);

            //Henter ut resultat til spiller 2
            ResultSet pt2 = statement.executeQuery(sqlPlayer2);
            pt2.next();
            int points2 = pt2.getInt("p2Points");
            cleaner.close(null, pt2, null);

            //Sjekker om det er spiller 1 eller 2 som er "Hovedspiller" og skriver poeng i passende rekkefølge
            if (email.equals(userEmail)) {
                player1Pt.setText(Integer.toString(points1));
                player2Pt.setText(Integer.toString(points2));
            } else {
                player1Pt.setText(Integer.toString(points2));
                player2Pt.setText(Integer.toString(points1));
            }

            //Skriver ut nye poeng
            newPoints.setText(String.valueOf(poeng));

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            cleaner.close(statement, null, connection);
        }
    }

    public void incorrectAnswer(ActionEvent event, int gameId) {
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
            cleaner.close(null, p1, null);

            //Henter ut resultat til spiller 1
            ResultSet pt1 = statement.executeQuery(sqlPlayer1);
            pt1.next();
            int points1 = pt1.getInt("p1Points");
            cleaner.close(null, pt1, null);

            //Henter ut resultat til spiller 2
            ResultSet pt2 = statement.executeQuery(sqlPlayer2);
            pt2.next();
            int points2 = pt2.getInt("p2Points");
            cleaner.close(null, pt2, null);


            //Sjekker om det er spiller 1 eller 2 som er "Hovedspiller" og skriver poeng i passende rekkefølge
            if (email.equals(userEmail)) {
                player1PtW.setText(Integer.toString(points1));
                player2PtW.setText(Integer.toString(points2));
            } else {
                player1PtW.setText(Integer.toString(points2));
                player2PtW.setText(Integer.toString(points1));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            cleaner.close(statement, null, connection);
        }
    }

    public void setUserEmail(String userEmail){
        this.userEmail = userEmail;
    } //husk å bruke denne metoden i controllerHome i log in for å sette verdien
    //   ControllerGame.setUserEmail(email.getText());

    public void highscore(ActionEvent event) { //HighScore knapp
        sceneChanger.change(event, "HighScore.fxml");
    }

    public void start(ActionEvent event) {
        chooseCategories();
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

    public void result(ActionEvent event, int gameId){
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
            cleaner.close(null, p1, null);

            //Henter ut resultat til spiller 1
            ResultSet pt1 = statement.executeQuery(sqlPlayer1);
            pt1.next();
            int points1 = pt1.getInt("p1Points");
            cleaner.close(null, pt1, null);

            //Henter ut resultat til spiller 2
            ResultSet pt2 = statement.executeQuery(sqlPlayer2);
            pt2.next();
            int points2 = pt2.getInt("p2Points");
            cleaner.close(null, pt2, null);

            if(!(email.equals(userEmail))){
                int help = points1;
                points1 = points2;
                points2 = help;
            }

            if(points1 > points2){
                resultText.setText("You are the winner of this round.");
                resultHeading.setText("Winner!");
            }else {
                resultText.setText("You lost this round.");
                resultHeading.setText("Loser...");
            }

            totalScore.setText(Integer.toString(points1));

            sceneChanger.change(event, "Game.fxml");
            sceneChanger.change(event, "ChallangeUser.fxml");

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            cleaner.close(statement, null, connection);
        }
    }

}