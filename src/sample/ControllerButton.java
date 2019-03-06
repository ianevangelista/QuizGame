package sample;

import Connection.ConnectionClass;
import Connection.Cleaner;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;


public class ControllerButton extends ChangeScene{

    @FXML
    public TextField email;
    public TextField password;


    public void sceneInfo(ActionEvent event) throws IOException { //trykker på infoknapp
       super.change(event, "Info.fxml"); //bruker super-metode
    }

    public void sceneHome(ActionEvent event) throws IOException { //hjemknapp
        super.change(event, "Main.fxml"); //bruker super-metode
    }

    public void register(ActionEvent event) throws IOException { //trykker registrer
        super.change(event, "Register.fxml"); //bruker super-metode
    }

    public void feedback(ActionEvent event) throws IOException { //feedback knapp
        super.change(event, "Feedback.fxml"); //bruker super-metode
    }

    public void playerLogin(ActionEvent event) throws IOException{
        ConnectionClass connectionClass = new ConnectionClass();
        Connection connection = connectionClass.getConnection();
        Cleaner cleaner = new Cleaner();
        ResultSet rs = null;
        Statement statement = null;


		String sql = "SELECT password, salt FROM Player WHERE email ='" + email.getText() + "';";
		try {
			statement = connection.createStatement();
            rs = statement.executeQuery(sql);
            rs.next();

            String salt = rs.getString("salt");
            String realPassword = rs.getString("password");
            String inputPassword = password.getText();

            HashSalt hashedSaltedPass = new HashSalt();
            byte[] byteSalt = hashedSaltedPass.decodeHexString(salt);
            String hashedPassword = hashedSaltedPass.genHashSalted(inputPassword, byteSalt);

            if(realPassword.equals(hashedPassword)){
                super.change(event, "Game.fxml");
            }
            else{
                System.out.println(hashedPassword);
                super.change(event, "Info.fxml"); //her skal en pop-up komme
            }

		}catch (Exception e){
			e.printStackTrace();
		}finally {
            cleaner.closeThree(statement, rs, connection);
        }
	}
}
