package sample;

import java.util.Properties;
import java.util.regex.Pattern;
import javax.mail.*;
import javax.mail.internet.*;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;

import static sample.ControllerHome.getUserName;

public class ControllerEmail {

    @FXML
    public TextArea feedback;
    public TextField email;
    public Label errorMessageEmailInvalid;

    Properties props = new Properties();
    Session session = Session.getDefaultInstance(props);
    MimeMessage message = new MimeMessage(session);
    Transport transport = null;

    public void sendFeedback(){

        String sendTo = "howdumbru.game@gmail.com";
        String host = "smtp.gmail.com";
        String username = "howdumbru.game@gmail.com";
        String password = "Junierkul";

        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.user", username);
        props.put("mail.smtp.password", password);
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", host);

        try{

            message.setFrom(new InternetAddress(username));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(sendTo));

            message.setSubject("Feedback from How Dumb R U?");
            String melding = feedback.getText() + "\nSendt from: " + email.getText();
            message.setText(melding);

            transport = session.getTransport("smtp");
            transport.connect(host, username, password);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();

        }
        catch (MessagingException e) {
            e.printStackTrace();
        }

    }

    public void sendConfirm(){

        String sendTo = email.getText();
        String host = "smtp.gmail.com";
        String username = "howdumbru.game@gmail.com";
        String password = "Junierkul";

        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.user", username);
        props.put("mail.smtp.password", password);
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", host);

        try{

            message.setFrom(new InternetAddress(username));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(sendTo));

            message.setSubject("Received feedback from How Dumb R U?");
            message.setText("Feedback received! We will come back to you as soon as possible.\n" +
                    "\nHere's your feedback:\n" + feedback.getText() +
                    "\n\nFrom: How Dumb R U?");

            transport = session.getTransport("smtp");
            transport.connect(host, username, password);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();

        }
        catch (MessagingException e) {
            e.printStackTrace();
        }

    }

    public void feedback(ActionEvent event){
        if(!checkEmail()) {
            errorMessageEmailInvalid.setVisible(true);
        } else{
            sendFeedback();
            sendConfirm();
            ChangeScene.change(event, "Main.fxml");
        }
    }

    public void sceneHome(ActionEvent event) { //back button
        if(getUserName() == null) {
            ChangeScene.change(event, "Main.fxml");
        }
        else{
            ChangeScene.change(event, "Game.fxml");
        }
    }

    private boolean checkEmail(){
        String getEmail = email.getText();
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (getEmail == null)
            return false;
        return pat.matcher(getEmail).matches();
    }
}
