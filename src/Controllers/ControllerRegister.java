package Controllers;

import Connection.ConnectionPool;
import Connection.Cleaner;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import java.util.regex.Pattern;
import java.sql.*;

public class ControllerRegister {

    @FXML
    public TextField user_reg;
    public TextField email_reg;
    public TextField birthyear_reg;
    public TextField pass_reg;
    public TextField confirm_reg;
    public Label errorMessageEmpty;
    public Label errorMessageBirthyear;
    public Label errorMessageEmailTaken;
    public Label errorMessageUserTaken;
    public Label errorMessageEmailInvalid;
    public Label errorMessagePassword;
    public RadioButton btnMale;
    public RadioButton btnFemale;
    public ToggleGroup gender;

    private String user_name;
    private String email_adress;
    private int birthyear;
    private String password;
    private byte[] salt;
    private String stringSalt;

    public void reg(ActionEvent event) {
        Connection connection = null;
        PreparedStatement pstmt = null;
        if (!notNull()) {
            visible(errorMessageEmpty);
        } else if (userExists()) {
            visible(errorMessageUserTaken);
        } else if (emailExists()) {
            visible(errorMessageEmailTaken);
        } else if (!checkEmail()) {
            visible(errorMessageEmailInvalid);
        } else if (checkBirthyear()) {
            visible(errorMessageBirthyear);
        } else if (!checkPassword()) {
            visible(errorMessagePassword);
        } else if(chooseGender() == -1){
            visible(errorMessageEmpty);
        }else{
            int gender = chooseGender();
            int ol = 0;
            int startPoints = 0;
            String sql = "INSERT INTO Player(username, email, points, online, password, salt, female, birthyear)VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
            try {
                connection = ConnectionPool.getConnection();
                pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, user_name);
                pstmt.setString(2, email_adress);
                pstmt.setInt(3, startPoints);
                pstmt.setInt(4, ol);
                pstmt.setString(5, password);
                pstmt.setString(6, stringSalt);
                pstmt.setInt(7, gender);
                pstmt.setInt(8, birthyear);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                Cleaner.close(pstmt, null, connection);
                ChangeScene.change(event, "/Scenes/Main.fxml");
            }
        }
    }

    public void sceneHome(ActionEvent event) { //hjemknapp
        ChangeScene.change(event, "/Scenes/Main.fxml");
    }

    public boolean notNull() {
        if(user_reg.getText().isEmpty() || email_reg.getText().isEmpty() || birthyear_reg.getText().isEmpty() || pass_reg.getText().isEmpty() || confirm_reg.getText().isEmpty()) {
            return false;
        } else {
            user_name = user_reg.getText().toLowerCase();
            email_adress = email_reg.getText().toLowerCase();
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
        Connection connection = null;
        ResultSet rsUser = null;
        PreparedStatement pstmt = null;

        try {
            connection = ConnectionPool.getConnection();
            String username = "SELECT username FROM Player WHERE username = ?;";
            pstmt = connection.prepareStatement(username);
            pstmt.setString(1, user_reg.getText().toLowerCase());
            rsUser = pstmt.executeQuery();

            if(rsUser.next()){
                return true;
            }else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }finally {
            Cleaner.close(pstmt, rsUser, connection);
        }
    }

    public boolean emailExists(){
        Connection connection = null;
        ResultSet rsEmail = null;
        PreparedStatement pstmt = null;

        try {
            connection = ConnectionPool.getConnection();
            String email = "SELECT email FROM Player WHERE email = ?;";
            pstmt = connection.prepareStatement(email);
            pstmt.setString(1, email_reg.getText().toLowerCase());
            rsEmail = pstmt.executeQuery();

            if(rsEmail.next()){
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }finally {
            Cleaner.close(pstmt, rsEmail, connection);
        }
    }
    public int chooseGender(){
        if(this.btnMale.isSelected()){
            return 0;
        }
        else if(this.btnFemale.isSelected()){
            return 1;
        }
        return -1;
    }

    public boolean checkEmail(){
        String getEmail = email_reg.getText();
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (getEmail == null)
            return false;
        return pat.matcher(getEmail).matches();
    }

    public boolean checkBirthyear(){
        try{
            String getYear = birthyear_reg.getText();
            birthyear = Integer.parseInt(getYear);
        }catch (NumberFormatException e){
            //e.printStackTrace();
            return true;
        }
        if(birthyear < 1903 || birthyear > 2019){
            return true;
        }else{
            return false;
        }
    }

    private void visible(Label label){
        errorMessageEmpty.setVisible(false);
        errorMessageBirthyear.setVisible(false);
        errorMessageEmailTaken.setVisible(false);
        errorMessageUserTaken.setVisible(false);
        errorMessageEmailInvalid.setVisible(false);
        errorMessagePassword.setVisible(false);
        label.setVisible(true);
    }
}
