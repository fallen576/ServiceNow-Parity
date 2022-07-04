package com.snp.rhino;

import com.snp.db.ConnectionManager;
import java.util.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.lang.StringBuilder;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import com.snp.service.LogService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

public class GlideRecord {
    private String table;
    private ArrayList<String> queryString = new ArrayList<>();
    private ArrayList<String> queryValues = new ArrayList<>();
    private ArrayList<String> whereString = new ArrayList<>();
    private ArrayList<String> whereValues = new ArrayList<>();
    private ArrayList<String> propertyString = new ArrayList<>();
    private ArrayList<String> propertyValues = new ArrayList<>();

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
                    json.put(md.getColumnName(m), rs.getString(m));
                }
            }

        } catch (Exception e) {
            ////LOG.error(Arrays.toString(e.getStackTrace()), GlideRecord.class.getName());
        } finally {
            stmt.close();
            con.close();
        }
        ////LOG.info(json.toString(), GlideRecord.class.getName());
        return json;
    }

    public String getQuery() throws SQLException {
        PreparedStatement stmt
                = buildQueryStatement(ConnectionManager.getConnection());

        //LOG.info("in getQuery " + stmt.toString(), GlideRecord.class.getName());
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
        	//LOG.error(e.getMessage(), GlideRecord.class.getName());
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
	    	//LOG.info("cmd update section is " + cmd, GlideRecord.class.getName());
	    	
	    	
	    	for (int i = 0; i < this.whereString.size(); i++) {
	    		cmd += this.whereString.get(i);
	    	}
	    	//LOG.info("cmd update and where section is " + cmd, GlideRecord.class.getName());
	    	
	    		stmt = con.prepareStatement(cmd);
	    	
	    	//LOG.info("valid statement " + stmt.toString(), GlideRecord.class.getName());
	    	
	    	for (int i = 0; i < this.propertyValues.size(); i++) {
	    		stmt.setString(i+1, this.propertyValues.get(i));
	    	}
	    	
	    	//LOG.info("stmt update section is " + stmt.toString(), GlideRecord.class.getName());
	    	
	    	for (int i = 0; i< this.whereValues.size(); i++) {
	    		stmt.setString(i+1+this.propertyValues.size(), this.whereValues.get(i));
	    	}
    	} catch (Exception e) {
    		//LOG.info(Arrays.toString(e.getStackTrace()), GlideRecord.class.getName());
    	}
    	//LOG.info("returning from buildUpdateStatement: " + stmt.toString(), GlideRecord.class.getName());
    	return stmt;
    }

    private PreparedStatement buildQueryStatement(Connection con) throws SQLException {

        PreparedStatement stmt = null;
        StringBuilder query = new StringBuilder("SELECT * FROM " + this.table + " WHERE ");

        for (int i = 0; i < this.queryString.size(); i++) {
            query.append(this.queryString.get(i));
        }
        
        //LOG.info("query is " + query, GlideRecord.class.getName());
        
        stmt = con.prepareStatement(query.toString());

        //LOG.info("stmt is " + stmt.toString(), GlideRecord.class.getName());
        
        for (int i = 0; i < this.queryValues.size(); i++) {
            //LOG.info("setString " + i + " - " + this.queryValues.get(i), GlideRecord.class.getName());
            stmt.setString(i+1, this.queryValues.get(i));
        }
        
        //LOG.info("returning from buildQueryStatement " + stmt.toString(), GlideRecord.class.getName());
        return stmt;
    }
}
