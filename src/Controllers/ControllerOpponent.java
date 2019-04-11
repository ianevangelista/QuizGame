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
 */

public class ControllerOpponent {
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
     * @param event is a necessary parameter which is used in a method from the class ChangeScene.
     */
    public void sceneHome(ActionEvent event) { //home button
        ChangeScene.change(event, "/Scenes/Game.fxml");
    }

    /**
     * The method gives user the option to press enter on the keyboard rather than the button.
     * It will then use the finOpponen-method.
     * @param event is a necessary parameter which is used in a method from the class ChangeScene.
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
     * @param event is a necessary parameter which is used in a method from the class ChangeScene.
     */
    public void findOpponent(ActionEvent event) {
        String opponentUser = opponent.getText();
        int check = checkOpponent(opponentUser);
        if (check == 1) {
            ChangeScene.change(event, "/Scenes/Wait.fxml");
        }
        else if(check == 0){
            setVisible(challengeYou);
        }
        else if(check == 2){
            setVisible(userOffline);
        }
        else if(check == 3){
            setVisible(usernameWrong);
        }
    }

    /**
     * The method finds a user to challenge.
     * It will check if the user is you or if the user is online.
     * If the user is not you and is online, a game will be created by using makeGame.
     * @param opponentName is the opponent's username
     * @return an int which represents if the opponent is you, is online or does not exist.
     */
    public int checkOpponent(String opponentName){
        // Connection objects
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try{
            connection = ConnectionPool.getConnection();
            //gets the opponents username, using a prepared statment beacause it is user input
            String insertSql = "SELECT username, online FROM Player WHERE username = ?;";
            pstmt = connection.prepareStatement(insertSql);
            pstmt.setString(1, (opponentName.toLowerCase()));
            rs = pstmt.executeQuery();

            // If it is a registered username
            if(rs.next()){
                opponentUsername = rs.getString("username");
                opponentOnline = rs.getInt("Online");
                // If the username is you
                if(opponentUsername.equals(username)) {
                    return 0;
                    // If the opponent is not online
                } else if(opponentOnline == 0){
                    return 2;
                    // If the user is online
                } else {
                    makeGame(username, opponentUsername);
                    return 1;
                }
            }
            // If the username doesn't exsist
            else {
                return 3;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }finally {
            Cleaner.close(pstmt, rs, connection);
        }
    }

    /**
     * The public method creates a game.
     * It will check if the challenged user already has gameId.
     * @param player1 is the challenger.
     * @param player2 is the challenged user.
     * @return true if game is made.
     */
    public boolean makeGame(String player1, String player2) {
        // Connection objects
        Connection connection = null;
        Statement statement = null;
        ResultSet rsGameId = null;

        try{
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();

            //Checks if the player you are trying to challenge is already challenged
            if(alreadyChallenged(player2) != -1){
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

    public int alreadyChallenged (String player2){
        // Connection objects
        Connection connection = null;
        Statement statement = null;
        ResultSet rsGameId = null;

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();

            //Checks if the player you are trying to challenge is already challenged
            String sqlCheckIfPlayerAlreadyChallenged = "SELECT gameId FROM `Player` WHERE `Player`.`username` = '" + player2 + "'";
            rsGameId = statement.executeQuery(sqlCheckIfPlayerAlreadyChallenged);
            rsGameId.next();
            int opponentGameId = rsGameId.getInt("gameId");
            // If the opponent has a gameId it means they are challenged by another player
            if (opponentGameId != 0) {
                return opponentGameId;
            }
            return -1;
        }
        catch (SQLException e){
            e.printStackTrace();
            return -1;
        }
        finally {
            Cleaner.close(statement, rsGameId, connection);
        }
    }

    /**
     * The method resets the gameId.
     */
   public static void resetGameId(){
        gameId = 0;
   }    /**


     * A private method for the different error messages.
     * Either sets the visibility of the components as true or false.
     */
   private void setVisible(Label label){
       usernameWrong.setVisible(false);
       userOffline.setVisible(false);
       challengeYou.setVisible(false);

       label.setVisible(true);
   }

    /**
     * A static method to set the gameId.
     */

   public static void setGameId(int newGameId) {
       gameId = newGameId;
   }

    /**
     * A static method fetching the gameId of the user.
     * @return the gameId.
     */
    public static int getGameId() {
        // Connection objects
        Connection connection = null;
        Statement statement = null;
        ResultSet rsGameId = null;

        if(gameId != 0) return gameId;
        else{
            try {
                connection = ConnectionPool.getConnection();
                statement = connection.createStatement();
                // Fetches your username
                String username = getUserName();

                //Checks if the player you are trying to challenge is already challenged
                String sqlGetGameIdFromPlayer = "SELECT gameId FROM `Player` WHERE `Player`.`username` = '" + username + "'";
                rsGameId = statement.executeQuery(sqlGetGameIdFromPlayer);
                rsGameId.next();
                gameId = rsGameId.getInt("gameId");
                return gameId;
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
            hs = statement.executeQuery(sqlOnlineUsers);
            hideOnlineUsers(true);

            // Adds all online users in an ArrayList except yourself
            while(hs.next()){
                if(!hs.getString("username").equals(username)) {
                    onlineList.add( hs.getString("username"));
                }
            }
            // Puts all items from the ArrayList in the ListView
            onlineListView.setItems(onlineList);
            // If the ArrayList is empty do not show the ListView
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
        // Creates an ObservableList to view all online users
        ObservableList selectedIndices = onlineListView.getSelectionModel().getSelectedIndices();
        int index = -1;
        // Each items in the list will be given an index
        for(Object o : selectedIndices){
            index = (Integer) o;
        }
        if(index!= -1) {
            // Sets the user chosen by index
            String user = onlineList.get(index);
            // Displays the username
            opponent.setText(user);
        }
    }

    /**
     * A private method which sets the listViev and hides user
     * @param visibility is either true or false.
     */
    private void hideOnlineUsers(boolean visibility){
        onlineListView.setVisible(visibility);
        label.setVisible(visibility);
        // If true display this layout
        if(visibility){
            challenge.setLayoutX(93);
            opponent.setLayoutX(74);
            infotext.setLayoutX(65);
            usernameWrong.setLayoutX(105);
            challengeYou.setLayoutX(81);
            userOffline.setLayoutX(63);
            // If false display this layout
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
        ControllerRefresh.refresh(event);
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
        // Creates a timer
        timer = new Timer();
        // Creates a TimerTask
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if(checkGameId(username)) {
                    showBtn();
                    turnOffTimer();
                    return;
                }
            }
        };
        // Scheduled to run the task and start after 5 seconds and every 3 seconds
        timer.schedule(task, 5000, 3000);
    }

    /**
     * A private method which terminates the timer.
     */
    private void turnOffTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
    }

    /**
     * A private method which checks if the user has a gameId.
     * @return if there's no gameId the method returns true if there is it returns false.
     */
    public boolean checkGameId(String user) {
        // Connection set-up
        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            // Selects the your gameId
            String sql = "SELECT gameId FROM Player WHERE username ='" + user + "';";
            rs = statement.executeQuery(sql);
            rs.next();
            int playerGameId = rs.getInt("gameId");
            // If there is a gameId return true
            if(playerGameId != 0){
                return true;
            }
            // If no gameId return false
            else return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            // Close everything
            Cleaner.close(statement, rs, connection);
        }
    }
}