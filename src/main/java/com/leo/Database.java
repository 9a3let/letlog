package com.leo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    public void createdb(String path) throws SQLException {

        Connection conn = DriverManager.getConnection("jdbc:sqlite:"+path);  
        if (conn != null) {  
            System.out.println("A new database has been created.");  
        }  

    }
}
