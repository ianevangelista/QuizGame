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

public class ControllerRegister extends ChangeScene{

    @FXML
    public TextField user_reg;
    public TextField email_reg;
    public TextField birthyear_reg;
    public TextField pass_reg;
    public TextField confirm_reg;

    public void reg() throws Exception {

        Connection connection = null;
        Statement statement = null;
        Cleaner cleaner = new Cleaner();

        int birthyear = 0;
        String password = null;
        byte[] salt = null;
        String stringSalt = null;

        if(birthyear_reg.getText() != null){
            String getYear = birthyear_reg.getText();
            birthyear = Integer.parseInt(getYear);
        }

        if(pass_reg.getText().equals(confirm_reg.getText())){
            String inputPassword = pass_reg.getText();
            HashSalt hashedSaltedPass = new HashSalt();
            salt = hashedSaltedPass.createSalt();
            stringSalt = hashedSaltedPass.encodeHexString(salt);
            password = hashedSaltedPass.genHashSalted(inputPassword, salt);
        }

        String sql = "INSERT INTO Player VALUES(\"" + user_reg.getText() + "\",  \"" + email_reg.getText() + "\", " + 0 + ", " + 0 + ", \"" + password + "\",  \"" + stringSalt + "\", " + 0 + ", " + birthyear + ")";

        try {
            ConnectionClass connectionClass = new ConnectionClass();
            connection = connectionClass.getConnection();
            statement = connection.createStatement();
            statement.executeUpdate(sql);
        }catch (SQLException e) {
            e.printStackTrace();
        }finally {
            cleaner.closeTwo(statement, connection);
        }
    }

    public void sceneHome(ActionEvent event) throws IOException { //hjemknapp
        super.change(event, "Main.fxml"); //bruker super-metode
    }
}
