package Controllers;

import Connection.ConnectionPool;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

import java.sql.*;

import java.util.Timer;
import java.util.TimerTask;

import Connection.Cleaner;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;

import static Controllers.ControllerHome.getUserName;

/**
 * The class ControllerOpponent is used to create a game.
 * It will display all online users and give the user the opportunity to challenge a player.
 *
 */

public class ControllerOpponent {

    private static Connection connection = null;
    private static Statement statement = null;
    private static ResultSet rs = null;
    private PreparedStatement pstmt = null;

    private String username = getUserName();
    private String opponentUsername = null;
    private Timer timer;
    private int opponentOnline = 0;
    private static int gameId;
    ObservableList<String> onlineList = FXCollections.observableArrayList();

    @FXML
    public TextField opponent;
    public Button challenge;
    public Button btnQuestion;
    public Label usernameWrong;
    public Label challengeYou;
    public Label userOffline;
    public ListView onlineListView;
    public Label label;
    public Label infotext;
    public Label beenChallenged;

    /**
     * This method runs when accessing the ChallengeUser scene.
     * The method runs highscoreTable and onlineUserTable.
     */
    public void initialize(){
        timerOpponent();
        onlineUsersTable();
    }

    /**
     * The method changes scene to Game.
     * @param event is a neccessary paramater which is used in a method from the class ChangeScene.
     */
    public void sceneHome(ActionEvent event) { //home button
        ChangeScene.change(event, "/Scenes/Game.fxml");
    }

    /**
     * The method gives user the option to press enter on the keyboard rather than the button.
     * It will then use the finOpponen-method.
     * @param event is a neccessary paramater which is used in a method from the class ChangeScene.
     */
    public void enter(ActionEvent event) {
        opponent.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                findOpponent(event);
            }
        });
    }

    /**
     * The method finds a user to challenge.
     * It will check if the user is you or if the user is online.
     * If the user is not you and is online, a game will be created by using makeGame.
     * The user will then be sent to a Wait scene.
     * @param event is a neccessary paramater which is used in a method from the class ChangeScene.
     */
    public void findOpponent(ActionEvent event) {
        try{
            connection = ConnectionPool.getConnection();
            usernameWrong.setVisible(false);
            challengeYou.setVisible(false);

            //gets the opponents username, using a prepared statment beacause it's user input
            String insertSql = "SELECT username, online FROM Player WHERE username = ?;";
            pstmt = connection.prepareStatement(insertSql);
            pstmt.setString(1, (opponent.getText().toLowerCase()));
            rs = pstmt.executeQuery();

            //if it is a registered username
            if(rs.next()){
                opponentUsername = rs.getString("username");
                opponentOnline = rs.getInt("Online");
                if(opponentUsername.equals(username)) {
                    setVisible(challengeYou);
                } else if(opponentOnline == 0){
                    setVisible(userOffline);
                } else {
                    makeGame(username, opponentUsername);
                    ChangeScene.change(event, "/Scenes/Wait.fxml");
                }
            }
            //if the username doesn't exsist
            else {
                setVisible(usernameWrong);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }finally {
            Cleaner.close(pstmt, rs, connection);
        }
    }

    /**
     * The private method creates a game.
     * It will check if the challenged user already has gameId.
     * @param player1 is the challenger.
     * @param player2 is the challenged user.
     */
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
            return true;

        }catch (SQLException e) {
            e.printStackTrace();
            return false;
        }finally {
            Cleaner.close(statement, rsGameId, connection);
        }
    }

    /**
     * The method resets the gameId.
     */
   public static void resetGameId(){
        gameId = 0;
   }

    /**
     * A private method for the different error messages.
     * Either sets the visibilty of the components as true or false.
     */
   private void setVisible(Label label){
       usernameWrong.setVisible(false);
       userOffline.setVisible(false);
       challengeYou.setVisible(false);

       label.setVisible(true);
   }

    /**
     * A static method fetching the gameId of the user.
     */
    public static int getGameId() {
        if(gameId != 0) return gameId;
        else{
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
            }finally {
                Cleaner.close(statement, rsGameId, connection);
            }
        }
    }

    /**
     * A private method which displays all online users.
     */
    private void onlineUsersTable(){

        Connection connection = null;
        Statement statement = null;
        ResultSet hs = null;

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            String sqlOnlineUsers = "SELECT username FROM `Player` WHERE online = 1 AND gameId IS NULL;";
            //Legger navn i tabellen onlinelist
            hs = statement.executeQuery(sqlOnlineUsers);

            hideOnlineUsers(true);

            while(hs.next()){
                if(!hs.getString("username").equals(username)) {
                    onlineList.add( hs.getString("username"));
                }
            }

            onlineListView.setItems(onlineList);

            if(onlineList.isEmpty()){
                hideOnlineUsers(false);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Cleaner.close(statement, hs, connection);
        }
    }

    /**
     * A method which chooses an online player's username to appear on the opponent-textfield.
     */
    public void chooseOnlineUser(){
        ObservableList selectedIndices = onlineListView.getSelectionModel().getSelectedIndices();
        int index = -1;
        for(Object o : selectedIndices){
            index = (Integer) o;
        }
        String user = onlineList.get(index);
        opponent.setText(user);
    }

    /**
     * A private method which sets the listViev and hides user
     * @param visibility is either true or false.
     */
    private void hideOnlineUsers(boolean visibility){
        onlineListView.setVisible(visibility);
        label.setVisible(visibility);
        if(visibility){
            challenge.setLayoutX(93);
            opponent.setLayoutX(74);
            infotext.setLayoutX(65);
            usernameWrong.setLayoutX(105);
            challengeYou.setLayoutX(81);
            userOffline.setLayoutX(63);
        } else {
            challenge.setLayoutX(241);
            opponent.setLayoutX(222);
            infotext.setLayoutX(213);
            usernameWrong.setLayoutX(260);
            challengeYou.setLayoutX(236);
            userOffline.setLayoutX(218);
        }
    }

    /**
     * A method which uses the refresh method from ControllerRefresh.
     */
    public void sceneCategory(ActionEvent event) {
        ControllerRefresh.refresh(event, username);
    }

    /**
     * A private method which sets the visibility of different opponents
     */
    private void showBtn() {
        btnQuestion.setVisible(true);
        beenChallenged.setVisible(true);
        opponent.setVisible(false);
        challenge.setVisible(false);
        usernameWrong.setVisible(false);
        challengeYou.setVisible(false);
        userOffline.setVisible(false);
        onlineListView.setVisible(false);
        label.setVisible(false);
        infotext.setVisible(false);
    }

    /**
     * A timer-method
     * Checks if the user has a gameId
     */
    private void timerOpponent(){
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if(checkGameId()) {
                    System.out.println(checkGameId());
                    showBtn();
                    turnOfTimer();
                    return;
                }
            }
        };
        timer.schedule(task, 5000, 3000);
    }

    /**
     * A private method which terminates the timer.
     */
    private void turnOfTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
    }

    /**
     * A private method which checks if the user has a gameId.
     * @return if there's no gameId the method returns true if there is it returns false.
     */
    private boolean checkGameId() {
        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            String sql = "SELECT gameId FROM Player WHERE username ='" + username + "';";
            rs = statement.executeQuery(sql);
            rs.next();
            int playerGameId = rs.getInt(1);
            if(playerGameId != 0){ return true;}
            else return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            Cleaner.close(statement, rs, connection);
        }
    }
}