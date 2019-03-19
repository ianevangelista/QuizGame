package sample;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import Connection.Cleaner;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

import static sample.ControllerHome.getUserName;
import Connection.ConnectionPool;

public class ControllerEmail {

    Connection connection = null;
    Statement statement = null;
    ResultSet rs = null;

    /*public void feedback(){

        String findEmail = "SELECT email FROM Player WHERE username = '" + getUserName() + "';";

        String sendTo = "howdumbru@hotmail.com";
        String host = "smtp.live.com";
        String sendFrom = "jlei-lar@hotmail.com";

        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.live", host);
        Session session = Session.getDefaultInstance(properties);

        try{
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            rs = statement.executeQuery(findEmail);
            rs.next();

            //sendFrom = rs.getString(1);

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(sendFrom));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(sendTo));

            message.setText(SETT INN MELDINGEN SOM SKAL BLI SENDT);

            Transport.send(message);

            //KOM MED MELDING OM AT MAIL ER SENDT

        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        catch (MessagingException e) {
            e.printStackTrace();
        }
        finally {
            Cleaner.close(statement, rs, connection);
        }

    }*/
}
