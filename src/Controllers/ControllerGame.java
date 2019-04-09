package Controllers;
import javafx.event.ActionEvent;
import static Controllers.ControllerHome.getUserName;
import static Controllers.ControllerHome.setUserNull;

/**
 * The class ControllerGame is the home page after logging in.
 * It only displays different buttons such as starting a game, log out or show profile page.
 */

public class ControllerGame {

    private static String username = getUserName();

    /**
     * The method returns the username of user who has logged in.
     * @return the username of the user.
     */
    public static String getUsername(){
        return username;
    }

    /**
     * The method uses the logout method from the class Logout.
     * Changes scene to the login page.
     * @param event is a necessary parameter which is used in a method from the class ChangeScene.
     */
    public void logout(ActionEvent event) {
        Logout.logOut();
        setUserNull();
        ChangeScene.change(event, "/Scenes/Main.fxml");
    }

    /**
     * The method uses the refresh method from the class ControllerRefresh.
     * Changes to a new scene depending on if the user is challenged or not.
     * @param event is a necessary parameter which is used in a method from the class ChangeScene.
     */
    public void start(ActionEvent event) {
        ControllerRefresh.refresh(event, username);
    }

    /**
     * The method changes scene to either Main or Game.
     * Changes to a Main if not logged in.
     * Changes to Game if logged in.
     * @param event is a necessary parameter which is used in a method from the class ChangeScene.
     */
    public void sceneBack(ActionEvent event) {
        if( getUserName() == null) { ChangeScene.change(event, "/Scenes/Main.fxml"); }
        else{ ChangeScene.change(event, "/Scenes/Game.fxml"); }
    }

    /**
     * The method changes scene to Info.
     * @param event is a necessary parameter which is used in a method from the class ChangeScene.
     */
    public void sceneInfo(ActionEvent event) { //trykker på infoknapp
        ChangeScene.change(event, "/Scenes/Info.fxml");
    }

    /**
     * The method changes scene to Info.
     * @param event is a necessary parameter which is used in a method from the class ChangeScene.
     */
    public void sceneHome(ActionEvent event) { //hjemknapp
        ChangeScene.change(event, "/Scenes/Main.fxml");
    }

    /**
     * The method changes scene to Highscore.
     * @param event is a necessary parameter which is used in a method from the class ChangeScene.
     */
    public void highscore(ActionEvent event) { //HighScore knapp
        ChangeScene.change(event, "/Scenes/HighScore.fxml");
    }

    /**
     * The method changes scene to Profile.
     * @param event is a necessary parameter which is used in a method from the class ChangeScene.
     */
    public void profile(ActionEvent event) {
        ChangeScene.change(event, "/Scenes/Profile.fxml");
    }

    /**
     * The method changes scene to Feedback.
     * @param event is a necessary parameter which is used in a method from the class ChangeScene.
     */
    public void feedback(ActionEvent event){
        ChangeScene.change(event, "/Scenes/Feedback.fxml");
    }
}