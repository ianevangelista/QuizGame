package sample;

import Connection.ConnectionClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.ResultSet;
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

    ArrayList<Integer> categoryId = new ArrayList<Integer>();

    public void sceneHome(ActionEvent event) { //hjemknapp
        sceneChanger.change(event, "Main.fxml");
    }

    public void chooseCategory(int chosenButton){
        try{
            String sql = "UPDATE `Game` SET `categoryId` = " + categoryId.get(chosenButton) + "WHERE `Game`.`gameId` = " + ChooseOpponent.getId();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void initialize(){
        ConnectionClass connectionClass = new ConnectionClass();
        Connection connection = connectionClass.getConnection();

        ResultSet rs = null;

        Random rng = new java.util.Random();

        try {
            String sql = "SELECT * FROM `Category`";

            Statement statement = connection.createStatement();
            rs = statement.executeQuery(sql);

            ArrayList<String> categoryName = new ArrayList<String>();

            while(rs.next()){
                categoryId.add(rs.getInt("categoryId"));
                categoryName.add(rs.getString("name"));
            }

            int amountOfCategorys = categoryId.size();

            int[] randomCategoryId = new int[3];

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < i; j++){
                    randomCategoryId[i] = rng.nextInt(amountOfCategorys);
                    if(randomCategoryId[j] == randomCategoryId[i]) i--;
                }
            }

            category1.setText(categoryName.get(categoryId.get(randomCategoryId[0])));
            category2.setText(categoryName.get(categoryId.get(randomCategoryId[1])));
            category3.setText(categoryName.get(categoryId.get(randomCategoryId[2])));

            cleaner.close(statement, rs, connection);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}