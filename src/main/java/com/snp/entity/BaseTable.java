package com.snp.entity;

import java.util.Date;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.springframework.security.core.context.SecurityContextHolder;

@MappedSuperclass
public class BaseTable {
	
	public BaseTable() {
		// TODO Auto-generated constructor stub
	}
	
	public BaseTable(String user) {
		this.sys_created_by = this.sys_updated_by = user;
	}

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
	void createdBy() {
		this.sys_updated_by = SecurityContextHolder.getContext().getAuthentication().getName();
		this.updatedOn = new Date();
	}

}
