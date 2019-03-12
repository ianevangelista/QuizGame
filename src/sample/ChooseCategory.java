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



public class ChooseCategory {

    public TextField user_challenge;
    public Label usernameWrong;
    private Cleaner cleaner = new Cleaner();
    private ChangeScene sceneChanger = new ChangeScene();
    private String username;

    @FXML
    public Button category1;
    public Button category2;
    public Button category3;
    ConnectionClass connectionClass = new ConnectionClass();
    Connection connection = connectionClass.getConnection();
    Statement statement = null;
    ResultSet rs = null;

    ArrayList<Integer> categoryId = new ArrayList<Integer>();
    int[] randomCategoryId = new int[3];

    public void sceneHome(ActionEvent event) { //hjemknapp
        sceneChanger.change(event, "Main.fxml");
    }

    public void chooseCategory1(){
        try{
            String sql = "UPDATE `Game` SET `categoryId` = " + (categoryId.get(randomCategoryId[0])+1) + " WHERE `Game`.`gameId` = 1"; //+ ChooseOpponent.getGameId();
            statement.executeUpdate(sql);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            //cleaner.close(statement, rs, connection);
        }
    }

    public void chooseCategory2(){
        try{
            String sql = "UPDATE `Game` SET `categoryId` = " + (categoryId.get(randomCategoryId[1])+1) + " WHERE `Game`.`gameId` = 1"; //+ ChooseOpponent.getGameId();
            statement.executeUpdate(sql);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            //cleaner.close(statement, rs, connection);
        }
    }

    public void chooseCategory3(){
        try{
            String sql = "UPDATE `Game` SET `categoryId` = " + (categoryId.get(randomCategoryId[2])+1) + " WHERE `Game`.`gameId` = 1"; //+ ChooseOpponent.getGameId();
            statement.executeUpdate(sql);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            //cleaner.close(statement, rs, connection);
        }
    }

    public void initialize(){
        Random rand = new java.util.Random();
        try {
            statement = connection.createStatement();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }

        try {
            String sql = "SELECT * FROM `Category`";
            rs = statement.executeQuery(sql);

            ArrayList<String> categoryName = new ArrayList<String>();

            while(rs.next()){
                categoryId.add(rs.getInt("categoryId"));
                categoryName.add(rs.getString("name"));
            }

            int amountOfCategorys = categoryId.size()-1;

            randomCategoryId = new int[3];

            for (int i = 0; i < 3; i++) {
                randomCategoryId[i] = rand.nextInt(amountOfCategorys);
            }

            while (randomCategoryId[0] == randomCategoryId[1]) {
                randomCategoryId[1] = rand.nextInt(amountOfCategorys);
            }

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