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

    // Global variables for connection
    private Connection connection = null;
    private PreparedStatement statement = null;

    // Elements from fxml
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
     * @param event is a necessary parameter which is used in a method from the class ChangeScene.
     */
    public void sceneProfile(ActionEvent event) {
        ChangeScene.change(event, "/Scenes/Profile.fxml");
    }

    /**
     * A method when confirming a new e-mail adress.
     * It will check if the e-mail is valid or already taken.
     * It will the user's e-mail if the e-mail is valid.
     */
    public void emailConfirm(){
        // Update player's email and username prepared statement
        String input = "UPDATE Player SET email = ? WHERE username = ?;";

        try {
            // Set up connection and prepared statement
            connection = ConnectionPool.getConnection();
            statement = connection.prepareStatement(input);

            // Check if email is written in correctly (input validation)
            if (inputEmail.getText().isEmpty()) {
                setVisibility(errorMessage);
            }else if(!checkEmail()){
                setVisibility(errorMessageEmail);
            }else{
                // Add username and email that the user wrote in the text field
                statement.setString(1, inputEmail.getText());
                statement.setString(2, username);
                statement.executeUpdate();
                setVisibility(confirmed);
            }
        }
        catch(SQLException e){
            // If the update fails
            e.printStackTrace();
        }
        finally {
            // Close connection
            Cleaner.close(statement, null, connection);
        }
    }

    /**
     * A method for confirming a gender change by using RadioButtons.
     * Uses the method of chooseGender.
     */
    public void genderConfirm(){
        // Setting up the prepared statement
        String input = "UPDATE Player SET female = ? WHERE username = ?;";

        try{
            // Set up connection and prepared statement
            connection = ConnectionPool.getConnection();
            statement = connection.prepareStatement(input);

            // Check if gender is chosen
            if(chooseGender() == -1){
                setVisibility(errorMessage);
            }
            else{
                // Update player gender
                statement.setInt(1, chooseGender());
                statement.setString(2, username);
                statement.executeUpdate();
                setVisibility(confirmed);
            }
        }
        catch(SQLException e){
            // If update fails
            e.printStackTrace();
        }
        finally {
            // Close connection
            Cleaner.close(statement, null, connection);
        }
    }

    /**
     * A method when confirming a new password.
     * It checks if the inputed old password is correct,
     * if it is then it updates with the new password
     * It will check if the passwordfield is empty
     * If not, the password will be hashed and salted.
     * The password and the salt will then be stored in the database.
     */
    public void passwordConfirm(){
        // Set up result set and prepared statements for getting the password from the database and updating the password
        ResultSet rs = null;
        String input = "UPDATE Player SET password = ?, salt = ? WHERE username = ?";
        String sql = "SELECT password, salt FROM Player WHERE username = ?;";
        try {
            // Set up connection
            connection = ConnectionPool.getConnection();
            statement = connection.prepareStatement(sql);

            // Get password and salt from the database
            statement.setString(1, username);
            rs = statement.executeQuery();
            rs.next();

            // Hash and salt the inputed old password with the salt from the database
            String salt = rs.getString("salt");
            String realPassword = rs.getString("password");
            String inputPassword = inputOldPassword.getText();
            HashSalt hashedSaltedPass = new HashSalt();
            byte[] byteSalt = hashedSaltedPass.decodeHexString(salt);
            String hashedPassword = hashedSaltedPass.genHashSalted(inputPassword, byteSalt);

            // If the user inputed their real password, it changes it to the new password
            if (realPassword.equals(hashedPassword)) {
                // Hashes the new password with a new salt
                statement = connection.prepareStatement(input);
                String inPassword = inputNewPassword.getText();
                HashSalt hashedSaltedPas = new HashSalt();
                byte[] newSalt = hashedSaltedPas.createSalt();
                String stringSalt = hashedSaltedPass.encodeHexString(newSalt);
                String password = hashedSaltedPass.genHashSalted(inPassword, newSalt);

                // Updates the player with the new password and salt
                statement.setString(1, password);
                statement.setString(2, stringSalt);
                statement.setString(3, username);
                statement.executeUpdate();

                // Show feedback to user
                setVisibility(confirmed);

            } else if(inputNewPassword.getText().isEmpty()) {
                // If no new password was written in
                setVisibility(errorMessage);
            } else{
                // If the player inputed the wrong password for their old password
                setVisibility(wrongPassword);
            }
        }
        catch (SQLException e){
            // If the sql fails
            e.printStackTrace();
        }
        finally {
            // Closes the connection
            Cleaner.close(statement, rs, connection);
        }
    }

    /**
     * A private method for choosing a new gender.
     * @return returns 0 if male is selected, returns 1 if female is selected, returns -1 if none selected.
     */
    private int chooseGender(){
        if(this.btnMale.isSelected()){
            // Male selected
            return 0;
        }
        else if(this.btnFemale.isSelected()){
            // Female selected
            return 1;
        }
        // Nothing selected
        else return -1;
    }

    /**
     * A method when the confirming a new e-mail adress.
     * It will check if the e-mail is valid or already taken.
     * It will update the user's e-mail if the e-mail is valid.
     * @return if valid e-mail return true, else return false.
     */
    private boolean checkEmail(){
        // Set up variables connection objects
        Connection connection = null;
        ResultSet rsEmail = null;
        PreparedStatement pstmt = null;
        try {
            // Set up connection and prepared statement for getting players with the email entered
            connection = ConnectionPool.getConnection();
            String email = "SELECT email FROM Player WHERE email = ?;";
            pstmt = connection.prepareStatement(email);

            // Add inputed email to prepared statement and get users with the email from database
            String getEmail = inputEmail.getText();
            pstmt.setString(1, getEmail.toLowerCase());
            rsEmail = pstmt.executeQuery();

            // Regex for checking if the email is valid
            String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                    "[a-zA-Z0-9_+&*-]+)*@" +
                    "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                    "A-Z]{2,7}$";
            Pattern pat = Pattern.compile(emailRegex);

            // If email is not written or someone already uses that email it returns false
            if (getEmail == null || rsEmail.next()){return false;}

            // Return true if the email passes the regex validation and false if it is invalid
            return pat.matcher(getEmail).matches();
        } catch (SQLException e) {
            // If the sql fails
            e.printStackTrace();
            return false;
        }finally {
            // Close connection
            Cleaner.close(pstmt, rsEmail, connection);
        }
    }

    /**
     * A method for the different error messages.
     * Sets the parameter label visibility to true.
     * Sets the visibilty of all other messages to false.
     */
    private void setVisibility(Label label){
        // Sets all other messages to invisible
        errorMessage.setVisible(false);
        confirmed.setVisible(false);
        wrongPassword.setVisible(false);
        errorMessageEmail.setVisible(false);

        // Makes chosen label visible
        label.setVisible(true);
    }
}
