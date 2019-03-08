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

public class ControllerRegister {

    @FXML
    public TextField user_reg;
    public TextField email_reg;
    public TextField birthyear_reg;
    public TextField pass_reg;
    public TextField confirm_reg;

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
        notNull();
        checkPassword();
        String sql = "INSERT INTO Player VALUES(\"" + user_name + "\",  \"" + email_adress + "\", " + 0 + ", " + 0 + ", \"" + password + "\",  \"" + stringSalt + "\", " + 0 + ", " + birthyear + ")";
        try {
            ConnectionClass connectionClass = new ConnectionClass();
            connection = connectionClass.getConnection();
            statement = connection.createStatement();
            statement.executeUpdate(sql);
        }catch (SQLException e) {
            e.printStackTrace();
        }finally {
            cleaner.close(statement, null, connection);
        }
    }

    public void sceneHome(ActionEvent event) { //hjemknapp
        sceneChanger.change(event, "Main.fxml"); //bruker super-metode
    }

    public void notNull() {
        if(user_reg.getText() != null && email_reg.getText() != null && birthyear_reg.getText() != null){
            user_name = user_reg.getText();
            email_adress = email_reg.getText();
            String getYear = birthyear_reg.getText();
            birthyear = Integer.parseInt(getYear);
        } /*else {
            sceneChanger.changeVisibility(); her skal det skje noe
        }*/
    }

    public void checkPassword(){
        if(pass_reg.getText().equals(confirm_reg.getText())){
            String inputPassword = pass_reg.getText();
            HashSalt hashedSaltedPass = new HashSalt();
            salt = hashedSaltedPass.createSalt();
            stringSalt = hashedSaltedPass.encodeHexString(salt);
            password = hashedSaltedPass.genHashSalted(inputPassword, salt);
        }
    }
}
