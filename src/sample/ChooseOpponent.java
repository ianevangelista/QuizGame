package sample;

import Connection.ConnectionClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.*;

import Connection.Cleaner;
import javafx.scene.control.Button;

import static sample.ControllerHome.getUserName;

public class ChooseOpponent{

    public Label usernameWrong;
    private Cleaner cleaner = new Cleaner();

    private ConnectionClass connectionClass;
    private Connection connection;
    private String username;
    private String opponentUsername = null;
    private static int gameId;


    @FXML
    public TextField opponent;
    public Button challenge;


    public void findOpponent(ActionEvent event) {
        ResultSet rs = null;
        PreparedStatement insertSentence = null;

        try{
            connectionClass = new ConnectionClass();
            connection = connectionClass.getConnection();
            findMyUsername();
            usernameWrong.setVisible(false);
            String insertSql = "SELECT username FROM Player WHERE username =?;";
            insertSentence = connection.prepareStatement(insertSql);
            insertSentence.setString(1, opponent.getText());
            rs = insertSentence.executeQuery();
            if(rs.next()) {
                opponentUsername = rs.getString("username");
                    makeGame(username, opponentUsername);
            }
            else {
                usernameWrong.setVisible(true);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }finally {
            cleaner.close(insertSentence, rs, connection);
        }
    }

    private void findMyUsername() {
        this.username = getUserName();
    }

    private void makeGame(String player1, String player2) {
        Statement statement = null;
        ResultSet rsGameId = null;

        try{
            statement = connection.createStatement();
            String sqlInsert = "INSERT INTO Game(player1, player2) VALUES('"+ player1 + "', '" + player2 + "');";
            statement.executeUpdate(sqlInsert, Statement.RETURN_GENERATED_KEYS);
            rsGameId = statement.getGeneratedKeys();
            rsGameId.next();
            gameId = rsGameId.getInt(1);

            System.out.println(gameId);
        }catch (SQLException e) {
            e.printStackTrace();
        }finally {
            cleaner.close(statement, rsGameId, connection);
        }

    }

    public static int getGameId() {
        return gameId;
    }
}
