package sample;

import Connection.ConnectionPool;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.*;

import Connection.Cleaner;
import javafx.scene.control.Button;

import static sample.ControllerHome.getUserName;

public class ChooseOpponent{

    private Connection connection;
    private String username = getUserName();
    private String opponentUsername = null;
    private int opponentOnline = 0;
    private static int gameId;


    @FXML
    public TextField opponent;
    public Button challenge;
    public Label usernameWrong;
    public Label challengeYou;
    public Label userOffline;

    public void sceneHome(ActionEvent event) { //home button
        ChangeScene.change(event, "Game.fxml");
    }

    public void findOpponent(ActionEvent event) {
        ResultSet rs = null;
        PreparedStatement insertSentence = null;

        try{
            connection = ConnectionPool.getConnection();
            usernameWrong.setVisible(false);
            challengeYou.setVisible(false);

            //gets the opponents username, using a prepared statment beacause it's user input
            String insertSql = "SELECT username, online FROM Player WHERE username =?;";
            insertSentence = connection.prepareStatement(insertSql);
            insertSentence.setString(1, (opponent.getText().toLowerCase())); //toLowerCase
            rs = insertSentence.executeQuery();

            //if it is a registered username
            if(rs.next()){
                opponentUsername = rs.getString("username");
                opponentOnline = rs.getInt("Online");
                if(opponentUsername.equals(username)) {
                    challengeYou.setVisible(true);
                } else if(opponentOnline == 0){
                    userOffline.setVisible(true);
                } else {
                    makeGame(username, opponentUsername);
                    //timerCat();
                    ChangeScene.change(event, "Wait.fxml");
                }
            }
            //if the username doesn't exsist
            else {
                usernameWrong.setVisible(true);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }finally {
            Cleaner.close(insertSentence, rs, connection);
        }
    }

    private boolean makeGame(String player1, String player2) {
        Statement statement = null;
        ResultSet rsGameId = null;

        try{
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();

            //Checks if the player you are trying to challenge is already challenged
            String sqlCheckIfPlayerAlreadyChallenged = "SELECT gameId FROM `Player` WHERE `Player`.`username` = '" + player2 + "'";
            rsGameId = statement.executeQuery(sqlCheckIfPlayerAlreadyChallenged);
            rsGameId.next();
            int opponentGameId = rsGameId.getInt("gameId");

            if(opponentGameId != 0){ // If the opponent has a gameId it means they are challenged by another player
                Cleaner.close(statement, rsGameId, connection);
                return false;
            }

            //Creates a new game
            String sqlInsert = "INSERT INTO Game(player1, player2, p1Points, p2Points) VALUES('"+ player1 + "', '" + player2 + "', 0, 0);";
            statement.executeUpdate(sqlInsert, Statement.RETURN_GENERATED_KEYS);
            rsGameId = statement.getGeneratedKeys();
            rsGameId.next();
            gameId = rsGameId.getInt(1);

            //Updates both players with a gameId that points to the new game
            sqlInsert = "UPDATE `Player` SET `gameId` = " + gameId + " WHERE `Player`.`username` = '" + player1 + "'";
            statement.executeUpdate(sqlInsert);

            sqlInsert = "UPDATE `Player` SET `gameId` = " + gameId + " WHERE `Player`.`username` = '" + player2 + "'";
            statement.executeUpdate(sqlInsert);

            Cleaner.close(statement, rsGameId, connection);
            return true;

        }catch (SQLException e) {

            e.printStackTrace();
            Cleaner.close(statement, rsGameId, connection);
            return false;
        }
    }

   public static void resetGameId(){
        gameId = 0;
       /*Connection connection = null;
       Statement statement = null;

       String sqlDeleteGame = "DELETE FROM Game WHERE gameId =" + gameId + ";";

       try {
           connection = ConnectionPool.getConnection();
           statement = connection.createStatement();
           statement.executeUpdate(sqlDeleteGame);

           gameId = 0;

       } catch (Exception e) {
           e.printStackTrace();
       } finally {
           Cleaner.close(statement, null, connection);
       }*/
   }


    public static int getGameId() {
        if(gameId != 0) return gameId;
        else{
            Connection connection = null;
            Statement statement = null;
            ResultSet rsGameId = null;

            try {
                connection = ConnectionPool.getConnection();
                statement = connection.createStatement();

                String username = getUserName();

                //Checks if the player you are trying to challenge is already challenged
                String sqlGetGameIdFromPlayer = "SELECT gameId FROM `Player` WHERE `Player`.`username` = '" + username + "'";
                rsGameId = statement.executeQuery(sqlGetGameIdFromPlayer);
                rsGameId.next();
                gameId = rsGameId.getInt("gameId");
                return  gameId;
            }
            catch (Exception e){
                e.printStackTrace();
                return  0;
            }
        }
    }
}