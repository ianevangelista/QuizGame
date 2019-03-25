package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import Connection.ConnectionPool;
import Connection.Cleaner;
import java.sql.PreparedStatement;

import javafx.scene.image.Image;

import javax.swing.*;
import java.sql.*;
import java.io.File;

import static sample.ControllerHome.getUserName;

public class ControllerProfile {

    private static String username = getUserName();

    Connection connection = null;
    PreparedStatement statement = null;

    @FXML
    public ImageView picture;
    public Label printUsername;
    public Label printEmail;
    public Label printPoints;
    public Label printWon;
    public Label printLost;

    public Button confirmEmail;
    public Button confirmGender;
    public Button confirmPassword;
    public TextField inputEmail;
    public TextField inputOldPassword;
    public TextField inputNewPassword;
    public RadioButton btnMale;
    public RadioButton btnFemale;

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

        String sql = "SELECT points, email, gamesWon, gamesLost FROM Player WHERE username = ?;";

        File first = new File("src/sample/1..png");
        File second = new File("src/sample/2..png");
        File third = new File("src/sample/3..png");

        try{
            connection = ConnectionPool.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();

            rs.next();
            int pointsLest = rs.getInt("points");
            String points = String.valueOf(pointsLest);
            String email = rs.getString("email");
            int wonLest = rs.getInt("gamesWon");
            String won = String.valueOf(wonLest);
            int lostLest = rs.getInt("gamesLost");
            String lost = String.valueOf(lostLest);

            Image one = new Image(first.toURI().toString());
            Image two = new Image(second.toURI().toString());
            Image three = new Image(third.toURI().toString());


            if(pointsLest < 100){
                picture.setImage(one);
            }

            else if(pointsLest < 500){
                picture.setImage(two);
            }

            else if(pointsLest > 500){
                picture.setImage(three);
            }

            System.out.println(username);

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
            Cleaner.close(statement, null, connection);
        }
    }

    /*public void editProfile(){

        String
    }*/

}
