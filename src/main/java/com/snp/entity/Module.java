package com.snp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="modules")
public class Module {
	@Id
	private String module_name;

	@Column(name="table_name")
	private String tableName;
	
	public Module() {
		
	}

	public Module(String module_name, String tableName) {
		this.module_name = module_name;
		this.tableName = tableName;
	}

	public String getModule_name() {
		return module_name;
	}

	public void setModule_name(String module_name) {
		this.module_name = module_name;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	@Override
	public String toString() {
		return "module [module_name=" + module_name + ", tableName=" + tableName + "]";
	}
}
