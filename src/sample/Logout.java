package sample;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import Connection.ConnectionPool;
import Connection.Cleaner;

import static sample.ControllerOpponent.resetGameId;
import static sample.ControllerHome.getUserName;
import static sample.ControllerQuestion.findUser;
import static sample.ControllerOpponent.getGameId;

public class Logout {
    private static String username = getUserName();

    public static boolean logOut(){
        Connection connection = null;
        Statement statement = null;
        ResultSet rsPlayersWithTheGameId = null;
        ResultSet rs = null;
        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();

            //Updates player to be offline
            String sqlLogout = "UPDATE Player SET online = 0 WHERE username = '" + username + "';";
            statement.executeUpdate(sqlLogout);

            //Automatically lose a game if you log out
            int gameId = getGameId();
            if(gameId != 0) {
                //Add to games lost
                String sqlLoseGame = "UPDATE Player SET gamesLost = gamesLost+1, gameId = NULL WHERE username = '" + username + "';";
                statement.executeUpdate(sqlLoseGame);

                String player = findUser();
                String sqlRageQuitGame;
                if (player.equals("player1")) {
                    sqlRageQuitGame = "UPDATE Game SET p1Points = 0, p1Finished = 1 WHERE gameId =" + gameId + ";"; //TODO COPY NICER CODE FROM SCENEHOME IN CONTROLERQUESTION
                } else {
                    sqlRageQuitGame = "UPDATE Game SET p2Points = 0, p2Finished = 1 WHERE gameId =" + gameId + ";";
                }
                statement.executeUpdate(sqlRageQuitGame);

                //Delete game if other player is finished and give opponent points
                String sqlCheckIfOtherPlayerHasLeft = "SELECT gameId FROM Player WHERE gameId =" + gameId + ";";
                rsPlayersWithTheGameId = statement.executeQuery(sqlCheckIfOtherPlayerHasLeft);

                if (!rsPlayersWithTheGameId.next()) {
                    if (player.equals("player1")) {
                        String getOpponentPoints = "SELECT player2, p2Points FROM Game WHERE gameId = " + gameId;
                        rs = statement.executeQuery(getOpponentPoints);
                        rs.next();
                        int opponentPoints = rs.getInt("p2Points");
                        String sqlUpdatePlayerScore = "UPDATE Player SET points= points +" + opponentPoints + ", gamesWon = gamesWon+1  WHERE username ='" + rs.getString("player2") + "';";
                        statement.executeUpdate(sqlUpdatePlayerScore);
                    } else {
                        String getOpponentPoints = "SELECT player1, p1Points FROM Game WHERE gameId = " + gameId;
                        rs = statement.executeQuery(getOpponentPoints);
                        rs.next();
                        int opponentPoints = rs.getInt("p1Points");
                        String sqlUpdatePlayerScore = "UPDATE Player SET points= points +" + opponentPoints + ", gamesWon = gamesWon+1 WHERE username ='" + rs.getString("player1") + "';";
                        statement.executeUpdate(sqlUpdatePlayerScore);
                    }
                    String sqlDeleteGame = "DELETE FROM Game WHERE gameId =" + gameId + ";";
                    statement.executeUpdate(sqlDeleteGame);
                    resetGameId();
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }finally {
            Cleaner.close(statement, rsPlayersWithTheGameId, null);
            Cleaner.close(null, rs, connection);
        }
        return true;
    }
    public static boolean logIn(){
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = ConnectionPool.getConnection();
            int online = 1;
            String login = "UPDATE Player SET online = ? WHERE username = ?;";
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
