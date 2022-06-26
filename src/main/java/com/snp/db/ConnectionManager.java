/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.snp.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ben
 */
public class ConnectionManager {
    
    static final String JDBC_DRIVER = "org.h2.Driver";   
    static final String DB_URL = "jdbc:h2:mem:snp_db;";  
    static final String USER = "sa"; 
    static final String PASS = ""; 
    private static Connection conn;
    private static final Logger LOG = 
			Logger.getLogger(ConnectionManager.class.
                                getPackage().getName());
    
    public static Connection getConnection() throws SQLException {
        
        try {
            Class.forName(JDBC_DRIVER);
            try {
                conn = DriverManager.getConnection(DB_URL, USER, PASS);
            } catch(SQLException e) {
                LOG.log(Level.SEVERE, e.toString());
            }
        } catch (ClassNotFoundException e) {
            LOG.log(Level.SEVERE, e.toString());
        }
        return conn;
    }
}
