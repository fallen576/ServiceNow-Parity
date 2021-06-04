package com.snp.entity;

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.springframework.security.crypto.bcrypt.BCrypt;

@Entity
@Table(name="users")
public class User {

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(
	        name = "UUID",
	        strategy = "org.hibernate.id.UUIDGenerator"
	)
	@Column(name = "sys_id", updatable = false, nullable = false)
	@ColumnDefault("random_uuid()")
	@Type(type = "uuid-char")
	private UUID id;
	
	@Column(name="user_name")
	private String user_name;
	
	@Column(name="first_name")
	private String firstName;
	
	@Column(name="last_name")
	private String lastName;
	
	@Column(name="password")
	private String password;
	
	public User() {
		
	}
	
	public User(String first_name, String last_name, String user_name) {
		this.firstName = first_name;
		this.lastName = last_name;
		this.user_name = user_name;
		this.password = BCrypt.hashpw(user_name, BCrypt.gensalt());
	}
	
	public UUID getValue() {
		return id;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}	
	
	public String getPassword() {
		return this.password;
	}
	
	public void setValue(UUID id) {
		this.id = id;
	}
	
	public void setPassword(String pass) {
		this.password = pass;
	}
	
	@Override
	public String toString() {
		return this.getUser_name() + " " + this.getFirstName() + " " + this.getLastName() + " " + this.getPassword();
	}
}
