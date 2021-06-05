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

@Entity
@Table(name="modules")
public class Module {
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

	@Override
	public String toString() {
		return "Module [id=" + id + ", moduleName=" + moduleName + ", tableName=" + tableName + ", display=" + display
				+ "]";
	}

}
