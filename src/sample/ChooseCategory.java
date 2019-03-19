package sample;

import Connection.ConnectionPool;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import Connection.Cleaner;
import javafx.scene.control.Button;

import java.util.Collections;
import java.util.Random;
import java.util.ArrayList;

//imports the method getGameId() from the class ChooseOpponent
import static sample.ControllerHome.getUserName;



public class ChooseCategory {

    private String username = getUserName();

    @FXML
    public Button category1;
    public Button category2;
    public Button category3;

    //Connections set-up
    Connection connection = null;
    Statement statement = null;
    ResultSet rs = null;

    //ArrayList containing id of all categories in the database
    ArrayList<Integer> categoryId = new ArrayList<Integer>();

    //Array that fills up with three random and distinct numbers that reference indexes of the categoryId ArrayList
    int[] randomCategoryId = new int[3];

    public void sceneHome(ActionEvent event) { //home button
        ChangeScene.change(event, "Game.fxml");
    }

    public void chooseCategory1(ActionEvent event){ //When button 1 is pressed
        try{
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();

            String sqlGetGameId = "SELECT gameId FROM Game WHERE player2 = '" + username +"'";
            rs = statement.executeQuery(sqlGetGameId);
            rs.next();

            int gameId = rs.getInt("gameId");

            int chosenCategoryId = randomCategoryId[0];
            String sql = "UPDATE Game SET categoryId = " + chosenCategoryId + " WHERE gameId = " + gameId;
            statement.executeUpdate(sql);
            questionPicker(chosenCategoryId, gameId);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            Cleaner.close(statement, rs, connection);
            ChangeScene.change(event, "Question.fxml");
        }
    }

    public void chooseCategory2(ActionEvent event){ //When button 2 is pressed
        try{
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();

            String sqlGetGameId = "SELECT gameId FROM Game WHERE player2 = '" + username +"'";
            rs = statement.executeQuery(sqlGetGameId);
            rs.next();

            int gameId = rs.getInt("gameId");

            int chosenCategoryId = randomCategoryId[1];
            String sql = "UPDATE Game SET categoryId = " + chosenCategoryId + " WHERE gameId = " + gameId;
            statement.executeUpdate(sql);
            questionPicker(chosenCategoryId, gameId);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            Cleaner.close(statement, rs, connection);
            ChangeScene.change(event, "Question.fxml");
        }
    }

    public void chooseCategory3(ActionEvent event){ //When button 3 is pressed
        try{
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();

            String sqlGetGameId = "SELECT gameId FROM Game WHERE player2 = '" + username +"'";
            rs = statement.executeQuery(sqlGetGameId);
            rs.next();

            int gameId = rs.getInt("gameId");

            int chosenCategoryId = randomCategoryId[2];
            String sql = "UPDATE Game SET categoryId = " + chosenCategoryId + " WHERE gameId = " + gameId;
            statement.executeUpdate(sql);
            questionPicker(chosenCategoryId, gameId);

        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            Cleaner.close(statement, rs, connection);
            ChangeScene.change(event, "Question.fxml");
        }
    }

    public void initialize(){ //gets run when the window is opened for the first time
        Random rand = new java.util.Random();
        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }

        try {
            // Gets all categories from the database
            String sql = "SELECT * FROM `Category`";
            rs = statement.executeQuery(sql);

            ArrayList<String> categoryName = new ArrayList<String>();

            while(rs.next()){
                categoryId.add(rs.getInt("categoryId"));
                categoryName.add(rs.getString("name"));
            }

            int amountOfCategorys = categoryId.size()-1;

            //Fills array with random numbers
            for (int i = 0; i < 3; i++) {
                randomCategoryId[i] = rand.nextInt(amountOfCategorys)+1;
            }

            //Checks that first and second element are different
            while (randomCategoryId[0] == randomCategoryId[1]) {
                randomCategoryId[1] = rand.nextInt(amountOfCategorys)+1;
            }

            //Checks that third element is different from first and second
            while (randomCategoryId[0] == randomCategoryId[2] || randomCategoryId[1] == randomCategoryId[2]) {
                randomCategoryId[2] = rand.nextInt(amountOfCategorys)+1;
            }
            category1.setText(categoryName.get(randomCategoryId[0]-1));
            category2.setText(categoryName.get(randomCategoryId[1]-1));
            category3.setText(categoryName.get(randomCategoryId[2]-1));

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            Cleaner.close(statement, rs, connection);
        }
    }

    private void questionPicker(int categoryId, int gameId) { //helene
        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            ResultSet rs = null;

            //gets all the questions in chosen category
            int[] questionId = new int[3];
            String sqlGetText = "SELECT questionId FROM Question WHERE categoryId=" + categoryId + " ORDER BY questionId;";
            rs = statement.executeQuery(sqlGetText);
            ArrayList<Integer> listQuestion = new ArrayList<Integer>();
            while(rs.next()) {
                listQuestion.add(new Integer(rs.getInt("questionId")));
            }

            //shuffles the list and puts 0-2 in a new list
            Collections.shuffle(listQuestion);
            for (int i=0; i<3; i++) {
                questionId[i] = listQuestion.get(i);
            }

            //updates the database with the selected questionids
            String sqlUpdate = "UPDATE Game SET question1='" + questionId[0] + "', question2 ='" + questionId[1] + "' , question3='" + questionId[2] + "' WHERE gameId=" + gameId + ";";

            statement.executeUpdate(sqlUpdate);

        }catch (SQLException e) {
            e.printStackTrace();
        }finally {
            Cleaner.close(statement, rs, connection);
        }
    }
}