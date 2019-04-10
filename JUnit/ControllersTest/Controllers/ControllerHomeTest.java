package Controllers;

import org.junit.Test;
import static org.junit.Assert.*;

/*
    JUnit tests for ControllerHome class
 */
public class ControllerHomeTest {
    public ControllerHome ch = new ControllerHome();

    /*
        Testing get and set method for Username
     */
    @Test
    public void userNameTest() {
        String expResult = "helene";
        ch.setUserName(expResult);
        String result = ch.getUserName();
        assertEquals(expResult, result);
    }

    /*
        Testing the validateLogin method.
        Using a username and password to test.
     */
    @Test
    public void validateLoginTest() {
        String username = "juni";
        String password = "hei";
        boolean ans = ch.validateLogin(username, password);
        Logout.logOut();
        assertTrue(ans);
    }
}