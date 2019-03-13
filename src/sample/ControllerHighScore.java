package sample;

import Connection.ConnectionPool;
import Connection.Cleaner;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;



import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;



public class ControllerHighScore {
   @FXML
    public static TextField userCol;
    public static TextField scoreCol;


    public static void highscoreTable(){

        Connection connection = null;
        Statement statement = null;

        String sqlHighScoreUser = "SELECT username FROM `Player` ORDER BY points desc LIMIT 5;";
        String sqlHighScorePoints = "SELECT points FROM `Player` ORDER BY points desc LIMIT 5;";

        ArrayList<String> usernameList = new ArrayList<>();
        ArrayList<String> pointsList = new ArrayList<>();

       try {
           connection = ConnectionPool.getConnection();
           statement = connection.createStatement();

            //Legger navn i tabellen highScoreList
            ResultSet hs = statement.executeQuery(sqlHighScoreUser);
            while(hs.next()){
                usernameList.add( hs.getString("username"));
            }

            //Legger til poeng i highScoreList
            hs = statement.executeQuery(sqlHighScorePoints);

            while(hs.next()){
                pointsList.add( Integer.toString(hs.getInt("points")));
            }

            String userText = "";
            for(String name : usernameList){
                userText += name +" \n ";
            }
           String pointsText = "";
           for(String points : pointsList){
               pointsText += points +" \n";
           }

           //Printer ut teksten
           userCol.setText(userText);
           scoreCol.setText(pointsText);


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Cleaner.close(statement, null, connection);
        }
    }


}
