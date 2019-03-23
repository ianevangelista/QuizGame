package sample;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import Connection.ConnectionPool;

import javafx.scene.image.Image;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.File;

import static sample.ControllerHome.getUserName;

public class ControllerProfile {

    private static String username = getUserName();

    Connection connection = null;
    Statement statement = null;

    @FXML
    public ImageView picture;
    public Label printUsername;
    public Label printEmail;
    public Label printFemale;

    public void initialize() {
        choosePic();
    }

        public void sceneGame(ActionEvent event) { //hjemknapp
        ChangeScene.change(event, "Game.fxml");
    }

    public void choosePic(){

        String sql = "SELECT points, email, female FROM Player WHERE username = '" + username + "';";

        File first = new File("src/sample/1..png");
        File second = new File("src/sample/2..png");
        File third = new File("src/sample/3..png");

        try{

            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            rs.next();
            int points = rs.getInt("points");
            String email = rs.getString("email");
            int female = rs.getInt("female");


            Image one = new Image(first.toURI().toString());
            Image two = new Image(second.toURI().toString());
            Image three = new Image(third.toURI().toString());


            if(points < 100){
                picture.setImage(one);
            }

            else if(points < 500){
                picture.setImage(two);
            }

            else if(points > 500){
                picture.setImage(three);
            }

            System.out.println(username);

            printUsername.setText(username);
            printEmail.setText(email);
            if(female == 1) {
                printFemale.setText("Female");
            }
            else{
                printFemale.setText("Male");
            }

        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }

}
