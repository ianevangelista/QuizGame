package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import static sample.ChooseOpponent.getGameId;
import static sample.ControllerHome.getUserName;

import Connection.Cleaner;
import Connection.ConnectionPool;

public class ControllerQuestion {
    private int playerScore = 0;
    private static int questionCount = 0;
    private static int gameId;
    private static String username = getUserName();
    private Connection connection = null;
    private Statement statement = null;
    private ResultSet rs = null;
    
    @FXML
    public TextField answerField;
    public Label questionField;
    public Label feedback;
    public Button nxtBtn;
    public Button confirmBtn;
    public Text questionLabel;

    public void initialize(){
        gameId = getGameId();
        questionDisplay();
    }

    public void nextQuest(ActionEvent event) {
        if(questionCount == 3){
            questionCount = 0;
            try {
                connection = ConnectionPool.getConnection();
                statement = connection.createStatement();
                //Removes gameId from player so that they can play a new game
                String sqlRemoveGameIdFromPlayer = "UPDATE Player SET gameId=NULL WHERE username ='" + username + "';";
                statement.executeUpdate(sqlRemoveGameIdFromPlayer);
                ChangeScene.change(event, "Result.fxml");
            }
            catch (Exception e){ e.printStackTrace();}
            finally {Cleaner.close(statement, rs, connection);}
        }else{
            questionDisplay();
            ChangeScene.changeVisibility(false, feedback);
            ChangeScene.changeVisibilityBtn(false, nxtBtn);
            ChangeScene.changeVisibilityBtn(true, confirmBtn);
            answerField.setVisible(true);
            questionField.setVisible(true);
            questionLabel.setVisible(true);
            answerField.setText("");
        }
    }
    public boolean sceneHome(ActionEvent event) { //feedback knapp
        ChangeScene.change(event, "Game.fxml"); //bruker super-metode
        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();

            //Automatically lose a game if you log out
            int gameId = getGameId();
            if(gameId != 0){
                String player = findUser();
                String sqlRageQuitGame;
                if(player.equals("player1")) {
                    sqlRageQuitGame = "UPDATE Game SET p1Points = 0, p1Finished = 1 WHERE gameId =" + gameId + ";";
                } else{
                    sqlRageQuitGame = "UPDATE Game SET p2Points = 0, p2Finished = 1 WHERE gameId =" + gameId + ";";
                }
                statement.executeUpdate(sqlRageQuitGame);

                //Delete game if other player is finished and give opponent points
                String sqlCheckIfOtherPlayerHasLeft = "SELECT gameId FROM Player WHERE gameId =" + gameId + ";";
                ResultSet rsPlayersWithTheGameId = statement.executeQuery(sqlCheckIfOtherPlayerHasLeft);

                if (!rsPlayersWithTheGameId.next()) {
                    if(player.equals("player1")) {
                        String getOpponentPoints = "SELECT player2, p2Points FROM Game WHERE gameId = " + gameId;
                        rs = statement.executeQuery(getOpponentPoints);
                        rs.next();
                        int opponentPoints = rs.getInt("p2Points");
                        String sqlUpdatePlayerScore = "UPDATE Player SET points= points +" + opponentPoints + " WHERE username ='" + rs.getString("player2") + "';";
                        statement.executeUpdate(sqlUpdatePlayerScore);
                    } else {
                        String getOpponentPoints = "SELECT player1, p1Points FROM Game WHERE gameId = " + gameId;
                        rs = statement.executeQuery(getOpponentPoints);
                        rs.next();
                        int opponentPoints = rs.getInt("p1Points");
                        String sqlUpdatePlayerScore = "UPDATE Player SET points= points +" + opponentPoints + " WHERE username ='" + rs.getString("player1") + "';";
                        statement.executeUpdate(sqlUpdatePlayerScore);
                    }
                    String sqlDeleteGame = "DELETE FROM Game WHERE gameId =" + gameId + ";";
                    statement.executeUpdate(sqlDeleteGame);
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }finally {
            Cleaner.close(statement, null, connection);
        }
    }

    public void confirmAnswer(ActionEvent event) { //clicks submit button
        boolean riktig = questionCheck();   //checks answer

        //checks if all the quesitons have been displayed
        if(questionCount == 2) {
            try{
                connection = ConnectionPool.getConnection();
                statement = connection.createStatement();

                String player = findUser();
                String finished = player.equals("player1") ? "p1Finished" : "p2Finished";

                //puts the finished variable true
                String sqlUpdate = "UPDATE Game SET " + finished + "=1 WHERE gameId=" + gameId + ";";
                statement.executeUpdate(sqlUpdate);
                changeTextVis(riktig);

            }catch(SQLException e) {
                e.printStackTrace();
            }finally {
                Cleaner.close(statement, null, connection);
            }
            ChangeScene.changeVisibilityBtn(true, nxtBtn);
        }
        else{
            changeTextVis(riktig);
        }
        ChangeScene.changeVisibilityBtn(false, confirmBtn);
        ChangeScene.changeVisibility(true, feedback);
        answerField.setVisible(false);
        questionField.setVisible(false);
        questionLabel.setVisible(false);

        questionCount++;
    }

    private void changeTextVis(boolean bool){
        if(bool){
            ChangeScene.changeVisibility(true, feedback);
            feedback.setText("Congratulations! You got " + playerScore + " point(s).");
            ChangeScene.changeVisibilityBtn(true, nxtBtn);
        }
        else{
            ChangeScene.changeVisibility(true, feedback);
            feedback.setText("You answered wrong.");
            ChangeScene.changeVisibilityBtn(true, nxtBtn);
        }
    }

    public void questionDisplay() { //displays questions
        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();

            //sql to get question text
            String sqlGetText = "SELECT questionText FROM Game JOIN Question ON questionId = question" + (questionCount+1) +  " WHERE gameId = " + gameId;
            rs = statement.executeQuery(sqlGetText);
            String qText = "";
            if(rs.next()) {
                qText = rs.getString("questionText");
            }
            //displays question
            questionField.setText(qText);

        }catch (SQLException e) {
            e.printStackTrace();
        }finally {
            Cleaner.close(statement, rs, connection);
        }
    }

