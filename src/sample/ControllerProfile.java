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

    private static String username = getUserName();

    private byte[] salt1;
    private String stringSalt;
    private String password;
    private boolean checking = false;

    private Connection connection = null;
    private PreparedStatement statement = null;

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
    public Label errorMessage;
    public Label confirmed;
    public Label wrongPassword;
    public TextField inputEmail;
    public PasswordField inputOldPassword;
    public PasswordField inputNewPassword;
    public RadioButton btnMale;
    public RadioButton btnFemale;
    public ToggleGroup gender;

    public void initialize() {
        if(!checking) {
            choosePic();
            checking = true;
        }
    }

    public void sceneGame(ActionEvent event) { //hjemknapp
        ChangeScene.change(event, "Game.fxml");
    }

    public void edit(ActionEvent event){
        System.out.println("test");
        ChangeScene.change(event, "EditProfile.fxml");
    }

    public void choosePic(){

        ResultSet rs = null;

        String sql = "SELECT points, email, gamesWon, gamesLost FROM Player WHERE username = ?;";

        File first = new File("src/sample/1..png");
        File second = new File("src/sample/2..png");
        File third = new File("src/sample/3..png");

        try{
            connection = ConnectionPool.getConnection();
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

    public void emailConfirm(){

        String input = "UPDATE Player SET email = ? WHERE username = ?";

        try{
            connection = ConnectionPool.getConnection();

            statement = connection.prepareStatement(input);
            statement.setString(2,username);

            if(inputEmail.getText() == null){
                errorMessage.setVisible(true);
            }
            else{
                statement.setString(1, inputEmail.getText());
                confirmed.setVisible(true);
            }
            statement.executeUpdate(input);
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        finally {
            Cleaner.close(statement, null, connection);
        }
    }

    public void genderConfirm(){

        String input = "UPDATE Player SET gender = ? WHERE username = ?";

        try{
            connection = ConnectionPool.getConnection();

            statement = connection.prepareStatement(input);
            statement.setString(2,username);

            if(chooseGender() == -1){
                errorMessage.setVisible(true);
            }
            else{
                statement.setInt(1, chooseGender());
                confirmed.setVisible(true);
            }
            statement.executeUpdate(input);
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        finally {
            Cleaner.close(statement, null, connection);
        }
    }

    public void passwordConfirm(){

        ResultSet rs = null;

        String input = "UPDATE Player SET password = ? WHERE username = ?";
        String sql = "SELECT password, salt FROM Player WHERE username = ?;";
        try {
            connection = ConnectionPool.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            rs = statement.executeQuery();

            if (!(rs.next())) {
                wrongPassword.setVisible(true);
            }
            else {
                String salt = rs.getString("salt");
                String realPassword = rs.getString("password");
                String inputPassword = inputOldPassword.getText();

                HashSalt hashedSaltedPass = new HashSalt();
                byte[] byteSalt = hashedSaltedPass.decodeHexString(salt);
                String hashedPassword = hashedSaltedPass.genHashSalted(inputPassword, byteSalt);

                if (realPassword.equals(hashedPassword)) {

                    statement = connection.prepareStatement(input);

                    String inPassword = inputNewPassword.getText();
                    HashSalt hashedSaltedPas = new HashSalt();
                    salt1 = hashedSaltedPas.createSalt();
                    stringSalt = hashedSaltedPass.encodeHexString(salt1);
                    password = hashedSaltedPass.genHashSalted(inPassword, salt1);

                    statement.setString(1, password);
                    statement.setString(2, username);
                    statement.executeUpdate();

                    confirmed.setVisible(true);
                } else {
                    wrongPassword.setVisible(true);
                }
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {

        }
    }

    public int chooseGender(){
        if(this.gender.getSelectedToggle().equals(this.btnMale)){
            return 0;
        }
        else if(this.gender.getSelectedToggle().equals(this.btnFemale)){
            return 1;
        }
        else return -1;
    }

}
