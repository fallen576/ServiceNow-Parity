package com.snp.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.snp.controller.AMB;
import com.snp.entity.Module;
import com.snp.model.Field;
import com.snp.model.Record;
import com.snp.model.Reference;
import com.snp.service.ModuleService;

import ch.qos.logback.classic.pattern.LineOfCallerConverter;


@Component
public class JdbcRepo {
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	private ModuleService modService;
	
	@Autowired
	private AMB amb;
	
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
		//does table exist?
		String sql = "CREATE TABLE " + data.getString("tableName").replaceAll(" ", "_").toLowerCase() + "( sys_id VARCHAR(255) default random_uuid(), "
				+ "primary key (sys_id), ";
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
				refTable = refObject.getString("display_value");
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
		
		jdbcTemplate.execute(sql);
		
		modService.save(new Module(data.getString("tableName"), data.getString("tableName").replaceAll(" ", "_").toLowerCase(), "SYS_ID"));		
    	amb.trigger(Collections.singletonMap(data.getString("tableName"), data.getString("tableName").replaceAll(" ", "_").toLowerCase()), "insertModule");
		
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
		//normalGet(this.jdbcTemplate.queryForList("select * from " + table + ";"), table);
		return this.jdbcTemplate.queryForList("select * from " + table + ";");
	}
	
	public Record normalGetNewRecord(String table) {
		List<String> columns = this.getColumns(table, false);
		List<Map<String, Object>> references = this.jdbcTemplate.queryForList(REFERENCES, table);
		List<Field> fields = new ArrayList();
		
		for (String col : columns) {
			Field field = new Field(col, null);
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
		
		for (Map<String, Object> row : rows) {
			
			List<Field> fields = new ArrayList<>();
			
		    for (Map.Entry<String, Object> i : row.entrySet()) {
		        String fieldLabel = i.getKey();
		        String fieldValue = (String) i.getValue();
		        Field tmpF = new Field(fieldLabel, fieldValue);
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
	
	public String lookup(String table, List<NameValuePair> params) {
		
		String query = SELECT_ALL + table + " WHERE ";
		String sys_id = "";
		if (params != null) {
			for (NameValuePair param : params) {
				  query += param.getName() + " = '" + param.getValue() + "'";
				  sys_id = param.getValue();
			}	
		}
		LOG.info(query);
		return jdbcTemplate.query(query,
				new ResultSetExtractor<String>() {
			
					public String extractData(ResultSet rs) throws SQLException, DataAccessException {
						return _process(rs);
					}
		});
	}
	
	public Map<String, Object> viewRecord(String table, String sys_id) {
		return jdbcTemplate.queryForList("select * from " + table + " where sys_id = ?", sys_id).get(0);
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
             String[] value = entry.getValue();
             
             if (key.equals("SYS_ID")) {
            	 where += value[0] + "'";
            	 id = value[0];
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
             values.add(entry.getValue()[0]);
             
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
        
        //jdbcTemplate.update(query, new Object[] {""}, keyHolder, new String[] {"SYS_ID"});
        String key = (String) keyHolder.getKeys().get("SYS_ID");
        LOG.info("SYS_ID " + key);
        //jdbcTemplate.update(query);
        return key;
        
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
}
