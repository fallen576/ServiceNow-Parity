package com.snp.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

import com.snp.model.*;
import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;
import com.snp.controller.AMB;
import com.snp.data.es.model.ESModel;
import com.snp.data.es.repository.ESModelRepository;
import com.snp.entity.ListLayout;
import com.snp.entity.Module;
import com.snp.entity.Role;
import com.snp.entity.SystemLog;
import com.snp.entity.SystemLog.LogLevel;
import com.snp.entity.User;
import com.snp.security.IAuthenticationFacade;
import com.snp.service.LogService;
import com.snp.service.ModuleService;
import com.snp.service.RoleService;
import com.snp.service.UserPreferenceService;
import org.springframework.util.StringUtils;

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
	private LogService LOG;
	
	@Autowired
	private UserPreferenceService listControl;
	
	@Autowired
	private ESModelRepository modelRepo;
	
	private final String SELECT_ALL = "SELECT * FROM ";
	private String LIMIT = " LIMIT ?";
	private final String REFERENCES = "SELECT FKCOLUMN_NAME, PKTABLE_NAME FROM INFORMATION_SCHEMA.CROSS_REFERENCES WHERE lower(FKTABLE_NAME) = lower(?)";

	public String createTable(CreateTable tableData) {
		String table = tableData.getTableName();
		String sql = "CREATE TABLE " + table + "( sys_id VARCHAR(255) default random_uuid(), "
				+ "sys_created_on TIMESTAMP(23), "
				+ "sys_updated_on TIMESTAMP(23), "
				+ "sys_created_by VARCHAR(255), "
				+ "sys_updated_by VARCHAR(255), "
				+ " primary key (sys_id), ";
		ArrayList<Object> params = new ArrayList();

		for (CreateTableField field : tableData.getTableFields()) {
			String name = field.getFieldName();
			String type = field.getFieldType();

			switch (type) {
				case "integer":
					sql += " " + name  + " INT  ,";
					params.add(name);
					break;
				case "reference":
					sql += " " + name  + " varchar(255)  ,";
					sql += "foreign key (" + name + ") references " + field.getReferenceField().getValue() + "(sys_id) ON DELETE CASCADE ,";
					params.add(name);
					params.add(field.getReferenceField().getTable());
					break;
				default:
					sql += " " + name  + " varchar(255)  ,";
					params.add(name);
			}
		}

		sql = sql.substring(0, sql.length() - 1) + ");";
		jdbcTemplate.execute(sql);

		//create module for table, notify front end to update modules list and create a role for the table
		modService.save(new Module(StringUtils.capitalize(table.replaceAll("_", " ")), table, "SYS_ID", auth.getAuthentication().getName()));
		amb.trigger(Collections.singletonMap(StringUtils.capitalize(table.replaceAll("_", " ")), table), "insertModule");
		roleService.save(new Role(table, "CRUD access on " + table , auth.getAuthentication().getName()));

		LOG.info(sql, JdbcRepo.class.getName());		
		return sql;
	}

	@Deprecated
	public String createTable(JSONObject data) throws Exception {
		LOG.info("Current user " + auth.getAuthentication().getName() + " " + auth.getAuthentication().getCredentials() + " new way? " 
				+ SecurityContextHolder.getContext().getAuthentication().getName(), JdbcRepo.class.getName());
		//does table exist?
		String tableName = "tableName";
		String table = data.getString(tableName).replaceAll(" ", "_").toLowerCase();
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
		
		LOG.info("SAFE SQL " + safeSql, JdbcRepo.class.getName());
		LOG.save(new SystemLog(LogLevel.Info, "Generating custom table using sql: " + sql, "CreateTable", auth.getAuthentication().getName()));
		
		jdbcTemplate.execute(sql);
		
		//create module for table, notify front end to update modules list and create a role for the table
		modService.save(new Module(data.getString(tableName), table, "SYS_ID", auth.getAuthentication().getName()));
    	amb.trigger(Collections.singletonMap(data.getString(tableName), data.getString(tableName).replaceAll(" ", "_").toLowerCase()), "insertModule");
    	roleService.save(new Role(table, "CRUD access on " + table , auth.getAuthentication().getName()));
		
		LOG.info(sql, JdbcRepo.class.getName());
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
		String COLUMNS = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE lower(TABLE_NAME)= ? "
				+ "AND TABLE_SCHEMA='PUBLIC' AND COLUMN_NAME != 'SYS_ID';";
		String COLUMNS_WITH_SYS_ID = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE lower(TABLE_NAME)= ? "
				+ "AND TABLE_SCHEMA='PUBLIC';";
		return (List<String>) this.jdbcTemplate.query((withSysId) ? COLUMNS_WITH_SYS_ID : COLUMNS, new Object[] {table}, (rs, rowNum) -> rs.getString("COLUMN_NAME"));
	}
	
	public String getDisplay(String table) {
		String display = this.jdbcTemplate.queryForObject("SELECT DISPLAY FROM MODULES WHERE TABLE_NAME = lower(?)", String.class, table.toLowerCase());
		return display;
	}
	
	public List<Map<String, Object>> getRow(String table, String id) {
		//check if user has a preference for this list layout
		String username = auth.getAuthentication().getName();
		List<String> fields = this.getUserPrefFields(username, table);
		
		if (!fields.isEmpty()) {
			if (!fields.contains("sys_id")) {
				fields.add(0, "sys_id");
			}
			return this.jdbcTemplate.queryForList("select " + String.join(",", fields) + " from " + table + " WHERE sys_id = ?", id);
		}
		return this.jdbcTemplate.queryForList("select * from " + table + " WHERE sys_id = ?", id);
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
			if (col.equalsIgnoreCase("sys_created_on") ||
					col.equalsIgnoreCase("sys_updated_on")) field.setReadOnly(true);
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
		List<Map<String, Object>> rows = (id == null) ? this.getRows(table) : this.getRow(table, id);
		
		List<Record> records = new ArrayList<>();
		
		List<String> userPref = this.getUserPrefFields(auth.getAuthentication().getName(), table);
		
		for (Map<String, Object> row : rows) {
			
			List<Field> fields = new ArrayList<>();
			Record tmp = new Record();
			
		    for (Map.Entry<String, Object> i : row.entrySet()) {
		        String fieldLabel = i.getKey();
		        String fieldValue = (i.getValue() == null) ? "" : i.getValue().toString();
		        
		        if (fieldLabel.equalsIgnoreCase("sys_id")) {
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
			String DELETE_RECORD = "DELETE FROM table WHERE SYS_ID = 'pid'";
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
				return jsonArray.toString();
			}
		}); 

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
		LOG.info(query, JdbcRepo.class.getName());
		
		//New type of lookup
		//get a more normalized object structure to describe the table schema (i.e field types, display values etc...)	
		List<Map<String, Object>> references = this.jdbcTemplate.queryForList(REFERENCES, table);		
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(query);
		List<Record> records = new ArrayList<>();
		
		for (Map<String, Object> row : rows) {
			
			List<Field> fields = new ArrayList<>();
			
		    for (Map.Entry<String, Object> i : row.entrySet()) {
		        String fieldLabel = i.getKey();
				String fieldValue = i.getValue().toString();

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
	
	public String updateRecord(HashMap<String, Object> fields, String table, String id) {
		
		String query = "UPDATE " + table + " SET ";
		
	    for (Map.Entry<String, Object> entry : fields.entrySet()) {
	    	String fieldName = entry.getKey();
	    	Object fieldValue = entry.getValue();
	    	
	    	query += fieldName + " = '" + fieldValue + "', ";
	    }
	    
	    query += "sys_updated_on = '" + new Timestamp(System.currentTimeMillis()) + "', "
	    		+ "sys_updated_by = '" + auth.getAuthentication().getName() + "' ";
	    
	    //query = query.substring(0, query.length() - 2);
	    
	    query += "WHERE sys_id = '" + id + "'";
	    
	    LOG.info(query, JdbcRepo.class.getName());
	    
	    jdbcTemplate.update(query);
	    
	    return id;
	}
	
	@Deprecated
	public String updateRecord(Map<?, ?> m, String table) {
		Set<?> s = m.entrySet();
        Iterator<?> it = s.iterator();
        String query = "UPDATE " + table + " SET ";
        String where = " WHERE SYS_ID = '" ;
        String id = "";
        
        while(it.hasNext()){
        	Map.Entry<String,String[]> entry = (Map.Entry<String,String[]>)it.next();
        	 String key = entry.getKey();
        	 if (key.equalsIgnoreCase("sys_created_on")) {
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
	
	private String getDateTimeStamp() {
		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
		df.setTimeZone(tz);
		return df.format(new Date());
	}
	
	public String insertRecord(Map<?, ?> m, String table) {
		Set<?> s = m.entrySet();
        Iterator<?> it = s.iterator();
        String query = "INSERT INTO " + table + " (";
        ArrayList<String> values = new ArrayList<>();
        JSONObject json = new JSONObject();
        ESModel model = new ESModel();
        //insert into users(FIRST_NAME,LAST_NAME,USER_NAME) VALUES ('a','b','c')
        
        while(it.hasNext()){
        	 Map.Entry<String,String[]> entry = (Map.Entry<String,String[]>)it.next();
        	 String col = entry.getKey();
        	 if (col.toLowerCase().equals("sys_created_on")) {
        		 String date = (new Timestamp(System.currentTimeMillis()).toString());
        		 values.add(date);
        		 model.setSys_created_on(getDateTimeStamp());
        	 }
        	 else if (col.toLowerCase().equals("sys_updated_on")) {
        		 String date = (new Timestamp(System.currentTimeMillis()).toString());
        		 values.add(date);
        		 model.setSys_updated_on(getDateTimeStamp());
        	 }
        	 else if (col.toLowerCase().equals("sys_created_by")) {
        		 String who = auth.getAuthentication().getName();
        		 values.add(who);
        		 model.setSys_created_by(who);
        	 }
        	 else if (col.toLowerCase().equals("sys_updated_by")) {
        		 String who = auth.getAuthentication().getName();
        		 values.add(who);
        		 model.setSys_updated_by(who);
        	 }
        	 else if (col.toLowerCase().equals("password") && table.toLowerCase().equals("users")) {
        		 String pass = BCrypt.hashpw(m.get("USER_NAME").toString(), BCrypt.gensalt());
        		 values.add(pass);
        		 json.append(col, pass);
        	 }
         	 else {
         		 String data = entry.getValue()[0];
        		 values.add(data);
        		 json.append(col, data);
        	 }
             
             query += col + ",";
		}
        
        query = query.substring(0, query.length() - 1) + ") VALUES (";
        
        LOG.info("query after cols " + query, JdbcRepo.class.getName());
        
        
        for (String i : values) {
        	query += "'" + i.trim() + "',";
        }
        
        query = query.substring(0, query.length() - 1) + ");";
        
        LOG.info("Final " + query, JdbcRepo.class.getName());
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        
        final String sql = query;
        
        jdbcTemplate.update(connection -> {
        	PreparedStatement ps = connection
        	          .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        	
        	return ps;
        }, keyHolder);
        
		String key = (String) keyHolder.getKeys().get("SCOPE_IDENTITY()");
		
		try {
			model.setId(key);
			model.setData(json);
			model.setTable(table);
			modelRepo.save(model);
		} catch (Exception e) {
			LOG.error("Unable to create elastic search index for " + table + ". Error is " + e, JdbcRepo.class.getName());
		}
		
        return key;
        
	}
	
	public void setUserPrefFields(String user_name, String table, String[] fields) {
		User user = findUserByUserName(user_name);		
		ListLayout existingList = listControl.findByTableAndUser(table, user);
		if (existingList != null ) {
			existingList.setList(fields);
			listControl.save(existingList);
		}
		else {
			listControl.save(new ListLayout(table, fields, user));
		}
	}
	
	public List<String> getUserPrefFields(String user_name, String table) {
		List<String> fields = new ArrayList<String>();
		User user = findUserByUserName(user_name);
		List<Map<String, Object>> map = jdbcTemplate.queryForList("SELECT b.list from sys_user_preference AS a JOIN"
				+ " sys_user_field as b ON a.sys_id = b.sys_id WHERE a.user_ref = ? AND a.table = ?",
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
		User user = null;
		try {
			
			user = jdbcTemplate.queryForObject("select * from users where user_name = ?", new UserRowMapper(), new Object[] {user_name});
			
		} catch (Exception e) {
			LOG.error("in catch block of db.... something went wrong locating user "
					+ e.getMessage() + e.getLocalizedMessage(), JdbcRepo.class.getName());
		}
		return user;
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
