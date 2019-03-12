package sample;

import Connection.ConnectionPool;
import Connection.Cleaner;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

import java.sql.*;

public class ControllerRegister {

    @FXML
    public TextField user_reg;
    public TextField email_reg;
    public TextField birthyear_reg;
    public TextField pass_reg;
    public TextField confirm_reg;
    public Label visibility;
    public RadioButton btnMale;
    public RadioButton btnFemale;
    public ToggleGroup gender;


    private ChangeScene sceneChanger = new ChangeScene();

    private String user_name;
    private String email_adress;
    private int birthyear;
    private String password;
    private byte[] salt;
    private String stringSalt;

    public void reg(ActionEvent event) {
        Connection connection = null;
        PreparedStatement pstmt = null;
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

        else if(chooseGender() == -1) {
            System.out.println("ingenting skal registreres");
            sceneChanger.changeVisibility(true, visibility);
        }

        else{
            int gender = chooseGender();
            int ol = 1;
            int startPoints = 0;
            String sql = "INSERT INTO Player(username, email, points, online, password, salt, female, birthyear)VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
            try {
                //ConnectionClass connectionClass = new ConnectionClass();
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
                sceneChanger.change(event, "Game.fxml");
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
            try{
                user_name = user_reg.getText().toLowerCase();
                email_adress = email_reg.getText().toLowerCase();
                String getYear = birthyear_reg.getText();
                birthyear = Integer.parseInt(getYear);
            }catch (NumberFormatException e){
                //e.printStackTrace();
                return false;
            }
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
        Statement statementUser = null;
        Statement statementEmail = null;
        ResultSet rsUser = null;
        ResultSet rsEmail = null;
        String username = "SELECT username FROM Player WHERE username ='" + user_reg.getText() + "';";
        String email = "SELECT email FROM Player WHERE username ='" + email_reg.getText() + "';";

        try {
            connection = ConnectionPool.getConnection();
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
            Cleaner.close(statementUser, rsUser, null);
            Cleaner.close(statementEmail, rsEmail, connection);
        }
        return true;
    }

    public int chooseGender(){
        if(this.gender.getSelectedToggle().equals(this.btnMale)){
            System.out.println("Male");
            return 0;
        }
        else if(this.gender.getSelectedToggle().equals(this.btnFemale)){
            System.out.println("Female");
            return 1;
        }
        else return -1;
    }
}
