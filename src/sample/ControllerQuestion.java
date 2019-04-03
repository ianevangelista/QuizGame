package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static sample.ControllerOpponent.getGameId;
import static sample.ControllerOpponent.resetGameId;
import static sample.ControllerHome.getUserName;

import Connection.Cleaner;
import Connection.ConnectionPool;

public class ControllerQuestion {
    private int seconds = 31;
    private Timer timer = new Timer();
    private int questionCount = 0;
    private static int gameId;
    private static String username = getUserName();
    private Connection connection = null;
    private Statement statement = null;
    private ResultSet rs = null;
    
    @FXML
    public TextField answerField;
    public Label questionField;
    public Label feedback;
    public Label countdown;
    public Label resultText;
    public Button confirmBtn;
    public Button resultButton;
    public Text questionLabel;


    //Answers locally cached
    private ArrayList<Integer> score = new ArrayList<Integer>();
    private ArrayList<String> rightAnswer = new ArrayList<String>();
    private ArrayList<Integer> previouslyAnswered = new ArrayList<Integer>();

    //Total points for the entire game
    private int totalPoints = 0;

    public void initialize(){
        timerCountdown();
        gameId = getGameId();
        questionDisplay();
    }

    public void sceneResult(ActionEvent event){ ChangeScene.change(event, "Result.fxml");}

    public void nextQuestion() {
        //System.out.println(questionCount);
        if(questionCount > 3){

        }
        else if(questionCount > 2){
            timer.cancel();
            timer.purge();
            answerField.setVisible(false);
            answerField.setText("");
            questionField.setVisible(false);
            feedback.setVisible(false);
            countdown.setVisible(false);
            confirmBtn.setVisible(false);
            questionLabel.setVisible(false);
            resultText.setVisible(true);
            resultButton.setVisible(true);
            try {
                connection = ConnectionPool.getConnection();
                statement = connection.createStatement();
                //Removes gameId from player so that they can play a new game
                String sqlRemoveGameIdFromPlayer = "UPDATE Player SET gameId=NULL WHERE username ='" + username + "';";
                statement.executeUpdate(sqlRemoveGameIdFromPlayer);

                //finds out if the player is player1 or 2
                //Update Game with player finished and final score
                String player = findUser();
                String finished = player.equals("player1") ? "p1Finished" : "p2Finished";
                String points = player.equals("player1") ? "p1Points" : "p2Points";

                //puts the finished variable true
                String sqlUpdate = "UPDATE Game SET " + finished + "=1, " + points + "=" + totalPoints + " WHERE gameId=" + gameId + ";";
                totalPoints = 0;
                statement.executeUpdate(sqlUpdate);
            }
            catch (Exception e){ e.printStackTrace();}
            finally {Cleaner.close(statement, rs, connection);}
        } else {
            questionDisplay();
            timerCountdown();
        }
    }
    public void confirmAnswer() { //clicks submit button
        int answerScore = questionCheck();   //checks answer
        showFeedback(answerScore);
    }

    private void showFeedback(int score){
        if(score == -1){
            feedback.setText("You already answered that!");
            answerField.setText("");
        }
        else if(score > 0){
            feedback.setText("Congratulations! You got " + score + ((score > 1) ? " points." : " point."));
        }
        else{
            feedback.setText("You answered wrong.");
        }
    }

    private void questionDisplay() { //displays questions
        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();

            //sql to get question text
            String sqlGetText = "SELECT questionText, questionId FROM Game JOIN Question ON questionId = question" + (questionCount+1) +  " WHERE gameId = " + gameId;
            rs = statement.executeQuery(sqlGetText);
            int qId = 0;
            String qText = "";
            if(rs.next()) {
                qText = rs.getString("questionText");
                qId = rs.getInt("questionId");
            }
            //displays question
            questionField.setText(qText);

            //selects all alternatives to the question
            String sqlGetAlt = "SELECT score, answer FROM Alternative WHERE questionId = " + qId +";";
            rs = statement.executeQuery(sqlGetAlt);

            //emptys previous values from the question that came before
            rightAnswer = new ArrayList<String>();
            score = new ArrayList<Integer>();
            previouslyAnswered = new ArrayList<Integer>();

            //fills arrayLists with answers and scores for the question
            while(rs.next()){
                rightAnswer.add(rs.getString("answer").toLowerCase());
                score.add(rs.getInt("score"));
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }finally {
            Cleaner.close(statement, rs, connection);
        }
    }

