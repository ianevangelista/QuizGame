package Controllers;

import Connection.Cleaner;
import Connection.ConnectionPool;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * The class ControllerPasswordReset is the page where the users password can be reset.
 * It will appear when you press the forgot password button on the Login page.
 */
public class ControllerPasswordReset {

    //Fxml element for the textfield
    @FXML
    public TextField email;

    //set up for java email
    Properties props = new Properties();
    Session session = Session.getDefaultInstance(props);
    MimeMessage message = new MimeMessage(session);
    Transport transport = null;

    private String passwordNew = newPassword();

    //Set up for connection
    private Connection connection = null;
    private PreparedStatement statement = null;
    ResultSet rs = null;

    //Set up for hashing of password
    private byte[] salt;
    private String stringSalt;
    private String password;


    /**
     * This methods sends the new password to the user's email account.
     * The first thing happening is establishing the receiver and the host we send from (host, username, password)
     * After this, we start to put the properties, so we can load the stream.
     * Then we start to formulate the mail and add the new password to the text.
     * Last, but not least, the mail is sent and the transaction is closed.
     */

    public boolean sendPassword(){

        // Set up email details for the HDRU gmail account
        String sendTo = email.getText();
        String host = "smtp.gmail.com";
        String username = "howdumbru.game@gmail.com";
        String emailpassword = "Junierkul";

        // Set up properties for the email connection
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.user", username);
        props.put("mail.smtp.password", emailpassword);
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", host);


        try{

            // Add email of the user sending the feedback and set recepient as the HDRU gmail address
            message.setFrom(new InternetAddress(username));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(sendTo));

            // Set email subject and contents to the feedback
            message.setSubject("How Dumb R U?");
            message.setText("Your new password to 'How Dumb R U?': " +
                    passwordNew +
                    "\n\nRemember to change it as soon as possible!" +
                    "\n\nFrom: How Dumb R U?");

            // Send the email
            transport = session.getTransport("smtp");
            transport.connect(host, username, emailpassword);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();

            return true;

        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * This method changes scene to Main and execute the methods sendPassword and setPassword if validateEmail() is true.
     * @param event is a necessary parameter which is used in a method from the class ChangeScene.
     */
    public void feedback(ActionEvent event){
        // Sends mail to the user with the new password if the email is in the database and updates
        if(validateEmail()){
            if(sendPassword()){
                setPassword();
            }
        }
        ChangeScene.change(event, "/Scenes/Main.fxml");
    }

    /**
     * This method executes when you press the back button.
     * @param event is a necessary parameter which is used in a method from the class ChangeScene.
     */
    public void sceneHome(ActionEvent event) { //back button
            ChangeScene.change(event, "/Scenes/Main.fxml");
    }

    /**
     * This method generates a new Password consisting of a 4 digit random number.
     * Then it converts the int to a String for later usage.
     * @return String with new password
     */
    private String newPassword(){
        Random ran = new Random();
        // Creates a random number between 1000 and 9999.
        int number = ran.nextInt(9000) + 1000;
        return Integer.toString(number);
    }


    /**
     * This method hashes and salts the new password generated in newPassword()..
     * The password and the salt will then be stored in the database.
     */
    private void setPassword(){
        String input = "UPDATE Player SET password = ?, salt = ? WHERE email = ?";
        try {
            // Establishing connection and the prepared statement
            connection = ConnectionPool.getConnection();
            statement = connection.prepareStatement(input);

            // Hashing and salting the new password
            HashSalt hashedSaltedPass = new HashSalt();
                salt = hashedSaltedPass.createSalt();
                stringSalt = hashedSaltedPass.encodeHexString(salt);
                password = hashedSaltedPass.genHashSalted(passwordNew, salt);

                statement.setString(1, password);
                statement.setString(2, stringSalt);
                statement.setString(3, email.getText());
                // Updating the new password and salt in the database
                statement.executeUpdate();

        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            Cleaner.close(statement, rs, connection);
        }
    }

    /**
     * This method checks if the email from the textField matches with a user in the database.
     * @return true if a user with the chosen email exists, and false if not.
     */
    public boolean validateEmail() {
        String sql = "SELECT username FROM Player WHERE email = ?;";
        try {
            // Set up connection and prepared statement
            connection = ConnectionPool.getConnection();
            statement = connection.prepareStatement(sql);

            // Add inputted email to prepared statement and execute
            statement.setString(1, email.getText());
            rs = statement.executeQuery();

            // If user does not exist
            if (!(rs.next())) {
                return false;
            } else {
                return true;
            }
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }finally{
            // Close connection
            Cleaner.close(statement, rs, connection);
        }
    }

}
