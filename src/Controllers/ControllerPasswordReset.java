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


import static Controllers.ControllerHome.getUserName;

public class ControllerPasswordReset {
    @FXML
    public TextField email;

    Properties props = new Properties();
    Session session = Session.getDefaultInstance(props);
    MimeMessage message = new MimeMessage(session);
    Transport transport = null;

    private String passwordNew = newPassword();

    private Connection connection = null;
    private PreparedStatement statement = null;
    ResultSet rs = null;

    private byte[] salt;
    private String stringSalt;
    private String password;


    public void sendPassword(){

        String sendTo = email.getText();
        String host = "smtp.gmail.com";
        String username = "howdumbru.game@gmail.com";
        String emailpassword = "Junierkul";

        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.user", username);
        props.put("mail.smtp.password", emailpassword);
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", host);


        try{

            message.setFrom(new InternetAddress(username));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(sendTo));

            message.setSubject("How Dumb R U?");
            message.setText("Your new password to 'How Dumb R U?': " +
                    passwordNew +
                    "\n\nRemember to change it as soon as possible!" +
                    "\n\nFrom: How Dumb R U?");

            transport = session.getTransport("smtp");
            transport.connect(host, username, emailpassword);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();

        }
        catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Feilmelding");
        }

    }

    public void feedback(ActionEvent event){
        if(validateEmail()){
            sendPassword();
            setPassword();
        }
            ChangeScene.change(event, "/Scenes/Main.fxml");
    }

    public void sceneHome(ActionEvent event) { //back button
        if(getUserName() == null) {
            ChangeScene.change(event, "/Scenes/Main.fxml");
        }
        else{
            ChangeScene.change(event, "/Scenes/Game.fxml");
        }
    }


    private String newPassword(){
        Random ran = new Random();
        int number = ran.nextInt(9000) + 1000;
        return Integer.toString(number);
    }



    private void setPassword(){
        String input = "UPDATE Player SET password = ?, salt = ? WHERE email = ?";
        try {
            connection = ConnectionPool.getConnection();
            statement = connection.prepareStatement(input);

                HashSalt hashedSaltedPass = new HashSalt();
                salt = hashedSaltedPass.createSalt();
                stringSalt = hashedSaltedPass.encodeHexString(salt);
                password = hashedSaltedPass.genHashSalted(passwordNew, salt);

                statement.setString(1, password);
                statement.setString(2, stringSalt);
                statement.setString(3, email.getText());
                statement.executeUpdate();

        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            Cleaner.close(statement, rs, connection);
        }
    }


    public boolean validateEmail() {
        String sql = "SELECT username FROM Player WHERE email = ?;";
        try {
            connection = ConnectionPool.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, email.getText());
            rs = statement.executeQuery();

            //if user does not exist
            if (!(rs.next())) {
                return false;
            } else {
                return true;
            }
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }finally{
            Cleaner.close(statement, rs, connection);
        }
    }

}
