package sample;

import Connection.ConnectionClass;
import Connection.Cleaner;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.TextField;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;


public class ControllerButton extends ChangeScene{

    @FXML
    public TextField user_reg;
    public TextField email_reg;
    public TextField birthyear_reg;
    public TextField pass_reg;
    public TextField confirm_reg;


    public TextField email;
    public TextField password;

    public void sceneInfo(ActionEvent event) throws IOException {
       super.change(event, "Info.fxml"); //bruker super-metode
    }

    public void sceneHome(ActionEvent event) throws IOException {
        super.change(event, "Main.fxml"); //bruker super-metode
    }

    public void register(ActionEvent event) throws IOException {
        super.change(event, "Register.fxml"); //bruker super-metode
    }

    public void feedback(ActionEvent event) throws IOException {
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

		String sql = "SELECT password FROM Player WHERE email ='" + email.getText()  + "' AND password='" + password.getText() + "';";
		try {
			Statement statement = connection.createStatement();
            rs = statement.executeQuery(sql);
            
            if(rs != null){
                rs.close();
                statement.close();
                connection.close();
                super.change(event, "Game.fxml");
            }
            rs.close();
            statement.close();
            connection.close();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}