    public void enter() {
        answerField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                confirmAnswer();
            }
        });
    }

    //Checks if the answered question is correct and if it has already been answered by the user this round
    private int questionCheck() {
        int answerScore = 0;

        String answer = answerField.getText().toLowerCase();  //get answer in lowercase

        //Check if player gave actual answer before checking DB
        if(!answer.equals("")){
            //check trought arrayList of all possible answers
            for(int i = 0; i < rightAnswer.size(); i++){
                if(answer.equals(rightAnswer.get(i))){
                    for(Integer previousAnswer: previouslyAnswered){
                        if(previousAnswer == i){
                            //That answer is already used
                            return -1;
                        }
                    }
                    previouslyAnswered.add(i);
                    totalPoints += score.get(i);
                    answerScore = score.get(i);
                    //TODO: Give feedback to the user
                }
            }
        } else {
            //TODO: "PLEASE ENTER YOUR ANSWER"
        }
        answerField.setText("");
        return answerScore;
    }

    //Finds out whether current user is player 1 or player 2
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

    //Timer
    private TimerTask makeTask() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    seconds--;
                    countdown.setText("Seconds left: " + seconds);
                    if (seconds == 0) {
                        seconds = 31;
                        questionCount++;
                        nextQuestion();
                        timer.cancel();
                        timer.purge();
                        return;
                    }
                });
            }
        };
        return task;
    }

    public void timerCountdown() {
        TimerTask task = makeTask();
        timer = new Timer();
        timer.scheduleAtFixedRate(task, 1000, 1000);
    }

    //Home-button quits game
    public boolean sceneHome(ActionEvent event) {
        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();

            //Automatically lose a game if you log out
            int gameId = getGameId();
            if(gameId != 0){
                String player = findUser();
                String sqlRageQuitGame;
                String rageQuittingPlayer = player.equals("player1") ? "p1Points = 0, p1Finished = 1" : "p2Points = 0, p2Finished = 1";
                sqlRageQuitGame = "UPDATE Game SET " + rageQuittingPlayer + " WHERE gameId =" + gameId + ";";
                statement.executeUpdate(sqlRageQuitGame);

                //remove gameId from player
                String sqlRemoveGameId = "UPDATE Player SET gameId = NULL, gamesLost = gamesLost+1 WHERE username = '" + username + "';";
                statement.executeUpdate(sqlRemoveGameId);

                //Delete game if other player is finished and give opponent points
                String sqlCheckIfOtherPlayerHasLeft = "SELECT gameId FROM Player WHERE gameId =" + gameId + ";";
                ResultSet rsPlayersWithTheGameId = statement.executeQuery(sqlCheckIfOtherPlayerHasLeft);

                if (!rsPlayersWithTheGameId.next()) {
                    String points = player.equals("player1") ? "p2Points" : "p1Points";
                    String opponent = player.equals("player1") ? "player2" : "player1";
                    String getOpponentPoints = "SELECT player2, p2Points FROM Game WHERE gameId = " + gameId;
                    rs = statement.executeQuery(getOpponentPoints);
                    rs.next();
                    int opponentPoints = rs.getInt(points);
                    String sqlUpdatePlayerScore = "UPDATE Player SET points= points +" + opponentPoints + " WHERE username ='" + rs.getString(opponent) + "';";
                    statement.executeUpdate(sqlUpdatePlayerScore);

                    String sqlDeleteGame = "DELETE FROM Game WHERE gameId =" + gameId + ";";
                    statement.executeUpdate(sqlDeleteGame);
                    resetGameId();
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }finally {
            Cleaner.close(statement, null, connection);
            ChangeScene.change(event, "Game.fxml"); //bruker super-metode
        }
    }
}