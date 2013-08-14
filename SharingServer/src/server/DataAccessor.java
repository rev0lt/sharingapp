package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;


public class DataAccessor {

    /* These variable values are used to setup the Connection object */

    static final String URL = "jdbc:mysql://97.107.141.233:3306/laser";
    static final String USER = "chupho_splat";
    static final String PASSWORD = "2bNKF7H5jS";
    static final String DRIVER = "com.mysql.jdbc.Driver";

    /* Creates a connection using the values listed above. */
    public Connection getConnection() throws SQLException {
        Connection con = null;
        try {
            Class.forName(DRIVER).newInstance();
            con = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return con;
    }

    // helper function
    private void closeThings(PreparedStatement pst, Connection con) {
        try {
            if (pst != null) pst.close();
            if (con != null) con.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}