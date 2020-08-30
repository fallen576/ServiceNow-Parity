package com.snp.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class JdbcRepo {
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	private String SELECT_ALL = "SELECT * FROM ";
	private String LIMIT = " LIMIT ?";

	public String createTable(String table, HashMap<String, String> columns) {
		//does table exist?
		String sql = "CREATE TABLE ";
		
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
	
	public void findAll(String table) {
		ArrayList<String> columns = new ArrayList<>();
		
		String sql = this.SELECT_ALL;
		
		String answer = jdbcTemplate.query(sql + table, new ResultSetExtractor<String>() {
			public String extractData(ResultSet rs) throws SQLException, DataAccessException {
				ResultSetMetaData md = rs.getMetaData();
				
				while (rs.next()) {
					for (int i = 1; i <= md.getColumnCount(); i++) {
						columns.add(md.getColumnName(i));
						System.out.println("Column Name: " + md.getColumnName(i));
						System.out.println("Value " + rs.getObject(i));
					}
				}
				
				return "ben";
			}
		}); 
		System.out.println("Ben's answer is " + answer);
	}
}
