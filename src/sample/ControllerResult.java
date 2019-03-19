package sample;

import static sample.ControllerHome.getUserName;
import static sample.ControllerQuestion.findUser;
import static sample.Logout.logOut;

import Connection.ConnectionPool;
import Connection.Cleaner;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import javax.swing.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import static sample.ChooseOpponent.getGameId;

public class ControllerResult {

    private String username = getUserName();
    private int gameId = getGameId();

    @FXML
    public Button refresh;

    //result
    public TextField totalScore;
    public TextField resultText;
    public TextField resultHeading;

    public void sceneHome(ActionEvent event){ ChangeScene.change(event, "Game.fxml");}

    public void sceneChallengeUser(ActionEvent event){ ChangeScene.change(event, "ChallengeUser.fxml");}

    private void initialize() {
        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;

        String sqlFinished = "SELECT * FROM Game WHERE gameId =" + gameId + ";";
        String sqlDeleteGame = "DELETE FROM Game WHERE gameId =" + gameId + ";";
        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            String player = findUser();
            rs = statement.executeQuery(sqlFinished);
            rs.next();
            int p1Finished = rs.getInt("p1Finished");
            int p2Finished = rs.getInt("p2Finished");

            //if the opponent isn't finished, but you are
            if((p1Finished == 1 && p2Finished == 0) && player.equals("player1") || (p2Finished == 1 && p1Finished == 0) && player.equals("player2")) {
                String sqlQuit = "UPDATE Player SET gameId=NULL WHERE username ='" + username +"';";

                //slå av autocommit??? rollback osv?
                statement.executeUpdate(sqlQuit);
            }

            //if both are finished
            else if(p1Finished == 1 && p2Finished == 1) {
                statement.executeQuery(sqlDeleteGame);
                //utfør sletting, blir gameId sletta på spilleren som spiller da?
            }



        }catch (SQLException e) {
            e.printStackTrace();
        }finally {
            Cleaner.close(statement, rs, connection);
        }
    }
}