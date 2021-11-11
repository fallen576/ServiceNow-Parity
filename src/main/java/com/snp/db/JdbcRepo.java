package com.snp.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.snp.controller.AMB;
import com.snp.entity.HasRole;
import com.snp.entity.ListLayout;
import com.snp.entity.Module;
import com.snp.entity.Role;
import com.snp.entity.SystemLog;
import com.snp.entity.SystemLog.LogLevel;
import com.snp.entity.User;
import com.snp.model.Field;
import com.snp.model.Record;
import com.snp.model.Reference;
import com.snp.security.IAuthenticationFacade;
import com.snp.service.LogService;
import com.snp.service.ModuleService;
import com.snp.service.RoleService;
import com.snp.service.UserPreferenceService;

import ch.qos.logback.classic.pattern.LineOfCallerConverter;


@Component
public class JdbcRepo {
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	private ModuleService modService;
	
	@Autowired
	private AMB amb;
	
	@Autowired
    private IAuthenticationFacade auth;
	
	@Autowired
	private RoleService roleService;
	
	@Autowired
	private LogService logService;
	
	@Autowired
	private UserPreferenceService listControl;
	
	private static final Logger LOG =
	        Logger.getLogger(JdbcRepo.class.getPackage().getName());
	
	private String SELECT_ALL = "SELECT * FROM ";
	private String LIMIT = " LIMIT ?";
	private String COLUMNS = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE lower(TABLE_NAME)= ? "
			+ "AND TABLE_SCHEMA='PUBLIC' AND COLUMN_NAME != 'SYS_ID';";
	private String COLUMNS_WITH_SYS_ID = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE lower(TABLE_NAME)= ? "
			+ "AND TABLE_SCHEMA='PUBLIC';";
	private String DELETE_RECORD = "DELETE FROM table WHERE SYS_ID = 'pid'";
	private String SINGLE_RECORD = "SELECT * FROM (?) WHERE sys_id = (?)";
	private String REFERENCES = "SELECT FKCOLUMN_NAME, PKTABLE_NAME FROM INFORMATION_SCHEMA.CROSS_REFERENCES WHERE lower(FKTABLE_NAME) = lower(?)";

	public String createTable(JSONObject data) throws Exception {
		LOG.info("Current user " + auth.getAuthentication().getName() + " " + auth.getAuthentication().getCredentials() + " new way? " 
				+ SecurityContextHolder.getContext().getAuthentication().getName());
		//does table exist?
		String table = data.getString("tableName").replaceAll(" ", "_").toLowerCase();
		String sql = "CREATE TABLE " + table + "( sys_id VARCHAR(255) default random_uuid(), "
				+ "sys_created_on TIMESTAMP(23), "
				+ "sys_updated_on TIMESTAMP(23), "
				+ "sys_created_by VARCHAR(255), "
				+ "sys_updated_by VARCHAR(255), "
				+ " primary key (sys_id), ";
		String safeSql = sql;
		ArrayList<Object> params = new ArrayList();

		JSONArray fields = data.getJSONArray("tableFields");
		HashMap<String, String> map = new HashMap<>();
		
		for (int i = 0; i < fields.length(); i++) {	
			JSONObject tmp = fields.getJSONObject(i);
			String name = tmp.getString("fieldName").replaceAll(" ", "_");
			String type = tmp.getString("fieldType");
			String refTable = "";
			if (type.equals("reference")) {
				JSONObject refObject = tmp.getJSONObject("referenceField");
				//refTable = refObject.getString("display_value");
				refTable = refObject.getString("value");
				map.put(name, refTable);
			}
			
			sql += " " + name  + " varchar(255)  ,"; 
			safeSql += " ? varchar(255)  ,"; 
			params.add(name);
		}
		for (Map.Entry mapElement : map.entrySet()) { 
			String k = (String) mapElement.getKey();
			String v = (String) mapElement.getValue();
			
			sql += "foreign key (" + k + ") references " + v + "(sys_id) ON DELETE CASCADE ,";
			safeSql += "foreign key (?) references  ? (sys_id) ON DELETE CASCADE ,";
			params.add(k);
			params.add(v);
		}
		
		sql = sql.substring(0, sql.length() - 1) + ");";
		
		LOG.info("SAFE SQL " + safeSql);
		logService.save(new SystemLog(LogLevel.Info, "Generating custom table using sql: " + sql, "CreateTable", auth.getAuthentication().getName()));
		
		jdbcTemplate.execute(sql);
		
		//create module for table, notify front end to update modules list and create a role for the table
		modService.save(new Module(data.getString("tableName"), table, "SYS_ID", auth.getAuthentication().getName()));		
    	amb.trigger(Collections.singletonMap(data.getString("tableName"), data.getString("tableName").replaceAll(" ", "_").toLowerCase()), "insertModule");
    	roleService.save(new Role(table, "CRUD access on " + table , auth.getAuthentication().getName()));
		
		LOG.info(sql);
		return sql;
	}
	
