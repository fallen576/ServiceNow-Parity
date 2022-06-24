package com.snp.rhino;

import com.snp.db.ConnectionManager;
import java.util.logging.Logger;
import com.snp.entity.User;
import com.snp.service.UserService;
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
    private Map<String, String> propertyMap;
    private ArrayList<String> queryString = new ArrayList<String>();
    private ArrayList<String> queryValues = new ArrayList<String>();

    public GlideRecord(String table) {
        LOG.info("in con");
        this.table = table;
        LOG.info("after con " + this.table);
    }

    public GlideRecord() {
        LOG.info("no con");
    }

    public void addQuery(String field, String operator, String value) {
        if (!this.queryString.isEmpty()) {
            this.queryString.add(" AND " + field + " " + operator + " ?");
        } else {
            this.queryString.add(field + " " + operator + " ?");
        }
        this.queryValues.add(value);
    }

    public void addORQuery(String field, String operator, String value) {
        this.queryString.add(" OR " + field + " " + operator + " ?");
        this.queryValues.add(value);
    }

    public String query() throws SQLException {
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
        return json.toString();
    }

    public String getQuery() throws SQLException {
        PreparedStatement stmt
                = buildQueryStatement(ConnectionManager.getConnection());

        LOG.info("in getquery " + stmt.toString());
        return stmt.toString();
    }

    public void setValue(String field, String value) {
        this.propertyMap.put(field, value);
    }

    public void update() {

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
