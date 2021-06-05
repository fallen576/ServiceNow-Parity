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

import com.snp.security.IAuthenticationFacade;

@Entity
@Table(name="modules")
public class Module {
	
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
		
	@Column(name="module_name")
	private String moduleName;

	@Column(name="table_name")
	private String tableName;
	
	@Column(name="display")
	private String display;
	
	@Column(name = "sys_created_on", updatable = false)
	public Date createdOn;

	@Column(name = "sys_updated_on", updatable = false)
	public Date updatedOn;
	
	@Column(name = "sys_created_by")
	public String sys_created_by;
	
	@Column(name = "sys_updated_by")
	public String sys_updated_by;
	
	public Module() {
		
	}
	
	public Module(String module_name, String tableName) {
		this.moduleName = module_name;
		this.tableName = tableName;
		this.display = "SYS_ID";
	}

	public Module(String module_name, String tableName, String display) {
		this.moduleName = module_name;
		this.tableName = tableName;
		this.display = display;
	}
	
	public Module(String module_name, String tableName, String display, String user) {
		this.moduleName = module_name;
		this.tableName = tableName;
		this.display = display;
		this.sys_created_by = user;
		this.sys_updated_by = user;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void ModuleName(String module_name) {
		this.moduleName = module_name;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
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
		return "Module [id=" + id + ", moduleName=" + moduleName + ", tableName=" + tableName + ", display=" + display
				+ "]";
	}

}
