package com.snp.db;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
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
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import com.snp.controller.AMB;
import com.snp.entity.Module;
import com.snp.service.ModuleService;


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
	

	public String createTable(JSONObject data) throws Exception {
		//does table exist?
		String sql = "CREATE TABLE " + data.getString("tableName").replaceAll(" ", "_") + "( sys_id uuid default random_uuid(), ";
		JSONArray fields = data.getJSONArray("tableFields");
		
		for (int i = 0; i < fields.length(); i++) {
			JSONObject tmp = fields.getJSONObject(i);
			String name = tmp.getString("fieldName").replaceAll(" ", "_");
			String type = (tmp.getString("fieldType").equals("string") ? "varchar(255)" : "varchar(255)	");
			sql += " " + name  + " " + type + ","; 
		}
		
		sql = sql.substring(0, sql.length() - 1) + ");";
		
		jdbcTemplate.execute(sql);
		
		modService.save(new Module(data.getString("tableName"), data.getString("tableName").replaceAll(" ", "_")));		
    	amb.trigger(Collections.singletonMap(data.getString("tableName"), data.getString("tableName").replaceAll(" ", "_")), "insertModule");
		
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
	
	public List<Map<String, Object>> getRows(String table) {
		return this.jdbcTemplate.queryForList("select * from " + table + ";");
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
		if (params != null) {
			for (NameValuePair param : params) {
				  query += param.getName() + " = '" + param.getValue() + "'";
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
            	 query += key + "='" + value[0] + "', ";
             }
		}
        query = query.substring(0, query.length() - 2);
        query += where;
        
        //System.out.println("FINAL QUERY!!! " + query);
        
        jdbcTemplate.update(query);
        return id;
        
	}
	
	public int insertRecord(Map<?, ?> m, String table) {
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
        	query += "'" + i + "',";
        }
        
        query = query.substring(0, query.length() - 1) + ");";
        
        LOG.info("Final " + query);
        
        
        
        return jdbcTemplate.update(query);
        
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
