package Controllers;

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

import static Controllers.ControllerOpponent.getGameId;
import static Controllers.ControllerHome.getUserName;

import Connection.Cleaner;
import Connection.ConnectionPool;

/**
 * The class ControllerOpponent is used to create a game.
 * It will display all online users and give the user the opportunity to challenge a player.
 */

public class ControllerQuestion {
    private boolean running = true;
    private int seconds = 31;
    private Timer timer = new Timer();
    private int questionCount = 0;
    private static int gameId;
    private static String username = getUserName();

    // FXML components
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

    /**
     * The method changes scene to Result.
     * @param event is a necessary parameter which is used in a method from the class ChangeScene.
     */
    public void sceneResult(ActionEvent event){ ChangeScene.change(event, "/Scenes/Result.fxml");}

    /**
     * The method checks if all questions have been answered.
     * If all questions are answered it will update the player's gameId and update the game and set you as finished.
     * If not all questions are answered, display next question and start the countdown.
     */
    public void nextQuestion() {
        // Connection objects
        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;

        if(questionCount > 2){
            running = false;
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

                if(!player.equals("game deleted")){
                    String finished = player.equals("player1") ? "p1Finished" : "p2Finished";
                    String points = player.equals("player1") ? "p1Points" : "p2Points";

                    //puts the finished variable true
                    String sqlUpdate = "UPDATE Game SET " + finished + "=1, " + points + "=" + totalPoints + " WHERE gameId=" + gameId + ";";
                    totalPoints = 0;
                    statement.executeUpdate(sqlUpdate);
                }
            }
            catch (Exception e){ e.printStackTrace();}
            finally {Cleaner.close(statement, rs, connection);}
        } else {
            questionDisplay();
            timerCountdown();
        }
    }

    /**
     * The method is connected to the confirm button and uses questionCheck to validate the answer.
     */
    public void confirmAnswer() {
        int answerScore = questionCheck();
        showFeedback(answerScore);
    }

    /**
     * The method displays a feedback message after answering.
     * @param score your score gained after a question.
     */
    private void showFeedback(int score){
        if(score == -1){
            feedback.setText("You already answered that!");
            answerField.setText("");
        }
        else if(score == -2){
            feedback.setText("Please enter an answer");
        }
        else if(score > 0){
            feedback.setText("Congratulations! You got " + score + ((score > 1) ? " points." : " point."));
        }
        else{
            feedback.setText("You answered wrong.");
        }
    }

    /**
     * The method displays the question by using questionInfo.
     */
    public void questionDisplay() {
        String qText = questionInfo();
        questionField.setText(qText);
    }

    /**
     * The method fetches the questions from the database depending on the category.
     * It will save the answers of every questions and the user's input to keep track of the score.
     * @return the question text.
     */
    public String questionInfo() {
        // Connection objects
        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();

            //sql to get question text
            String sqlGetText = "SELECT questionText, questionId FROM Game JOIN Question ON questionId = question" + (questionCount+1) +  " WHERE gameId = " + getGameId();
            rs = statement.executeQuery(sqlGetText);
            int qId = 0;
            String qText = "";
            if(rs.next()) {
                qText = rs.getString("questionText");
                qId = rs.getInt("questionId");
            }


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
            return qText;
        }catch (SQLException e) {
            e.printStackTrace();
            return "";
        }finally {
            Cleaner.close(statement, rs, connection);
        }
    }

    /**
     * The method gives user the option to press enter on the keyboard rather than the button.
     * It will then use the confirmAnswer-method.
     */
    public void enter() {
        answerField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                confirmAnswer();
            }
        });
    }

    /**
     * The method checks if the answered question is correct.
     * It will also check if the user already have answered the same input the same round.
     * @return an int depending on answering correct, incorrect or answering null.
     */
    private int questionCheck() {
        int answerScore = 0;
        String answer = answerField.getText().toLowerCase();  //get answer in lowercase
        answerField.setText("");
        //Check if player gave actual answer before checking DB
        if(!answer.equals("")){
            answerScore = findScore(answer);
        }
        else answerScore = -2;
        return answerScore;
    }

    /**
     * The method checks the score of your input.
     * It will add the score to your total score the same game.
     * @param answer is the user's input.
     * @return the score of your answer.
     */
    public int findScore(String answer) {
        int answerScore = 0;
        //check trought arrayList of all possible answers
        for(int i = 0; i < rightAnswer.size(); i++) {
            if (answer.equals(rightAnswer.get(i))) {
                for (Integer previousAnswer : previouslyAnswered) {
                    if (previousAnswer == i) {
                        //That answer is already used
                        return -1;
                    }
                }
                previouslyAnswered.add(i);
                totalPoints += score.get(i);
                answerScore = score.get(i);
            }
        }
        return answerScore;
    }

    /**
     * The method checks if the user is player 1 or player 2
     * @return a String if the player is either 1 or 2 or else it will return game deleted.
     */
    public static String findUser() {
        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;
        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            String sqlPlayer = "SELECT player1 FROM Game WHERE gameId=" + getGameId() + ";";
            rs = statement.executeQuery(sqlPlayer);
            if(rs.next()) {
                String player1Name = rs.getString("player1");
                if (getUserName().equals(player1Name)) {
                    return "player1";
                } else return "player2";
            } else {
                return "game deleted";
            }
        }catch (SQLException e) {
            e.printStackTrace();
            return "ex";
        }finally {
            Cleaner.close(statement, rs, connection);
        }
    }

    /**
     * The method start a timer which starts after one second and repeats every seconds.
     */
    private void timerCountdown() {
        TimerTask task = makeTask();
        timer = new Timer();
        timer.scheduleAtFixedRate(task, 1000, 1000);
    }

    /**
     * The method creates a task for a timer. It will display the amount of seconds left.
     * It displays the next question if the seconds run out.
     * @return the TimerTask
     */
    private TimerTask makeTask() {
        return new TimerTask() {
            @Override
            public void run() {
                if(running) {
                    Platform.runLater(() -> {
                        seconds--;
                        countdown.setText("Seconds left: " + seconds);
                        if (seconds == 0) {
                            seconds = 31;
                            questionCount++;
                            nextQuestion();
                            turnOffTimer();
                            return;
                        }
                    });
                }
            }
        };
    }

    /**
     * The method terminates the timer
     */
    public void turnOffTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            return;
        }
    }
}