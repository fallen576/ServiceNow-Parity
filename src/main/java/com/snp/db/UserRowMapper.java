package com.snp.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Logger;

import org.springframework.jdbc.core.RowMapper;

import com.snp.entity.User;

import jdk.internal.org.jline.utils.Log;

public class UserRowMapper implements RowMapper<User> {
	
	private static final Logger LOG =
	        Logger.getLogger(UserRowMapper.class.getPackage().getName());
	
	@Override
	public User mapRow(ResultSet rs, int rowNum) throws SQLException {
		LOG.info("in mapper?");
		User user = new User();
		String first_name = rs.getString("FIRST_NAME");
		String last_name = rs.getString("LAST_NAME");
		String password = rs.getString("PASSWORD");
		String id = rs.getString("SYS_ID");
		String user_name = rs.getString("USER_NAME");
		user.setFirstName(first_name);
		user.setLastName(last_name);
		user.setPassword(password);
		user.setValue(UUID.fromString(id));
		user.setUser_name(user_name);
		return user;
	}

}
