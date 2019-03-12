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
    public static TableColumn userCol;
    public static TableColumn scoreCol;
    public static TableView hSTable;

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


           ObservableList<String> usernames = FXCollections.<String>observableArrayList();

           for(String name : usernameList){
               usernames.add(name);
           }

//MÅ LEGGE TIL RESULTATET I TABELLEN




            String tekst = "";
            for(String navn : usernameList){
                tekst += navn +" \n ";
            }
           for(String poeng : pointsList){
               tekst += poeng +" \n";
           }

            System.out.println(tekst);


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Cleaner.close(statement, null, connection);
        }
    }


}
