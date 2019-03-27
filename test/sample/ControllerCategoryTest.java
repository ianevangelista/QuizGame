package sample;


import Connection.ConnectionPool;
import javafx.event.ActionEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;


public class ControllerCategoryTest {
    ControllerCategory cc = new ControllerCategory();

    @Test
    public void sceneHome() {
    }

    @Test
    public void chooseCategory1Test() {
        ActionEvent event = new ActionEvent();
        cc.initialize();
        boolean ans = cc.chooseCategory1(event);
        assertTrue(ans);
    }

    @Test
    public void chooseCategory2Test() {
        ActionEvent event = new ActionEvent();
        cc.initialize();
        boolean ans = cc.chooseCategory2(event);
        assertTrue(ans);
    }

    @Test
    public void chooseCategory3Test() {
        ActionEvent event = new ActionEvent();
        cc.initialize();
        boolean ans = cc.chooseCategory3(event);
        assertTrue(ans);
    }

    @Test
    public void initialize() {
        boolean ans = cc.initialize();
        assertTrue(ans);
    }
}
