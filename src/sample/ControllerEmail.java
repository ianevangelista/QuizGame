package sample;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

import Connection.ConnectionPool;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;


public class ControllerEmail {

    @FXML
    public TextArea feedback;
    public TextField email;

    public void sendFeedback(){

        String sendTo = "howdumbru.game@gmail.com";
        String host = "smtp.gmail.com";
        String username = "howdumbru.game@gmail.com";
        String password = "Junierkul";

        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.user", username);
        props.put("mail.smtp.password", password);
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", host);


        try{

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(sendTo));

            message.setSubject("Feedback from How Dumb R U?");
            message.setText(feedback.getText());

            Transport transport = session.getTransport("smtp");
            transport.connect(host, username, password);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();

        }
        catch (MessagingException e) {
            System.out.println("Heihei fanger feil");
            e.printStackTrace();
        }

    }

    public void sendConfirm(){

        String sendTo = email.getText();
        String host = "smtp.gmail.com";
        String username = "howdumbru.game@gmail.com";
        String password = "Junierkul";

        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.user", username);
        props.put("mail.smtp.password", password);
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", host);

        try{

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(sendTo));

            message.setSubject("Received feedback from How Dumb R U?");
            message.setText("Feedback received! We will come back to you as soon as possible.");

            Transport transport = session.getTransport("smtp");
            transport.connect(host, username, password);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();

        }
        catch (MessagingException e) {
            System.out.println("Heihei fanger feil2");
            e.printStackTrace();
        }

    }

    public void feedback(ActionEvent event){
        sendFeedback();
        sendConfirm();
        ChangeScene.change(event, "Main.fxml");
    }

    public void sceneHome(ActionEvent event) { //feedback knapp
        ChangeScene.change(event, "Main.fxml"); //bruker super-metode
    }
}
