package sample;

import Connection.ConnectionClass;
import Connection.Cleaner;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ControllerHighScore {
    @FXML
    public TextField knapp;

    public void highscore(){
        ConnectionClass connectionClass = new ConnectionClass();
        Connection connection = connectionClass.getConnection();
        Statement statement = null;

        Cleaner cleaner = new Cleaner();

        String sqlHighScoreUser = "SELECT username FROM `Player` ORDER BY points desc LIMIT 5;";
        String sqlHighScorePoints = "SELECT points FROM `Player` ORDER BY points desc LIMIT 5;";

        ArrayList<String> highScoreList = new ArrayList<>();

        try {
            statement = connection.createStatement();

            //Legger navn i tabellen highScoreList
            ResultSet hs = statement.executeQuery(sqlHighScoreUser);
            while(hs.next()){
                highScoreList.add(0, hs.getString("Highscore"));
            }

            //Legger til poeng i highScoreList
            hs = statement.executeQuery(sqlHighScorePoints);

            while(hs.next()){
                highScoreList.add(1, Integer.toString(hs.getInt("Points")));
            }



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
