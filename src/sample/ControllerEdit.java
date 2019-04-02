package sample;

import Connection.Cleaner;
import Connection.ConnectionPool;
import javafx.event.ActionEvent;
import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

import static sample.ControllerHome.getUserName;

public class ControllerEdit {

    private byte[] newSalt;
    private String stringSalt;
    private String password;

    private String username = getUserName();

    private Connection connection = null;
    private PreparedStatement statement = null;

    public Button confirmEmail;
    public Button confirmGender;
    public Button confirmPassword;
    public Label errorMessage;
    public Label confirmed;
    public Label errorMessageEmail;
    public Label wrongPassword;
    public TextField inputEmail;
    public PasswordField inputOldPassword;
    public PasswordField inputNewPassword;
    public RadioButton btnMale;
    public RadioButton btnFemale;
    public ToggleGroup gender;

    public void sceneProfile(ActionEvent event) { //hjemknapp
        ChangeScene.change(event, "Profile.fxml");
    }

    public void emailConfirm(){

        String input = "UPDATE Player SET email = ? WHERE username = ?;";

        try {
            connection = ConnectionPool.getConnection();

            statement = connection.prepareStatement(input);
            statement.setString(1, inputEmail.getText());
            statement.setString(2, username);

            if (inputEmail.getText().isEmpty()) {
                setVisibility(errorMessage);
            }else if(!checkEmail()){
                setVisibility(errorMessageEmail);
            }else{
                statement.executeUpdate();
                setVisibility(confirmed);
            }

        }
        catch(SQLException e){
            e.printStackTrace();
        }
        finally {
            Cleaner.close(statement, null, connection);
        }
    }

    public void genderConfirm(){

        String input = "UPDATE Player SET female = ? WHERE username = ?;";

        try{
            connection = ConnectionPool.getConnection();

            statement = connection.prepareStatement(input);
            statement.setInt(1, chooseGender());
            statement.setString(2, username);

            if(chooseGender() == -1){
                setVisibility(errorMessage);
            }
            else{
                statement.executeUpdate();
                setVisibility(confirmed);
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        finally {
            Cleaner.close(statement, null, connection);
        }
    }

    public void passwordConfirm(){

        ResultSet rs = null;

        String input = "UPDATE Player SET password = ?, salt = ? WHERE username = ?";
        String sql = "SELECT password, salt FROM Player WHERE username = ?;";
        try {
            connection = ConnectionPool.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            rs = statement.executeQuery();
            rs.next();

            String salt = rs.getString("salt");
            String realPassword = rs.getString("password");
            String inputPassword = inputOldPassword.getText();
            HashSalt hashedSaltedPass = new HashSalt();
            byte[] byteSalt = hashedSaltedPass.decodeHexString(salt);
            String hashedPassword = hashedSaltedPass.genHashSalted(inputPassword, byteSalt);

            if (realPassword.equals(hashedPassword)) {

                statement = connection.prepareStatement(input);

                String inPassword = inputNewPassword.getText();
                HashSalt hashedSaltedPas = new HashSalt();
                newSalt = hashedSaltedPas.createSalt();
                stringSalt = hashedSaltedPass.encodeHexString(newSalt);
                password = hashedSaltedPass.genHashSalted(inPassword, newSalt);

                statement.setString(1, password);
                statement.setString(2, stringSalt);
                statement.setString(3, username);
                statement.executeUpdate();

                setVisibility(confirmed);

            } else if(inputNewPassword.getText().isEmpty()) {
                setVisibility(errorMessage);
            } else{
                setVisibility(wrongPassword);
            }

        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            Cleaner.close(statement, rs, connection);
        }
    }

    public int chooseGender(){
        if(this.btnMale.isSelected()){
            return 0;
        }
        else if(this.btnFemale.isSelected()){
            return 1;
        }
        else return -1;
    }
    private boolean checkEmail(){
        String getEmail = inputEmail.getText();
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (getEmail == null)
            return false;
        return pat.matcher(getEmail).matches();
    }

    private void setVisibility(Label label){
        errorMessage.setVisible(false);
        confirmed.setVisible(false);
        wrongPassword.setVisible(false);
        errorMessageEmail.setVisible(false);
        label.setVisible(true);
    }
}