	public String deleteTable() {
		String sql = "";
		
		return sql;
	}
	
	public String modifyTable() {
		String sql = "";
		
		return sql;
	}
	
	public List<String> getColumns(String table, boolean withSysId) {		
		return (List<String>) this.jdbcTemplate.query((withSysId) ? COLUMNS_WITH_SYS_ID : COLUMNS, new Object[] {table}, (rs, rowNum) -> rs.getString("COLUMN_NAME"));
	}
	
	public String getDisplay(String table) {
		String display = this.jdbcTemplate.queryForObject("SELECT DISPLAY FROM MODULES WHERE TABLE_NAME = lower(?)", String.class, table.toLowerCase());
		return display;
	}
	
	public List<Map<String, Object>> getRows(String table) {
		//check if user has a preference for this list layout
		String username = auth.getAuthentication().getName();
		List<String> fields = this.getUserPrefFields(username, table);
		
		if (!fields.isEmpty()) {
			if (!fields.contains("sys_id")) {
				fields.add(0, "sys_id");
			}
			return this.jdbcTemplate.queryForList("select " + String.join(",", fields) + " from " + table + ";");
		}
		
		return this.jdbcTemplate.queryForList("select * from " + table + ";");
	}
	
	public Record normalGetNewRecord(String table) {
		List<String> columns = this.getColumns(table, false);
		List<Map<String, Object>> references = this.jdbcTemplate.queryForList(REFERENCES, table);
		List<Field> fields = new ArrayList();
		
		for (String col : columns) {
			Field field = new Field(col, null);
			if (col.toLowerCase().equals("sys_created_on") || col.toLowerCase().equals("sys_updated_on")) field.setReadOnly(true);
			field.setReference(null);
			fields.add(field);
		
			for (Map<String, Object> reference : references) {
				String reference_field = (String) reference.get("FKCOLUMN_NAME");
				String referenced_table = (String) reference.get("PKTABLE_NAME");
				
				if (field.getName().equals(reference_field)) {
					field.setReference(new Reference(null, null, referenced_table));
				}
			}
		}
		Record record = new Record(fields);
		return record;
	}
	
	public List<Record> normalGet(String table, String id) {
		
		//get a more normalized object structure to describe the table schema (i.e field types, display values etc...)	
		List<Map<String, Object>> references = this.jdbcTemplate.queryForList(REFERENCES, table);		
		List<Map<String, Object>> rows = (id == null) ? 
				this.getRows(table) :
				jdbcTemplate.queryForList("select * from " + table + " where sys_id = ?", id);
		List<Record> records = new ArrayList<>();
		
		List<String> userPref = this.getUserPrefFields(auth.getAuthentication().getName(), table);
		
		for (Map<String, Object> row : rows) {
			
			List<Field> fields = new ArrayList<>();
			Record tmp = new Record();
			
		    for (Map.Entry<String, Object> i : row.entrySet()) {
		        String fieldLabel = i.getKey();
		        String fieldValue = (i.getValue() == null) ? "" : i.getValue().toString();
		        
		        if (fieldLabel.toLowerCase().equals("sys_id")) {
		        	tmp.setValue(fieldValue);
		        	
		        	if (!userPref.isEmpty() && !userPref.contains("sys_id")) {
		        		//skip displaying sys_id in list view, user has a preference and does not specify it. but we still need the id.
		        		continue;
		        	}
		        	
		        }
		       
		        Field tmpF = new Field(fieldLabel, fieldValue);
		        tmpF.setReference(null);
		        if (fieldLabel.toLowerCase().equals("sys_created_on") || fieldLabel.toLowerCase().equals("sys_updated_on")) tmpF.setReadOnly(true);
		        fields.add(tmpF);
		        
	        	//which field, create reference
	        	for (Map<String, Object> reference : references) {
	    		    String reference_field = (String) reference.get("FKCOLUMN_NAME");
	    		    String referenced_table = (String) reference.get("PKTABLE_NAME");
	    		    
	    		    if (tmpF.getName().equals(reference_field)) {
	    		    	tmpF.setReference(new Reference(tmpF.getValue(), _getDisplayValue(referenced_table, tmpF.getValue()), referenced_table)); 	
	    		    }
    		    }
    		    
		    }
		    tmp.setFields(fields);
		    records.add(tmp);
		}
		
		return records;
	}
	
