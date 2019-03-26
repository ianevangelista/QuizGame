package sample;

import Connection.ConnectionPool;
import com.mysql.cj.protocol.Resultset;
import javafx.event.ActionEvent;
import javafx.scene.control.TextFormatter;
import jdk.nashorn.internal.runtime.ECMAException;
import org.junit.Before;

import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.rmi.activation.ActivationInstantiator;
import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ControllerHomeTest {
    ControllerHome ch;
    ChangeScene cs;
    ConnectionPool cp;
    PreparedStatement pstmt;

    @Before
        public void setUp() {
        ch = new ControllerHome();
        cs = new ChangeScene();
        cp = new ConnectionPool();
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



    @Test
    public void playerLoginTest() {
        ActionEvent event = new ActionEvent();
        ch.setUserName("helene");
        when(ch.usernameGetText()).thenReturn("helene");
        boolean ans = ch.playerLogin(event);
        assertTrue(ans);
    }


}