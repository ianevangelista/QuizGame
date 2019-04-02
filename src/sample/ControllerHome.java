package sample;

import Connection.Cleaner;
import Connection.ConnectionPool;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ControllerHome {

    private Connection connection = null;
    private ResultSet rs = null;
    private PreparedStatement pstmt = null;
    private static String userName;

    @FXML
    private TextField username;
    public TextField password;
    public Label visibility;

    public boolean sceneInfo(ActionEvent event) { //trykker på infoknapp
        ChangeScene.change(event, "Info.fxml"); //bruker super-metode
        return true;
    }

    public boolean sceneHome(ActionEvent event) { //feedback knapp
        ChangeScene.change(event, "Main.fxml");
        return true;
    }

    public boolean register(ActionEvent event) { //trykker registrer
        ChangeScene.change(event, "Register.fxml");
        return true;
    }

    public boolean feedback(ActionEvent event) { //feedback knapp
        ChangeScene.change(event, "Feedback.fxml");
        return true;
    }

    public void enter(ActionEvent event) {
        password.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                playerLogin(event);
            }
        });
    }

    public boolean playerLogin(ActionEvent event) {
        //checks if there is a input
        if (username.getText().isEmpty() || password.getText().isEmpty()) {
            visibility.setVisible(true); //her skal en pop-up komme
            return false;
        }

        String inputUsername = username.getText();
        String inputPassword = password.getText();

        //uses method validateLogin to check if username and password is correct
        if(validateLogin(inputUsername, inputPassword)) {
            ChangeScene.change(event, "Game.fxml");
            return true;
        }

        //if validateLogin returns false, an errormessage is shown
        visibility.setVisible(true); //her skal en pop-up komme
        return false;
	}

	public boolean validateLogin(String inputUsername, String inputPassword) {
        String sql = "SELECT username, online, password, salt FROM Player WHERE username = ?;";
        try {
            connection = ConnectionPool.getConnection();
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, inputUsername);
            rs = pstmt.executeQuery();

            //if user does not exist
            if (!(rs.next())) { return false;
            }

            //if user is online
            else if (rs.getBoolean("online")) { return false; }

            else {
                String salt = rs.getString("salt");
                String realPassword = rs.getString("password");

                HashSalt hashedSaltedPass = new HashSalt();
                byte[] byteSalt = hashedSaltedPass.decodeHexString(salt);
                String hashedPassword = hashedSaltedPass.genHashSalted(inputPassword, byteSalt);

                if (realPassword.equals(hashedPassword)) {
                    setUserName(inputUsername);
                    Logout.logIn();
                    return true;
                }
            }
            return false;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }finally{
            Cleaner.close(pstmt, rs, connection);
        }
    }

    public void setUserName(String username){
        this.userName = username;
    }

    public static void setUserNull(){
        userName = null;
    }

    public static String getUserName(){
        return userName;
    }
}
