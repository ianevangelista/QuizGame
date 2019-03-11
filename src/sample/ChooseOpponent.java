package sample;

import Connection.ConnectionClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

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
        ConnectionClass connectionClass = new ConnectionClass();
        Connection connection = connectionClass.getConnection();

        ResultSet rs = null;


        String sql = "SELECT username FROM Player WHERE username ='" + user_challenge.getText() + "';";

        try{
            Statement statement = connection.createStatement();
            rs = statement.executeQuery(sql);

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
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