    public boolean questionCheck() {
        boolean riktig = false;

        String[] sqlQuestionName = {"question1", "question2", "question3"};
        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();

            String user = findUser();       //find user
            String answer = answerField.getText().toLowerCase();  //get answer in lowercase

            //gets the answered questionid
            String sqlGetQId = " FROM Game WHERE gameId=" + gameId + ";";
            rs = statement.executeQuery("SELECT " + sqlQuestionName[questionCount] + sqlGetQId);
            rs.next();
            int QId = rs.getInt(1);

            //Check if player gave actual answer before checking DB
            if(answer != ""){
                //selects all alternatives to the question
                String sqlGetAlt = "SELECT score FROM Alternative WHERE questionId = " + QId +" AND answer = '" + answer + "';";
                rs = statement.executeQuery(sqlGetAlt);

                if(rs.next()){
                    playerScore = rs.getInt("score");
                    riktig = true;
                } else {
                    playerScore = 0;
                }
            } else {
                System.out.printf(answer);
                playerScore = 0;
            }

            //chooses correct players score
            String points = (user.equals("player1") ? "p1Points" : "p2Points");

            //updates score in database
            String sqlUpdate = "UPDATE Game SET " + points + " = " + points + " + " + playerScore + " WHERE gameId=" + gameId + ";";
            statement.execute(sqlUpdate);
            return riktig;

        }catch (SQLException e) {
            e.printStackTrace();
            return riktig;
        }finally {
            Cleaner.close(statement, rs, connection);
        }
    }

    public static String findUser() {
        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;
        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            String sqlPlayer = "SELECT player1 FROM Game WHERE gameId=" + gameId + ";";
            rs = statement.executeQuery(sqlPlayer);
            rs.next();
            String player1Name = rs.getString("player1");
            if(username.equals(player1Name)){return "player1";}
            else return "player2";
        }catch (SQLException e) {
            e.printStackTrace();
            return "ex";
        }finally {
            Cleaner.close(statement, rs, connection);
        }
    }
}