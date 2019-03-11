package sample;

import Connection.ConnectionClass;
import Connection.Cleaner;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;


public class ControllerHighScore {
    @FXML
    public static TextField hSText;
    public static TableColumn userCol;

    public static void highscoreTable(){

        ConnectionClass connectionClass = new ConnectionClass();
        Connection connection = connectionClass.getConnection();
        Statement statement = null;

        Cleaner cleaner = new Cleaner();

        String sqlHighScoreUser = "SELECT username FROM `Player` ORDER BY points desc LIMIT 5;";
        String sqlHighScorePoints = "SELECT points FROM `Player` ORDER BY points desc LIMIT 5;";

        ArrayList<String> usernameList = new ArrayList<>();
        ArrayList<String> pointsList = new ArrayList<>();

       try {

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

            hSText = new TextField();
            hSText.setPromptText("Halla");


           ObservableList<String> usernames = FXCollections.<String>observableArrayList();

           for(String name : usernameList){
               usernames.add(name);
           }
           TableView<String> tableView = new TableView<String>(usernames);




           userCol.setCellValueFactory("Halla");

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
            cleaner.close(statement, null, connection);
        }
    }



}
