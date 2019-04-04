package Controllers;

import Connection.ConnectionPool;

import javafx.event.ActionEvent;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;


public class ControllerHomeTest {
    ControllerHome ch;
    ChangeScene cs;

    @Before
        public void setUp() {
        ch = new ControllerHome();
        cs = new ChangeScene();
    }

    @Test
    public void sceneInfoTest() {
        ActionEvent event = new ActionEvent();
        boolean ans = ch.sceneInfo(event);
        assertTrue(ans);
    }

    @Test
    public void sceneHomeTest() {
        ActionEvent event = new ActionEvent();
        boolean ans = ch.sceneHome(event);
        assertTrue(ans);
    }

    @Test
    public void registerTest() {
        ActionEvent event = new ActionEvent();
        boolean ans = ch.register(event);
        assertTrue(ans);
    }

    @Test
    public void feedbackTest() {
        ActionEvent event = new ActionEvent();
        boolean ans = ch.feedback(event);
        assertTrue(ans);
    }

    //testing get and set method for UserName
    @Test
    public void userNameTest() {
        String expResult = "helene";
        ch.setUserName(expResult);
        String result = ch.getUserName();
        assertEquals(expResult, result);
    }

    //
    @Test
    public void validateLoginTest() {
        String username = "helene";
        String password = "helene";
        boolean ans = ch.validateLogin(username, password);
        Logout.logOut();
        assertTrue(ans);
    }


}