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

    //Fxml buttons
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
     * @return if all code is executed return true.
     */
    public boolean initialize() { //gets run when the window is opened for the first time
        Random rand = new java.util.Random();
        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            gameId = getGameId();

            // Gets all categories from the database
            String sql = "SELECT categoryId, name FROM `Category`";
            rs = statement.executeQuery(sql);

            // ArrayList that contains all category names
            ArrayList<String> categoryName = new ArrayList<String>();

            // Fills up the arraylists with data from the database
            while (rs.next()) {
                categoryId.add(rs.getInt("categoryId"));
                categoryName.add(rs.getString("name"));
            }

            int amountOfCategorys = categoryId.size();

            // Fills array with random numbers
            for (int i = 0; i < 3; i++) {
                randomCategoryId[i] = rand.nextInt(amountOfCategorys);
            }

            // Checks that first and second element are different
            while (randomCategoryId[0] == randomCategoryId[1]) {
                randomCategoryId[1] = rand.nextInt(amountOfCategorys);
            }

            // Checks that third element is different from first and second
            while (randomCategoryId[0] == randomCategoryId[2] || randomCategoryId[1] == randomCategoryId[2]) {
                randomCategoryId[2] = rand.nextInt(amountOfCategorys);
            }

            // Adds the names of three random categories to the buttons in the scene
            category1.setText(categoryName.get(randomCategoryId[0]));
            category2.setText(categoryName.get(randomCategoryId[1]));
            category3.setText(categoryName.get(randomCategoryId[2]));
            return true;
        } catch (SQLException sqle) {
            // Database access error
            System.out.println("Database access error");
            sqle.printStackTrace();
            return false;
        } catch (Exception e) {
            // If something else goes wrong
            e.printStackTrace();
            return false;
        } finally {
            // closes everything
            Cleaner.close(statement, rs, connection);
        }
    }

    /**
     * A method when the home button is pressed.
     * You will return to the previous page, the game page.
     * @param event is a necessary parameter which is used in a method from the class ChangeScene.
     */
    public void sceneHome(ActionEvent event) { 
        // Change scene to the main game page
        ChangeScene.change(event, "/Scenes/Game.fxml");
    }

    /**
     * A method for when the category 1 button is pressed.
     * It will set the categoryId for the Game.
     * @param event is a necessary parameter which is used in a method from the class ChangeScene.
     * @return if category is set return true, else return false.
     */
    public boolean chooseCategory1(ActionEvent event){ //When button 1 is pressed
        // Translates the random number to the equivalent id in the database in case of the Id's not being sequential
        int chosenCategoryId = categoryId.get(randomCategoryId[0]);
        if(updateCategory(chosenCategoryId, gameId)) {
            // Adds questions from the category to the game
            questionPicker(chosenCategoryId, gameId);
            // Changes scene to the page where you can answer questions
            ChangeScene.change(event, "/Scenes/Question.fxml");
            return true;
        }
        // Returns false if the category fails to update
        return false;
    }

    /**
     * A method for when the category 2 button is pressed.
     * It will set the categoryId for the Game.
     * @param event is a necessary parameter which is used in a method from the class ChangeScene.
     * @return if category is set return true, else return false.
     */
    public boolean chooseCategory2(ActionEvent event){ //When button 2 is pressed
        // Translates the random number to the equivalent id in the database in case of the Id's not being sequential
        int chosenCategoryId = categoryId.get(randomCategoryId[1]);
        if(updateCategory(chosenCategoryId, gameId)) {
            // Adds questions from the category to the game
            questionPicker(chosenCategoryId, gameId);
            // Changes scene to the page where you can answer questions
            ChangeScene.change(event, "/Scenes/Question.fxml");
            return true;
        }
        // Returns false if the category fails to update
        return false;
    }

    /**
     * A method for when the category 3 button is pressed.
     * It will set the categoryId for the Game.
     * @param event is a necessary parameter which is used in a method from the class ChangeScene.
     * @return if category is set return true, else return false.
     */
    public boolean chooseCategory3(ActionEvent event){ //When button 3 is pressed
        // Translates the random number to the equivalent id in the database in case of the Id's not being sequential
        int chosenCategoryId = categoryId.get(randomCategoryId[2]);
        if(updateCategory(chosenCategoryId, gameId)) {
            // Adds questions from the category to the game
            questionPicker(chosenCategoryId, gameId);
            // Changes scene to the page where you can answer questions
            ChangeScene.change(event, "/Scenes/Question.fxml");
            return true;
        }
        // Returns false if the category fails to update
        return false;
    }

    /**
     * A private method which is used inside the chooseCategory-methods.
     * It will set the categoryId for the Game.
     * @param categoryId is a parameter which is used to identify which categoryId is chosen.
     * @param gameId is a parameter which is used to identify which gameId to update with the categoryId.
     * @return if category is set return true.
     */
    public boolean updateCategory(int categoryId, int gameId) {
        try {
            // Sets up the connection to the database
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            // Sets category of the game in the database to the chosen category
            String sql = "UPDATE Game SET categoryId = " + categoryId + " WHERE gameId = " + gameId;
            statement.executeUpdate(sql);
            return true;
        }
        catch(SQLException sqle){
            // Database access error
            System.out.println("Database access error");
            sqle.printStackTrace();
            return false;
        }
        catch (Exception e) {
            // If something else goes wrong
            e.printStackTrace();
            return false;
        }
        finally {
            // Close connection
            Cleaner.close(statement, rs, connection);
        }
    }

    /**
     * A private method to choose three randomly questions when a category is chosen.
     * The method is usen in all thre chooseCategory-methods.
     * @param categoryId is a parameter which is used to identify which categoryId is chosen.
     * @param gameId is a parameter which is used to identify which gameId to update with the categoryId.
     */
    private void questionPicker(int categoryId, int gameId) {
        try {
            // Sets up the connection
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();

            // Gets all the question ids that are in the chosen category
            String sqlGetText = "SELECT questionId FROM Question WHERE categoryId=" + categoryId + ";";
            rs = statement.executeQuery(sqlGetText);

            // Adds all of the question ids to an ArrayList
            ArrayList<Integer> listQuestion = new ArrayList<Integer>();
            while(rs.next()) {
                listQuestion.add(new Integer(rs.getInt("questionId")));
            }

            // Shuffles the list and adds the first three elements in a new list with random questions for the game
            Collections.shuffle(listQuestion);
            int[] questionId = new int[3];
            for (int i=0; i<3; i++) {
                questionId[i] = listQuestion.get(i);
            }

            // Updates the database with the selected questionids
            String sqlUpdate = "UPDATE Game SET question1='" + questionId[0] + "', question2 ='" + questionId[1] + "' , question3='" + questionId[2] + "' WHERE gameId=" + gameId + ";";
            statement.executeUpdate(sqlUpdate);

        }
        catch(SQLException sqle){
            // Database access error
            System.out.println("Database access error");
            sqle.printStackTrace();
        }
        catch (Exception e) {
            // If something else goes wrong
            e.printStackTrace();
        }finally {
            Cleaner.close(statement, rs, connection);
        }
    }
}