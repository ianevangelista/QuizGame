package Controllers;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TimerCTest {
    TimerC timerc;

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
    public void checkCat() {
        int gameId = 58; //depends on gameId
        boolean result = timerc.checkCat(gameId);
        assertTrue(result);
    }

    @Test
    public void checkGameId() {
        String username = "ian";//depends if the user has a game and is player 1
        boolean result = timerc.checkGameId(username);
        assertTrue(result);
    }

    @Test
    public void timerCat() {
    }

    @Test
    public void turnOfTimer() {
    }
}