package com.snp.rhino;

import com.snp.db.ConnectionManager;
import java.util.logging.Logger;
import com.snp.entity.User;
import com.snp.service.UserService;
import com.sun.xml.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.logging.Level;
import java.lang.StringBuilder;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Arrays;
import org.json.JSONObject;

public class GlideRecord {

    private static final Logger LOG
            = Logger.getLogger(GlideRecord.class.getPackage().getName());
    private String table;
    private ArrayList<String> queryString = new ArrayList<String>();
    private ArrayList<String> queryValues = new ArrayList<String>();
    private ArrayList<String> whereString = new ArrayList<String>();
    private ArrayList<String> whereValues = new ArrayList<String>();
    private ArrayList<String> propertyString = new ArrayList<String>();
    private ArrayList<String> propertyValues = new ArrayList<String>();
    

    public GlideRecord(String table) {
        this.table = table;
    }

    public GlideRecord() {
    	
    }

    public void addQuery(String field, String operator, String value) {
        if (!this.queryString.isEmpty()) {
            this.queryString.add(" AND " + field + " " + operator + " ?");
        } else {
            this.queryString.add(field + " " + operator + " ?");
        }
        this.queryValues.add(value);
    }

    public void addOrQuery(String field, String operator, String value) {
        this.queryString.add(" OR " + field + " " + operator + " ?");
        this.queryValues.add(value);
    }
    
    public void addWhere(String field, String operator, String value) {
    	if (!this.whereString.isEmpty()) {
            this.whereString.add(" AND " + field + " " + operator + " ?");
        } else {
            this.whereString.add(field + " " + operator + " ?");
        }
        this.whereValues.add(value);
    }
    
    public void addOrWhere(String field, String operator, String value) {
        this.whereString.add(" OR " + field + " " + operator + " ?");
        this.whereValues.add(value);
    }
    
    public void setValue(String field, String value) {
        this.propertyString.add(field + " = ?");
        this.propertyValues.add(value);
    }

    public JSONObject query() throws SQLException {
        JSONObject json = new JSONObject();
        Connection con = null;
        PreparedStatement stmt = null;
        
        try {
            con = ConnectionManager.getConnection();
            stmt = buildQueryStatement(con);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                ResultSetMetaData md = rs.getMetaData();

                for (int m = 1; m <= md.getColumnCount(); m++) {
                    LOG.log(Level.INFO, "{0} - {1}",
                            new Object[]{md.getColumnName(m), rs.getString(m)});
                    json.put(md.getColumnName(m), rs.getString(m));
                }
            }

        } catch (Exception e) {
            LOG.log(Level.SEVERE, Arrays.toString(e.getStackTrace()));
        } finally {
            stmt.close();
            con.close();
        }
        LOG.info(json.toString());
        return json;
    }

    public String getQuery() throws SQLException {
        PreparedStatement stmt
                = buildQueryStatement(ConnectionManager.getConnection());

        LOG.info("in getquery " + stmt.toString());
        return stmt.toString();
    }

    public int update() throws SQLException {
    	
    	Connection con = null;
        PreparedStatement stmt = null;
        int ans = -1;
    	
        try {
        	con = ConnectionManager.getConnection();
        	stmt = buildUpdateStatement(con);
        	ans = stmt.executeUpdate();
        } catch (Exception e) {
        	LOG.log(Level.SEVERE, e.getMessage());
        } finally {
        	stmt.close();
        	con.close();
        }
    	
    	return ans;
    }
    
    private PreparedStatement buildUpdateStatement(Connection con) {
    	PreparedStatement stmt = null;
    	String cmd = "UPDATE " + this.table + " SET ";
    	try {
	    	for (int i = 0; i < this.propertyString.size(); i++) {
	    		cmd += this.propertyString.get(i) + ",";
	    	}
	    	
	    	cmd = cmd.substring(0, cmd.length() - 1) + " WHERE ";    	
	    	LOG.info("cmd update section is " + cmd);
	    	
	    	
	    	for (int i = 0; i < this.whereString.size(); i++) {
	    		cmd += this.whereString.get(i);
	    	}
	    	LOG.info("cmd update and where section is " + cmd);
	    	
	    		stmt = con.prepareStatement(cmd);
	    	
	    	LOG.info("valid statement " + stmt.toString());
	    	
	    	for (int i = 0; i < this.propertyValues.size(); i++) {
	    		stmt.setString(i+1, this.propertyValues.get(i));
	    	}
	    	
	    	LOG.info("stmt update section is " + stmt.toString());
	    	
	    	for (int i = 0; i< this.whereValues.size(); i++) {
	    		stmt.setString(i+1+this.propertyValues.size(), this.whereValues.get(i));
	    	}
    	} catch (Exception e) {
    		LOG.log(Level.SEVERE, Arrays.toString(e.getStackTrace()));
    	}
    	LOG.info("returning from buildUpdateStatement: " + stmt.toString());
    	return stmt;
    }

    private PreparedStatement buildQueryStatement(Connection con) throws SQLException {

        PreparedStatement stmt = null;
        String query = "SELECT * FROM " + this.table + " WHERE ";

        for (int i = 0; i < this.queryString.size(); i++) {
            query += this.queryString.get(i);
        }
        
        LOG.info("query is " + query);
        
        stmt = con.prepareStatement(query);

        LOG.info("stmt is " + stmt.toString());
        
        for (int i = 0; i < this.queryValues.size(); i++) {
            LOG.info("setString " + i + " - " + this.queryValues.get(i));
            stmt.setString(i+1, this.queryValues.get(i));
        }
        
        LOG.info("returning from buildQueryStatement " + stmt.toString());
        return stmt;
    }
    /*
	public void createUser(String fName, String lName, String uName) {
		Connection conn = null; 
	      Statement stmt = null; 
	      try { 
	         // STEP 1: Register JDBC driver 
	         Class.forName(JDBC_DRIVER); 
	             
	         //STEP 2: Open a connection 
	         LOG.info("Connecting to database..."); 
	         conn = DriverManager.getConnection(DB_URL,USER,PASS);  
	         
	         //STEP 3: Execute a query 
	         LOG.info("Creating table in given database..."); 
	         stmt = conn.createStatement(); 
	         String sql =  "CREATE TABLE   REGISTRATION " + 
	            "(id INTEGER not NULL, " + 
	            " first VARCHAR(255), " +  
	            " last VARCHAR(255), " +  
	            " age INTEGER, " +  
	            " PRIMARY KEY ( id ))";  
	         stmt.executeUpdate(sql);
	         LOG.info("Created table in given database..."); 
	         
	         // STEP 4: Clean-up environment 
	         stmt.close(); 
	         conn.close(); 
	      } catch(SQLException se) { 
	         //Handle errors for JDBC 
	         se.printStackTrace(); 
	      } catch(Exception e) { 
	         //Handle errors for Class.forName 
	         e.printStackTrace(); 
	      } finally { 
	         //finally block used to close resources 
	         try{ 
	            if(stmt!=null) stmt.close(); 
	         } catch(SQLException se2) { 
	         } // nothing we can do 
	         try { 
	            if(conn!=null) conn.close(); 
	         } catch(SQLException se){ 
	            se.printStackTrace(); 
	         } //end finally try 
	      } //end try 
	      LOG.info("Goodbye!");
	   }
     */
}