	public String _getDisplayValue(String table, String value) {		
		String displayField = jdbcTemplate.queryForObject("select display from modules where TABLE_NAME = lower(?)", String.class, table);
		
		String display = jdbcTemplate.queryForObject("select * from " + table + " where SYS_ID = ?",
				(rs, rowNum) -> rs.getString(displayField.toUpperCase()),
				value);
		
		return display;
	}
	
	public void delete(String table, String id) {		
		try {
		this.jdbcTemplate.update(DELETE_RECORD.replace("table",  table).replace("pid",  id));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String findAll(String table) {		
		String sql = SELECT_ALL;
		
		String answer = jdbcTemplate.query(sql + table, new ResultSetExtractor<String>() {
			public String extractData(ResultSet rs) throws SQLException, DataAccessException {
			
				ResultSetMetaData md = rs.getMetaData();
				
				JSONArray jsonArray = new JSONArray();
				while (rs.next()) {
					JSONObject json = new JSONObject();
					for (int i = 1; i <= md.getColumnCount(); i++) {
						json.put(md.getColumnName(i), rs.getObject(i));
					}
					jsonArray.put(json);
				}
				//System.out.println("JSON: " + jsonArray);
				return jsonArray.toString();
			}
		}); 
		
		//System.out.println("Ben's answer is " + answer);
		return answer;
	}
	
	public List<Record> lookup(String table, List<NameValuePair> params) {
		
		String query = SELECT_ALL + table + " WHERE ";
		String sys_id = "";
		if (params != null) {
			for (NameValuePair param : params) {
				  query += param.getName() + " = '" + param.getValue() + "'";
				  sys_id = param.getValue();
			}	
		}
		LOG.info(query);
		
		//New type of lookup
		//get a more normalized object structure to describe the table schema (i.e field types, display values etc...)	
		List<Map<String, Object>> references = this.jdbcTemplate.queryForList(REFERENCES, table);		
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(query);
		List<Record> records = new ArrayList<>();
		
		for (Map<String, Object> row : rows) {
			
			List<Field> fields = new ArrayList<>();
			
		    for (Map.Entry<String, Object> i : row.entrySet()) {
		        String fieldLabel = i.getKey();
		        String fieldValue = (String) i.getValue();
		        Field tmpF = new Field(fieldLabel, fieldValue);
		        if (fieldLabel.toLowerCase().equals("sys_created_on") || fieldLabel.toLowerCase().equals("sys_updated_on")) tmpF.setReadOnly(true);
		        tmpF.setReference(null);
		        fields.add(tmpF);
		        
	        	//which field, create reference
	        	for (Map<String, Object> reference : references) {
	    		    String reference_field = (String) reference.get("FKCOLUMN_NAME");
	    		    String referenced_table = (String) reference.get("PKTABLE_NAME");
	    		    
	    		    if (tmpF.getName().equals(reference_field)) {
	    		    	tmpF.setReference(new Reference(tmpF.getValue(), _getDisplayValue(referenced_table, tmpF.getValue()), referenced_table)); 	
	    		    }
    		    }
    		    
		    }
		    records.add(new Record(fields));
		}
		
		return records;

	}
	
	public Map<String, Object> viewRecord(String table, String sys_id) {
		return jdbcTemplate.queryForList("select * from " + table + " where sys_id = ?", sys_id).get(0);
	}
	
	public List<Map<String, Object>> getFields(String table) {
		return jdbcTemplate.queryForList("show columns from " + table);
	}
	
	public String updateRecord(Map<?, ?> m, String table) {
		Set<?> s = m.entrySet();
        Iterator<?> it = s.iterator();
        String query = "UPDATE " + table + " SET ";
        String where = " WHERE SYS_ID = '" ;
        String id = "";
        
        while(it.hasNext()){
        	Map.Entry<String,String[]> entry = (Map.Entry<String,String[]>)it.next();
        	 String key = entry.getKey();
        	 if (key.toLowerCase().equals("sys_created_on")) {
        		 continue;
        	 }

             String[] value = entry.getValue();
             
             if (key.equals("SYS_ID")) {
            	 where += value[0] + "'";
            	 id = value[0];
             }
             else if (key.toLowerCase().equals("sys_updated_on")) {
            	 query += key + "='" + new Timestamp(System.currentTimeMillis()) + "', ";
             }
             else if (key.toLowerCase().equals("sys_updated_by")) {
            	 query += key + "='" + auth.getAuthentication().getName() + "', ";
             }
             else {
            	 query += key + "='" + value[0].trim() + "', ";
             }
		}
        query = query.substring(0, query.length() - 2);
        query += where;
        
        //System.out.println("FINAL QUERY!!! " + query);
        
        jdbcTemplate.update(query);
        return id;
        
	}
	
	public String insertRecord(Map<?, ?> m, String table) {
		Set<?> s = m.entrySet();
        Iterator<?> it = s.iterator();
        String query = "INSERT INTO " + table + " (";
        ArrayList<String> values = new ArrayList<>();
        //insert into users(FIRST_NAME,LAST_NAME,USER_NAME) VALUES ('a','b','c')
        
        while(it.hasNext()){
        	 Map.Entry<String,String[]> entry = (Map.Entry<String,String[]>)it.next();
        	 String col = entry.getKey();
        	 if (col.toLowerCase().equals("sys_created_on") || col.toLowerCase().equals("sys_updated_on")) {
        		 values.add((new Timestamp(System.currentTimeMillis()).toString()));
        	 }
        	 else if (col.toLowerCase().equals("sys_created_by") || col.toLowerCase().equals("sys_updated_by")) {
        		 values.add(auth.getAuthentication().getName());
        	 }
         	 else {
        		 values.add(entry.getValue()[0]);
        	 }
             
             query += col + ",";
		}
        
        query = query.substring(0, query.length() - 1) + ") VALUES (";
        
        LOG.info("query after cols " + query);
        
        
        for (String i : values) {
        	query += "'" + i.trim() + "',";
        }
        
        query = query.substring(0, query.length() - 1) + ");";
        
        LOG.info("Final " + query);
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        
        final String sql = query;
        
        jdbcTemplate.update(connection -> {
        	PreparedStatement ps = connection
        	          .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        	
        	return ps;
        }, keyHolder);
        
		String key = (String) keyHolder.getKeys().get("SCOPE_IDENTITY()");
        return key;
        
	}
	
	public void setUserPrefFields(String user_name, String table, String[] fields) {
		User user = findUserByUserName(user_name);		
		ListLayout existingList = listControl.findByTableAndUser(table, user);
		if (existingList != null ) {
			LOG.info(existingList.getId() + " " + existingList.table + " " + existingList.getList() + " " + existingList.getUser());
			LOG.info(fields.toString());
			existingList.setList(fields);
			listControl.save(existingList);
		}
		else {
			listControl.save(new ListLayout(table, fields, user));
		}
	}
	
	public List<String> getUserPrefFields(String user_name, String table) {
		//SELECT a.table, a.user_ref, b.list from sys_user_preference AS a JOIN sys_user_field as b ON a.sys_id = b.name WHERE a.sys_id = 'd29b13fb-ef66-476f-b0c5-232679cd179a'
		List<String> fields = new ArrayList<String>();
		User user = findUserByUserName(user_name);
		//LOG.info("SELECT a.table, a.user_ref, b.list from sys_user_preference AS a JOIN sys_user_field as b ON a.sys_id = b.name WHERE a.sys_id = " + user.getValue().toString());
		List<Map<String, Object>> map = jdbcTemplate.queryForList("SELECT b.list from sys_user_preference AS a JOIN"
				+ " sys_user_field as b ON a.sys_id = b.name WHERE a.user_ref = ? AND a.table = ?",
				user.getValue().toString(),
				table);
		
		for (Map<String, Object> rs : map) {
		    for (Map.Entry<String, Object> entry : rs.entrySet()) {
		        String value = entry.getValue().toString();
		        fields.add(value);
		    }
		}
		return fields;
	}
	
	public User findUserByUserName(String user_name) {
		LOG.info("attempting to locat user... " + user_name);
		User user = null;
		try {
			
			user = jdbcTemplate.queryForObject("select * from users where user_name = ?", new UserRowMapper(), new Object[] {user_name});
			
		} catch (Exception e) {
			LOG.info("in catch block of db.... something went wrong locating user " + e.getMessage() + e.getLocalizedMessage());
		}
		LOG.info("user has been located...");
		return user;
	}
	
	private String _process(ResultSet rs) throws SQLException {
		ResultSetMetaData md = rs.getMetaData();
		
		JSONArray jsonArray = new JSONArray();
		while (rs.next()) {
			JSONObject json = new JSONObject();
			for (int i = 1; i <= md.getColumnCount(); i++) {
				json.put(md.getColumnName(i), rs.getObject(i));
			}
			jsonArray.put(json);
		}
		return jsonArray.toString();
	}

	public List<Role> findRoleForUser(UUID value) {
		
		@SuppressWarnings("deprecation")
		List<String> has_roles = jdbcTemplate.query("Select * from sys_user_has_role where user = ?",
				new Object[] {value},
				(rs, rowNum) -> rs.getString("role"));
		
		String inSql = String.join(",", Collections.nCopies(has_roles.size(), "?"));
		
		@SuppressWarnings("deprecation")
		List<Role> roles = jdbcTemplate.query(String.format("select name, description from sys_user_role where SYS_ID IN (%s)", inSql),
				has_roles.toArray(),
						(rs, rowNum) -> new Role(rs.getString("name"), rs.getString("description")));
		return roles;
	}
}
