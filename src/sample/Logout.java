package sample;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import Connection.ConnectionPool;
import Connection.Cleaner;
import static sample.ControllerHome.getUserName;
import static sample.ControllerQuestion.findUser;
import static sample.ChooseOpponent.getGameId;

public class Logout {
    private static String username = getUserName();

    public static boolean logOut(){
        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;
        String sqlLogout = "UPDATE Player SET online = 0, gameId = NULL WHERE username = '" + username + "';";
        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();

            //Updates player to be offline
            statement.executeUpdate(sqlLogout);

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
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }finally {
            Cleaner.close(statement, null, connection);
        }
    }
    public static boolean logIn(){
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        String sql = "SELECT online FROM Player WHERE username = ?;";
        String login = "UPDATE Player SET online = ? WHERE username = ?;";
        try {
            connection = ConnectionPool.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            rs = statement.executeQuery();
            rs.next();

            int status = rs.getInt("online");
            int online = 1;
            statement = connection.prepareStatement(login);
            statement.setInt(1, online);
            statement.setString(2, username);
            statement.executeUpdate();
            return true;
            }catch (Exception e) {
            e.printStackTrace();
            return false;
        }finally {
            Cleaner.close(statement, null, connection);
        }
    }
}
