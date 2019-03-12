package sample;

import javafx.event.ActionEvent;
import Connection.ConnectionPool;
import Connection.Cleaner;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static sample.ControllerHome.getUserName;


public class ControllerRefresh{

    public ChangeScene sceneChanger = new ChangeScene();
    public Cleaner cleaner = new Cleaner();

    private String username = getUserName();

    public void refresh(ActionEvent event) throws SQLException {
        Connection connection = null;
        Statement statement = null;

        String sql = "SELECT gameId FROM Player WHERE username = '" + username + "';";


        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            rs.next();

            int challenge = rs.getInt(1);

            if (challenge != 0) {
                sceneChanger.change(event, "Challenged.fxml");
            } else {
                sceneChanger.change(event, "ChallangeUser.fxml");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cleaner.close(statement, null, connection);
        }
    }
}
