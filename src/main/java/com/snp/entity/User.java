package com.snp.entity;

import java.util.Date;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;

import com.snp.security.IAuthenticationFacade;

@Entity
@Table(name="users")
public class User {
	
	//@Autowired
    //private IAuthenticationFacade auth;

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
	
	@Column(name = "sys_created_on", updatable = false)
	public Date createdOn;

	@Column(name = "sys_updated_on", updatable = false)
	public Date updatedOn;
	
	@Column(name = "sys_created_by")
	public String sys_created_by;
	
	@Column(name = "sys_updated_by")
	public String sys_updated_by;
	
	public User() {
		
	}
	
	public User(String first_name, String last_name, String user_name) {
		this.firstName = first_name;
		this.lastName = last_name;
		this.user_name = user_name;
		this.password = BCrypt.hashpw(user_name, BCrypt.gensalt());
	}
	
	public User(String first_name, String last_name, String user_name, String name) {
		this.firstName = first_name;
		this.lastName = last_name;
		this.user_name = user_name;
		this.password = BCrypt.hashpw(user_name, BCrypt.gensalt());
		this.sys_created_by = name;
		this.sys_updated_by = name;
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
	
	@PrePersist
	void createdAt() {
		this.createdOn = this.updatedOn = new Date();
	}

	@PreUpdate
	void updatedAt() {
		this.updatedOn = new Date();
	}
	public void setSys_created_by(String name) {
		this.sys_created_by = name;
	}

	public void setSys_updated_by(String name) {
		this.sys_updated_by = name;
	}
	@Override
	public String toString() {
		return this.getUser_name() + " " + this.getFirstName() + " " + this.getLastName() + " " + this.getPassword();
	}
}
