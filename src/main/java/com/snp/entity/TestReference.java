package com.snp.entity;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.springframework.beans.factory.annotation.Autowired;

import com.snp.security.IAuthenticationFacade;

@Entity
@Table(name="example_reference")
public class TestReference {
	
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
		
	@OneToOne
	@JoinColumn(name="user")
	private User user;
	
	@Column(name = "sys_created_on", updatable = false)
	public Date createdOn;

	@Column(name = "sys_updated_on", updatable = false)
	public Date updatedOn;
	
	@Column(name = "sys_created_by")
	public String sys_created_by;
	
	@Column(name = "sys_updated_by")
	public String sys_updated_by;
	
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
	
	public TestReference() {
		
	}
	
	public TestReference(User user) {
		this.user = user;
	}
	
	public TestReference(User user, String name) {
		this.user = user;
		this.sys_created_by = name;
		this.sys_updated_by = name;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
}
