package sample;

import Connection.ConnectionClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import Connection.Cleaner;
import Connection.ConnectionClass;
import javafx.scene.control.Button;
import java.util.Random;
import java.util.ArrayList;

//imports the method getGameId() from the class ChooseOpponent
import static sample.ChooseOpponent.getGameId;



public class ChooseCategory {

    public TextField user_challenge;
    public Label usernameWrong;
    private ChangeScene sceneChanger = new ChangeScene();
    private String username;

    @FXML
    public Button category1;
    public Button category2;
    public Button category3;

    //Connections set-up
    ConnectionClass connectionClass = new ConnectionClass();
    Connection connection = connectionClass.getConnection();
    Statement statement = null;
    ResultSet rs = null;

    //ArrayList containing id of all categories in the database
    ArrayList<Integer> categoryId = new ArrayList<Integer>();

    //Array that fills up with three random and distinct numbers that reference indexes of the categoryId ArrayList
    int[] randomCategoryId = new int[3];

    public void sceneHome(ActionEvent event) { //home button
        sceneChanger.change(event, "Main.fxml");
    }

    public void chooseCategory1(){ //When button 1 is pressed
        try{
            String sql = "UPDATE `Game` SET `categoryId` = " + (categoryId.get(randomCategoryId[0])+1) + " WHERE `Game`.`gameId` = " + getGameId();
            statement.executeUpdate(sql);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            Cleaner.close(statement, rs, connection);
        }
    }

    public void chooseCategory2(){ //When button 2 is pressed
        try{
            String sql = "UPDATE `Game` SET `categoryId` = " + (categoryId.get(randomCategoryId[1])+1) + " WHERE `Game`.`gameId` = " + getGameId();
            statement.executeUpdate(sql);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            Cleaner.close(statement, rs, connection);
        }
    }

    public void chooseCategory3(){ //When button 3 is pressed
        try{
            String sql = "UPDATE `Game` SET `categoryId` = " + (categoryId.get(randomCategoryId[2])+1) + " WHERE `Game`.`gameId` = " + getGameId();
            statement.executeUpdate(sql);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            Cleaner.close(statement, rs, connection);
        }
    }

    public void initialize(){ //gets run when the window is opened for the first time
        Random rand = new java.util.Random();
        try {
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
                randomCategoryId[i] = rand.nextInt(amountOfCategorys);
            }

            //Checks that first and second element are different
            while (randomCategoryId[0] == randomCategoryId[1]) {
                randomCategoryId[1] = rand.nextInt(amountOfCategorys);
            }

            //Checks that third element is different from first and second
            while (randomCategoryId[0] == randomCategoryId[2] || randomCategoryId[1] == randomCategoryId[2]) {
                randomCategoryId[1] = rand.nextInt(amountOfCategorys);
            }

            category1.setText(categoryName.get(categoryId.get(randomCategoryId[0])));
            category2.setText(categoryName.get(categoryId.get(randomCategoryId[1])));
            category3.setText(categoryName.get(categoryId.get(randomCategoryId[2])));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}