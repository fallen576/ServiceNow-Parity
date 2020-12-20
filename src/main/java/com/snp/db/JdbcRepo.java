package com.snp.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
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
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.snp.entity.Module;
import com.snp.model.DatabaseTable;
import com.snp.service.ModuleService;


@Component
public class JdbcRepo {
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	private ModuleService modService;
	
	private static final Logger LOG =
	        Logger.getLogger(JdbcRepo.class.getPackage().getName());
	
	private String SELECT_ALL = "SELECT * FROM ";
	private String LIMIT = " LIMIT ?";
	private String COLUMNS = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE lower(TABLE_NAME)='?' "
			+ "AND TABLE_SCHEMA='PUBLIC' AND COLUMN_NAME != 'SYS_ID';";
	

	public String createTable(JSONObject data) {
		//does table exist?
		String sql = "CREATE TABLE " + data.getString("tableName").replaceAll(" ", "_") + "( sys_id uuid default random_uuid(), ";
		JSONArray fields = data.getJSONArray("tableFields");
		
		for (int i = 0; i < fields.length(); i++) {
			JSONObject tmp = fields.getJSONObject(i);
			String name = tmp.getString("fieldName").replaceAll(" ", "_");
			String type = (tmp.getString("fieldType").equals("string") ? "varchar(255)" : "int");
			
			LOG.info(name);
			LOG.info(type);
			
			sql += " " + name  + " " + type + ","; 
		}
		
		sql = sql.substring(0, sql.length() - 1) + ");";
		
		jdbcTemplate.execute(sql);
		
		modService.save(new Module(data.getString("tableName"), data.getString("tableName").replaceAll(" ", "_")));
		
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
	
	public String getTableColumns(String table) {
		String sql = COLUMNS.replace("?", table);
		LOG.info("query " + sql);
		
		return jdbcTemplate.query(sql,
				new ResultSetExtractor<String>() {
			
					public String extractData(ResultSet rs) throws SQLException, DataAccessException {
						List<String> json = new ArrayList<>();
						while (rs.next()) {
							json.add(rs.getString("COLUMN_NAME"));
						}
						return json.toString();
					}
		});
	}
	
	public String findAll(String table) {		
		String sql = this.SELECT_ALL;
		
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
	
	public String insertRecord(Map<?, ?> m, String table) {
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
