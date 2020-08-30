package com.snp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="modules")
public class Module {
	@Id
	@Column(name="module_name")
	private String moduleName;

	@Column(name="table_name")
	private String tableName;
	
	public Module() {
		
	}

	public Module(String module_name, String tableName) {
		this.moduleName = module_name;
		this.tableName = tableName;
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
	
	@Override
	public String toString() {
		return "module [module_name=" + moduleName + ", tableName=" + tableName + "]";
	}
}
