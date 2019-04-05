package Controllers;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TimerCTest {
    TimerC t;
    @Before
    public void setUp() throws Exception {
        t = new TimerC();
    }

    @Test
    public void initialize() {
        boolean ans = t.initialize();
        assertTrue(ans);
    }

    @Test
    public void timerCat() {
    }

    @Test
    public void checkCat() {
    }

    @Test
    public void checkGameId() {
    }

    @Test
    public void turnOfTimer() {
    }
}