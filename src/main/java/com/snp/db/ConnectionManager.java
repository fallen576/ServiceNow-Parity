/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.snp.db;

import com.snp.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Ben
 */
public class ConnectionManager {

    @Autowired
    private static LogService LOG;
    static final String JDBC_DRIVER = "org.h2.Driver";   
    static final String DB_URL = "jdbc:h2:mem:snp_db;";  
    static final String USER = "sa"; 
    static final String PASS = ""; 
    private static Connection conn;
    
    public static Connection getConnection() throws SQLException {
        
        try {
            Class.forName(JDBC_DRIVER);
            try {
                conn = DriverManager.getConnection(DB_URL, USER, PASS);
            } catch(SQLException e) {
                LOG.error(e.toString(), ConnectionManager.class.getName());
            }
        } catch (ClassNotFoundException e) {
            LOG.error(e.toString(), ConnectionManager.class.getName());
        }
        return conn;
    }
}
