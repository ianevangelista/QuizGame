package sample;

import Connection.ConnectionClass;
import Connection.Cleaner;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.TextField;
import javafx.scene.control.Label;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;


public class ControllerHome {

    private String userName;

    @FXML
    public TextField username;
    public TextField password;
    public Label visibility;

    public ChangeScene sceneChanger = new ChangeScene();

    public void sceneInfo(ActionEvent event) { //trykker på infoknapp
       sceneChanger.change(event, "Info.fxml"); //bruker super-metode
    }

    public void sceneHome(ActionEvent event) { //feedback knapp
        sceneChanger.change(event, "Main.fxml"); //bruker super-metode
    }

    public void register(ActionEvent event) { //trykker registrer
        sceneChanger.change(event, "Register.fxml"); //bruker super-metode
    }

    public void feedback(ActionEvent event) { //feedback knapp
        sceneChanger.change(event, "Feedback.fxml"); //bruker super-metode
    }

    public void playerLogin(ActionEvent event) {
        Connection connection = null;
        Cleaner cleaner = new Cleaner();
        ResultSet rs = null;
        Statement statement = null;

		String sql = "SELECT username, password, salt FROM Player WHERE username ='" + username.getText() + "';";
		try {
		    connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            rs = statement.executeQuery(sql);

            if (!(rs.next())) {
                sceneChanger.changeVisibility(true, visibility); //her skal en pop-up komme
            }
            else if (username.getText().isEmpty() || password.getText().isEmpty()) {
                sceneChanger.changeVisibility(true, visibility); //her skal en pop-up komme
            }
            else {
                String salt = rs.getString("salt");
                String realPassword = rs.getString("password");
                String inputPassword = password.getText();

                HashSalt hashedSaltedPass = new HashSalt();
                byte[] byteSalt = hashedSaltedPass.decodeHexString(salt);
                String hashedPassword = hashedSaltedPass.genHashSalted(inputPassword, byteSalt);

                if (realPassword.equals(hashedPassword)) {
                    setUserName(username.getText());
                    sceneChanger.change(event, "Game.fxml");
                } else {
                    sceneChanger.changeVisibility(true, visibility); //her skal en pop-up komme
                }
            }
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                cleaner.close(statement, rs, connection);
            }
	}

    private void setUserName(String username){
        this.userName = username;
    }

    public String getUserName(){
        return userName;
    }


}
