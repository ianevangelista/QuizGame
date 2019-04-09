package Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import Connection.ConnectionPool;
import Connection.Cleaner;
import java.sql.PreparedStatement;

import javafx.scene.image.Image;

import java.sql.*;
import java.io.File;

import static Controllers.ControllerHome.getUserName;

/**
 * The class ControllerProfile includes everything involving showing and editing the profile.
 */

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


    /**
     * This method runs when your access the profile function in the program.
     * The method runs setProfile().
     */
    public void initialize() {
        setProfile();
    }

    /**
     * A method when the home button is pressed.
     * You will return to the previous page, the game page.
     * @param event is a necessary parameter which is used in a method from the class ChangeScene.
     */

    public void sceneGame(ActionEvent event) {
        ChangeScene.change(event, "/Scenes/Game.fxml");
    }

    /**
     * A method for when the home button is pressed.
     * You will be sent forward to the edit page.
     * @param event is a necessary parameter which is used in a method from the class ChangeScene.
     */

    public void edit(ActionEvent event){ ChangeScene.change(event, "/Scenes/EditProfile.fxml"); }

    /**
     * A method which sets the profile page of the user.
     */
    private void setProfile(){
        ResultSet rs = null;
        try{
            /*
              Sets up connection
              Selects ponts, email, gamesWon and gamesLost from Player
             */
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
            /*
              This is the profile picture for the first level
             */
            if(pointsLest < 500){
                File first = new File("src/Scenes/1..png");
                Image one = new Image(first.toURI().toString());
                picture.setImage(one);
            }
            /*
              This is the profile picture for the second level
             */
            else if(pointsLest < 1000){
                File second = new File("src/Scenes/2..png");
                Image two = new Image(second.toURI().toString());
                picture.setImage(two);
            }
            /*
              This is the profile picture for the first level
             */
            else{
                File third = new File("src/Scenes/3..png");
                Image three = new Image(third.toURI().toString());
                picture.setImage(three);
            }
            /*
              Displays the information of the user
             */
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