package Controllers;

import Connection.ConnectionPool;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import Connection.Cleaner;
import javafx.scene.control.Button;

import java.util.Collections;
import java.util.Random;
import java.util.ArrayList;

//imports the method getGameId() from the class ChooseOpponent
import static Controllers.ControllerOpponent.getGameId;


/**
 * The class ControllerCategory is used when a game is created.
 * It displays three random category to the user.
 * After a category is chosen, another three questions will be randomly chosen from the database.
 */

public class ControllerCategory {
    private int gameId;

    @FXML
    public Button category1;
    public Button category2;
    public Button category3;

    //Connections set-up
    private Connection connection = null;
    private Statement statement = null;
    private ResultSet rs = null;

    //ArrayList containing id of all categories in the database
    ArrayList<Integer> categoryId = new ArrayList<Integer>();

    //Array that fills up with three random and distinct numbers that reference indexes of the categoryId ArrayList
    private int[] randomCategoryId = new int[3];

    /**
     * This method runs after a game request is accepted.
     * It will display three random categorys from the database.
     */
    public boolean initialize(){ //gets run when the window is opened for the first time
        Random rand = new java.util.Random();
        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            gameId = getGameId();

            // Gets all categories from the database
            String sql = "SELECT categoryId, name FROM `Category`";
            rs = statement.executeQuery(sql);

            ArrayList<String> categoryName = new ArrayList<String>();

            while(rs.next()){
                categoryId.add(rs.getInt("categoryId"));
                categoryName.add(rs.getString("name"));
            }

            int amountOfCategorys = categoryId.size();

            //Fills array with random numbers
            for (int i = 0; i < 3; i++) {
                randomCategoryId[i] = rand.nextInt(amountOfCategorys);
            }

            //Checks that first and second element are different
            while (randomCategoryId[0] == randomCategoryId[1]) {
                randomCategoryId[1] = rand.nextInt(amountOfCategorys);
            }

            //Checks that third element is different from first and second
            while (randomCategoryId[0] == randomCategoryId[2] || randomCategoryId[1] == randomCategoryId[2]) {
                randomCategoryId[2] = rand.nextInt(amountOfCategorys);
            }
            category1.setText(categoryName.get(randomCategoryId[0]));
            category2.setText(categoryName.get(randomCategoryId[1]));
            category3.setText(categoryName.get(randomCategoryId[2]));
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return true;
        }
        finally {
            Cleaner.close(statement, rs, connection);
        }
    }

    /**
     * A method when the home button is pressed.
     * You will return to the previous page, the game page.
     * @param event is a neccessary paramater which is used in a method from the class ChangeScene.
     */
    public void sceneHome(ActionEvent event) { //home button
        ChangeScene.change(event, "/Scenes/Game.fxml");
    }

    /**
     * A method when the category 1 button is pressed.
     * It will set the categoryId for the Game.
     * @param event is a neccessary paramater which is used in a method from the class ChangeScene.
     */
    public boolean chooseCategory1(ActionEvent event){ //When button 1 is pressed
        int chosenCategoryId = categoryId.get(randomCategoryId[0]);
        if(updateCategory(chosenCategoryId, gameId)) {
            // Adds questions from the category to the game
            questionPicker(chosenCategoryId, gameId);
            ChangeScene.change(event, "/Scenes/Question.fxml");
            return true;
        }
        return false;
    }

    /**
     * A method when the category 2 button is pressed.
     * It will set the categoryId for the Game.
     * @param event is a neccessary paramater which is used in a method from the class ChangeScene.
     */
    public boolean chooseCategory2(ActionEvent event){ //When button 2 is pressed
        int chosenCategoryId = categoryId.get(randomCategoryId[1]);
        if(updateCategory(chosenCategoryId, gameId)) {
            questionPicker(chosenCategoryId, gameId);
            ChangeScene.change(event, "/Scenes/Question.fxml");
            return true;
        }
        return false;
    }

    /**
     * A method when the category 3 button is pressed.
     * It will set the categoryId for the Game.
     * @param event is a neccessary paramater which is used in a method from the class ChangeScene.
     */
    public boolean chooseCategory3(ActionEvent event){ //When button 3 is pressed
        int chosenCategoryId = categoryId.get(randomCategoryId[2]);
        if(updateCategory(chosenCategoryId, gameId)) {
            questionPicker(chosenCategoryId, gameId);
            ChangeScene.change(event, "/Scenes/Question.fxml");
            return true;
        }
        return false;
    }

    /**
     * A private method which is used inside the chooseCategory-methods.
     * It will set the categoryId for the Game.
     * @param categoryId is a paramater which is used to identify which categoryId is chosen.
     * @param gameId is a paramater which is used to identify which gameId to update with the categoryId.
     */
    public boolean updateCategory(int categoryId, int gameId) {
        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            String sql = "UPDATE Game SET categoryId = " + categoryId + " WHERE gameId = " + gameId;
            statement.executeUpdate(sql);
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
        finally {
            Cleaner.close(statement, rs, connection);
        }
    }

    /**
     * A private method to choose three randomly questions when a category is chosen.
     * The method is usen in all thre chooseCategory-methods.
     * @param categoryId is a paramater which is used to identify which categoryId is chosen.
     * @param gameId is a paramater which is used to identify which gameId to update with the categoryId.
     */
    private void questionPicker(int categoryId, int gameId) {
        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();

            //gets all the questions in chosen category
            int[] questionId = new int[3];
            String sqlGetText = "SELECT questionId FROM Question WHERE categoryId=" + categoryId + ";";
            rs = statement.executeQuery(sqlGetText);
            ArrayList<Integer> listQuestion = new ArrayList<Integer>();
            while(rs.next()) {
                listQuestion.add(new Integer(rs.getInt("questionId")));
            }

            //shuffles the list and puts 0-2 in a new list
            Collections.shuffle(listQuestion);
            for (int i=0; i<3; i++) {
                questionId[i] = listQuestion.get(i);
            }

            //updates the database with the selected questionids
            String sqlUpdate = "UPDATE Game SET question1='" + questionId[0] + "', question2 ='" + questionId[1] + "' , question3='" + questionId[2] + "' WHERE gameId=" + gameId + ";";

            statement.executeUpdate(sqlUpdate);

        }catch (SQLException e) {
            e.printStackTrace();
        }finally {
            Cleaner.close(statement, rs, connection);
        }
    }
}