package Connection;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionClass {
    public Connection forbindelse;

    public Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            forbindelse = DriverManager.getConnection("jdbc:mysql://mysql.stud.iie.ntnu.no:3306/iaevange?user=iaevange&password=53BJMtne");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return forbindelse;
    }

}
