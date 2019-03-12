package sample;

import Connection.ConnectionClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.sql.PreparedStatement;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import Connection.Cleaner;
import Connection.ConnectionClass;
import javafx.scene.control.Button;

public class ChooseOpponent {

    public TextField user_challenge;
    public Label usernameWrong;
    private Cleaner cleaner = new Cleaner();
    private ChangeScene sceneChanger = new ChangeScene();
    private String username;

    @FXML
    public TextField opponent;
    public Button challenge;


    public void findOpponent(ActionEvent event) {
        ConnectionClass connectionClass;
        Connection connection;
        Statement statement;
        ResultSet rs = null;
        ResultSet res = null;

        PreparedStatement insertSetning;




        try{
            connectionClass = new ConnectionClass();
            connection = connectionClass.getConnection();

            String insertSql = "SELECT username FROM Player WHERE username =?;";
            insertSetning = connection.prepareStatement(insertSql);
            insertSetning.setString(1, opponent.getText());
            rs = insertSetning.executeQuery();
            if(rs.next()) {

            }















           /* rs = statement.executeQuery(sql);

            String realUsername = null;
            if (rs.next()) {
                realUsername = rs.getString("user_challenge");
            } else {
                realUsername = "-1";
            }
            if (realUsername.equals(user_challenge.getText())){
                cleaner.close(statement, rs, connection);
                sceneChanger.change(event, "Categories.fxml");
            } else {
                cleaner.close(statement, rs, connection);
                sceneChanger.changeVisibility(true, usernameWrong);
            }*/
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
