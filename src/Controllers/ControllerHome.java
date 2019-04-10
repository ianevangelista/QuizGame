package Controllers;

import Connection.Cleaner;
import Connection.ConnectionPool;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The class ControllerHome is the starting page which displays the log in system.
 * It will displays top 5 players with the highest points.
 */

public class ControllerHome {
    // Set up connection
    private Connection connection = null;
    private ResultSet rs = null;
    private PreparedStatement pstmt = null;
    private static String userName;

    // Fxml elements
    @FXML
    private TextField username;
    public TextField password;
    public Label visibility;

    /**
     * The method changes scene to Info and returns true.
     * @param event is a necessary parameter which is used in a method from the class ChangeScene.
     * @return true if scene is changed.
     */
    public boolean sceneInfo(ActionEvent event) {
        ChangeScene.change(event, "/Scenes/Info.fxml");
        return true;
    }

    /**
     * The method changes scene to ResetPassword and returns true.
     * @param event is a necessary parameter which is used in a method from the class ChangeScene.
     * @return true if scene is changed.
     */
    public boolean forgotPassword(ActionEvent event) {
        ChangeScene.change(event, "/Scenes/ResetPassword.fxml");
        return true;
    }

    /**
     * The method changes scene to Main and returns true.
     * @param event is a necessary parameter which is used in a method from the class ChangeScene.
     * @return true if scene is changed.
     */
    public boolean sceneHome(ActionEvent event) {
        ChangeScene.change(event, "/Scenes/Main.fxml");
        return true;
    }

    /**
     * The method changes scene to Register and returns true.
     * @param event is a necessary parameter which is used in a method from the class ChangeScene.
     * @return true if scene is changed.
     */
    public boolean register(ActionEvent event) {
        ChangeScene.change(event, "/Scenes/Register.fxml");
        return true;
    }

    /**
     * The method changes scene to Feedback and returns true.
     * @param event is a necessary parameter which is used in a method from the class ChangeScene.
     * @return true if scene is changed.
     */
    public boolean feedback(ActionEvent event) {
        ChangeScene.change(event, "/Scenes/Feedback.fxml");
        return true;
    }

    /**
     * The method gives user the option to press enter on the keyboard rather than the button.
     * It will then use the playerLogin-method.
     * @param event is a necessary parameter which is used in a method from the class ChangeScene.
     */
    public void enter(ActionEvent event) {
        // When a button is pressed in the password field it triggers this event
        password.setOnKeyPressed(e -> {
            // If the button pressed is enter, the user attempts to log in
            if (e.getCode() == KeyCode.ENTER) {
                playerLogin(event);
            }
        });
    }

    /**
     * The method checks the username and password of the user.
     * Uses the method validateLogin.
     * Displays an error-message if there is no match or if a field is empty.
     * @param event is a necessary parameter which is used in a method from the class ChangeScene.
     * @return true login is validated.
     */
    public boolean playerLogin(ActionEvent event) {
        // Get input
        String inputUsername = username.getText();
        String inputPassword = password.getText();

        // Checks if there is a input
        if (inputUsername.isEmpty() || inputPassword.isEmpty()) {
            // Error message to user that either password or username is empty
            visibility.setVisible(true);
            return false;
        }

        // Uses method validateLogin to check if username and password is correct
        if(validateLogin(inputUsername, inputPassword)) {
            // If the login is valid it changes scene to the
            ChangeScene.change(event, "/Scenes/Game.fxml");
            return true;
        }

        // If validateLogin returns false, an errormessage is shown
        visibility.setVisible(true);
        return false;
	}

    /**
     * The method checks if the username matches the password.
     * If the user does not exist an error-message will appear.
     * If the user is already logged in an error-message will appear.
     * It will check salted and hashed password to the password which is written.
     * @param inputUsername is a parameter which is used to identify the user.
     * @param inputPassword is a parameter which is used to compare the password to the user.
     * @return true if username and password is validated.
     */
	public boolean validateLogin(String inputUsername, String inputPassword) {
        String sql = "SELECT username, online, password, salt FROM Player WHERE username = ?;";
        try {
            // Setting up connection
            connection = ConnectionPool.getConnection();
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, inputUsername);
            rs = pstmt.executeQuery();

            // If user does not exist
            if (!(rs.next())) return false;

            // If user is online
            else if (rs.getBoolean("online")) { return false; }

            // If the user is not online
            else {
                // Get salt and hashed & salted password from the database
                String salt = rs.getString("salt");
                String realPassword = rs.getString("password");

                // Hash input password and add the salt from the database
                HashSalt hashedSaltedPass = new HashSalt();
                byte[] byteSalt = hashedSaltedPass.decodeHexString(salt);
                String hashedPassword = hashedSaltedPass.genHashSalted(inputPassword, byteSalt);

                // If hashed input matches the password in the database, it logs in
                if (realPassword.equals(hashedPassword)) {
                    setUserName(inputUsername);
                    Logout.logIn();
                    return true;
                }
            }
            return false;
        } catch(SQLException sqle){
            // Database access error
            System.out.println("Database access error");
            sqle.printStackTrace();
            return false;
        } catch (Exception e) {
            // If something else goes wrong
            e.printStackTrace();
            return false;
        }finally{
            Cleaner.close(pstmt, rs, connection);
        }
    }

    /**
     * The method will set the username to identify the user.
     * @param username is a parameter which is used to identify the user.
     * @return the username after setting the username.
     */
    public static String setUserName(String username){
        userName = username;
        return userName;
    }

    /**
     * The method will set the username to null.
     */
    public static void setUserNull(){ userName = null; }

    /**
     * The method will return the username of the user.
     * @return the username.
     */
    public static String getUserName(){
        return userName;
    }
}