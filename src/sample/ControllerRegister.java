package sample;

import Connection.ConnectionClass;
import Connection.Cleaner;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ControllerRegister {

    @FXML
    public TextField user_reg;
    public TextField email_reg;
    public TextField birthyear_reg;
    public TextField pass_reg;
    public TextField confirm_reg;
    public Label visibility;


    public ChangeScene sceneChanger = new ChangeScene();

    public String user_name;
    public String email_adress;
    public int birthyear;
    public String password;
    public byte[] salt;
    public String stringSalt;

    public void reg() {
        Cleaner cleaner = new Cleaner();
        Connection connection = null;
        Statement statement = null;
        if(!notNull()) {
            System.out.println("ingenting skal registreres");
            sceneChanger.changeVisibility(true, visibility);
        }
        else if(!checkPassword()) {
            System.out.println("ingenting skal registreres");
            sceneChanger.changeVisibility(true, visibility);
        }
        else if(userExists()) {
            System.out.println("ingenting skal registreres");
            sceneChanger.changeVisibility(true, visibility);
        }

        else{
            String sql = "INSERT INTO Player VALUES(\"" + user_name + "\",  \"" + email_adress + "\", " + 0 + ", " + 0 + ", \"" + password + "\",  \"" + stringSalt + "\", " + 0 + ", " + birthyear + ")";
            try {
                ConnectionClass connectionClass = new ConnectionClass();
                connection = connectionClass.getConnection();
                statement = connection.createStatement();
                statement.executeUpdate(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                cleaner.close(statement, null, connection);
            }
        }
    }

    public void sceneHome(ActionEvent event) { //hjemknapp
        sceneChanger.change(event, "Main.fxml"); //bruker super-metode
    }

    public boolean notNull() {
        if(user_reg.getText().isEmpty() || email_reg.getText().isEmpty() || birthyear_reg.getText().isEmpty() || pass_reg.getText().isEmpty() || confirm_reg.getText().isEmpty()) {
            return false;
        } else {
            user_name = user_reg.getText();
            email_adress = email_reg.getText();
            String getYear = birthyear_reg.getText();
            birthyear = Integer.parseInt(getYear);
            return true;
        }
    }

    public boolean checkPassword(){
        if(pass_reg.getText().equals(confirm_reg.getText()) && (!(pass_reg.getText().isEmpty() && confirm_reg.getText().isEmpty()))){
            String inputPassword = pass_reg.getText();
            HashSalt hashedSaltedPass = new HashSalt();
            salt = hashedSaltedPass.createSalt();
            stringSalt = hashedSaltedPass.encodeHexString(salt);
            password = hashedSaltedPass.genHashSalted(inputPassword, salt);
            return true;
        }else {
            return false;
        }
    }

    public boolean userExists(){
        Cleaner cleaner = new Cleaner();
        ConnectionClass connectionClass = new ConnectionClass();
        Connection connection = null;
        Statement statementUser = null;
        Statement statementEmail = null;
        ResultSet rsUser = null;
        ResultSet rsEmail = null;
        String username = "SELECT username FROM Player WHERE username ='" + user_reg.getText() + "';";
        String email = "SELECT email FROM Player WHERE username ='" + email_reg.getText() + "';";

        try {
            connection = connectionClass.getConnection();
            statementUser = connection.createStatement();
            statementEmail = connection.createStatement();
            rsUser = statementUser.executeQuery(username);
            rsEmail = statementEmail.executeQuery(email);
            if(rsUser.next() || rsEmail.next()){
                return true;
            }else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            cleaner.close(statementUser, rsUser, null);
            cleaner.close(statementEmail, rsEmail, connection);
        }
        return true;
    }
}
