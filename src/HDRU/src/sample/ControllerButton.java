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
    public TextField textField;
    public TextField email;
    public TextField password;
    public TextField user_reg;
    public TextField email_reg;
    public TextField birthyear_reg;
    public TextField pass_reg;
    public TextField confirm_reg;


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

    public void reg() {
        Connection connection = null;
        Statement statement = null;
        Cleaner cleaner = new Cleaner();

        int birthyear = 0;
        String password = null;

        if(birthyear_reg.getText() != null){
            String getYear = birthyear_reg.getText();
            birthyear = Integer.parseInt(getYear);
        }

        if(pass_reg.getText().equals(confirm_reg.getText())){
            password = pass_reg.getText();
        }

        String sql = "INSERT INTO Player VALUES(\"" + user_reg.getText() + "\",  \"" + email_reg.getText() + "\", " + 0 + ", " + false + ", \"" + password + "\", " + true + ", " + birthyear + ")";

        try {
            ConnectionClass connectionClass = new ConnectionClass();
            connection = connectionClass.getConnection();
            statement = connection.createStatement();
            statement.executeUpdate(sql);
            statement.close();
            connection.close();
        }catch (SQLException e) {
            e.printStackTrace();
        }finally {
            cleaner.closeStatement(statement);
            cleaner.closeConnection(connection);
        }
    }

    public void playerLogin(ActionEvent event) throws IOException{
        ConnectionClass connectionClass = new ConnectionClass();
        Connection connection = connectionClass.getConnection();
        ResultSet rs = null;


		String sql = "SELECT password FROM Player WHERE email ='" + email.getText() + "';";
		try {
		    Cleaner cleaner = new Cleaner();
			Statement statement = connection.createStatement();
            rs = statement.executeQuery(sql);

            rs.next();

            String realPassword = rs.getString("password");

            if(realPassword.equals(password.getText())){
                cleaner.closeResult(rs);
                cleaner.closeStatement(statement);
                cleaner.closeConnection(connection);
                super.change(event, "Game.fxml");
            }
            else{
                cleaner.closeResult(rs);
                cleaner.closeStatement(statement);
                cleaner.closeConnection(connection);
                super.change(event, "Info.fxml"); //her skal en pop-up komme
            }

		}catch (Exception e){
			e.printStackTrace();
		}
	}
}
