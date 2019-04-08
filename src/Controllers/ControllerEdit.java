package Controllers;

import Connection.Cleaner;
import Connection.ConnectionPool;
import javafx.event.ActionEvent;
import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

import static Controllers.ControllerHome.getUserName;

/**
 * The class ControllerEdit is used when a editing the user's profile.
 */

public class ControllerEdit {

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

    /**
     * A method when the back button is pressed.
     * You will return to the previous page, the profile page.
     * @param event is a neccessary paramater which is used in a method from the class ChangeScene.
     */

    public void sceneProfile(ActionEvent event) { //hjemknapp
        ChangeScene.change(event, "/Scenes/Profile.fxml");
    }

    /**
     * A method when confirming a new e-mail adress.
     * It will check if the e-mail is valid or already taken.
     * It will the user's e-mail if the e-mail is valid.
     */
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

    /**
     * A method for confirming a gender change by using RadioButtons.
     * Uses the method of chooseGender.
     */
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

    /**
     * A method when confirming a new password.
     * It will check if the passwordfield is empty
     * If not, the password will be hashed and salted.
     * The password and the salt will then be stored in the database.
     */
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
                byte[] newSalt = hashedSaltedPas.createSalt();
                String stringSalt = hashedSaltedPass.encodeHexString(newSalt);
                String password = hashedSaltedPass.genHashSalted(inPassword, newSalt);

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

    /**
     * A private method for choosing a new gender.
     * Returns 0 if male is selected.
     * Returns 1 if female is selected.
     * Returns -1 if none selected.
     */
    private int chooseGender(){
        if(this.btnMale.isSelected()){
            return 0;
        }
        else if(this.btnFemale.isSelected()){
            return 1;
        }
        else return -1;
    }

    /**
     * A method when the confirming a new e-mail adress.
     * It will check if the e-mail is valid or already taken.
     * It will the user's e-mail if the e-mail is valid.
     */
    private boolean checkEmail(){

        Connection connection = null;
        ResultSet rsEmail = null;
        PreparedStatement pstmt = null;
        try {
            String getEmail = inputEmail.getText();
            connection = ConnectionPool.getConnection();
            String email = "SELECT email FROM Player WHERE email = ?;";
            pstmt = connection.prepareStatement(email);
            pstmt.setString(1, getEmail.toLowerCase());
            rsEmail = pstmt.executeQuery();

            String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                    "[a-zA-Z0-9_+&*-]+)*@" +
                    "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                    "A-Z]{2,7}$";
            Pattern pat = Pattern.compile(emailRegex);

            if (getEmail == null || rsEmail.next()){return false;}
            return pat.matcher(getEmail).matches();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }finally {
            Cleaner.close(pstmt, rsEmail, connection);
        }
    }

    /**
     * A method for the different error messages.
     * Either sets the visibilty of the components as true or false.
     */
    private void setVisibility(Label label){
        errorMessage.setVisible(false);
        confirmed.setVisible(false);
        wrongPassword.setVisible(false);
        errorMessageEmail.setVisible(false);
        label.setVisible(true);
    }
}
