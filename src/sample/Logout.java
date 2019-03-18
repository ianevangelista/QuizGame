package sample;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import Connection.ConnectionPool;
import Connection.Cleaner;
import static sample.ControllerHome.getUserName;

public class Logout {
    private static String username = getUserName();

    public static boolean logOut(){
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = "SELECT online FROM Player WHERE username = ?;";
        String logout = "UPDATE Player SET online = ? WHERE username = ?;";
        try {
            connection = ConnectionPool.getConnection();
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();
            if(!(rs.next()))return false;
            int status = rs.getInt("online");
            if(status == 1){
                int offline = 0;
                pstmt = connection.prepareStatement(logout);
                pstmt.setInt(1, offline);
                pstmt.setString(2, username);
                pstmt.executeUpdate();
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }finally {
            Cleaner.close(pstmt, null, connection);
        }
    }
    public static boolean logIn(){
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = "SELECT online FROM Player WHERE username = ?;";
        String login = "UPDATE Player SET online = ? WHERE username = ?;";
        try {
            connection = ConnectionPool.getConnection();
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();
            rs.next();

            int status = rs.getInt("online");
            int online = 1;
            pstmt = connection.prepareStatement(login);
            pstmt.setInt(1, online);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
            return true;
            }catch (Exception e) {
            e.printStackTrace();
            return false;
        }finally {
            Cleaner.close(pstmt, null, connection);
        }
    }
}
