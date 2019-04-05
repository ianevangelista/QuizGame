package Controllers;

import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import Connection.ConnectionPool;
import Connection.Cleaner;

import static org.junit.Assert.*;

public class TimerCTest {
    TimerC timerc;
    Connection connection = null;
    Statement statement = null;
    ResultSet rs = null;

    @Before
    public void setUp() throws Exception {
        timerc = new TimerC();
    }

    @Test
    public void initialize() {
        boolean ans = timerc.initialize();
        assertTrue(ans);
    }

    @Test
    public void timerCat() {
    }

    //@Test
    /*public void checkCat() {
        int gameId = 40;

            boolean resultat = timerc.checkCat(gameId);
            rs = statement.executeQuery(sqlCategory);
            if(rs.next()) {
                if(rs.getInt(1) ==category) {
                    statement.executeUpdate(sqlDelete);
                    assertTrue(resultat);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            Cleaner.close(statement, null, connection);
        }
    }*/

    @Test
    public void checkGameId() {
    }

    @Test
    public void turnOfTimer() {
    }
}