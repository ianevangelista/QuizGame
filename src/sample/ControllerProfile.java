package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import Connection.ConnectionPool;
import Connection.Cleaner;
import java.sql.PreparedStatement;

import javafx.scene.image.Image;

import javax.sound.midi.Soundbank;
import javax.swing.*;
import java.sql.*;
import java.io.File;

import static sample.ControllerHome.getUserName;

public class ControllerProfile {

    private String username = getUserName();

    private Connection connection = null;
    private PreparedStatement statement = null;

    @FXML
    public ImageView picture;
    public Label printUsername;
    public Label printEmail;
    public Label printPoints;
    public Label printWon;
    public Label printLost;


    public void initialize() {
        choosePic();
    }

    public void sceneGame(ActionEvent event) { //hjemknapp
        ChangeScene.change(event, "Game.fxml");
    }

    public void edit(ActionEvent event){
        ChangeScene.change(event, "EditProfile.fxml");
    }

    public void choosePic(){
        ResultSet rs = null;
        try{
            connection = ConnectionPool.getConnection();
            String sql = "SELECT points, email, gamesWon, gamesLost FROM Player WHERE username = ?;";
            statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            rs = statement.executeQuery();

            rs.next();
            int pointsLest = rs.getInt("points");
            String points = String.valueOf(pointsLest);
            String email = rs.getString("email");
            int wonLest = rs.getInt("gamesWon");
            String won = String.valueOf(wonLest);
            int lostLest = rs.getInt("gamesLost");
            String lost = String.valueOf(lostLest);

            if(pointsLest < 100){
                File first = new File("src/sample/1..png");
                Image one = new Image(first.toURI().toString());
                picture.setImage(one);
            }

            else if(pointsLest < 500){
                File second = new File("src/sample/2..png");
                Image two = new Image(second.toURI().toString());
                picture.setImage(two);
            }

            else{
                File third = new File("src/sample/3..png");
                Image three = new Image(third.toURI().toString());
                picture.setImage(three);
            }

            printUsername.setText(username);
            printEmail.setText(email);
            printPoints.setText(points);
            printWon.setText(won);
            printLost.setText(lost);
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        finally {
            Cleaner.close(statement, rs, connection);
        }
    }
}