package com.snp.data.es.model;

import static org.springframework.data.elasticsearch.annotations.FieldType.Keyword;
import static org.springframework.data.elasticsearch.annotations.FieldType.Text;

import java.util.Map;

import org.json.JSONObject;
import static org.springframework.data.elasticsearch.annotations.FieldType.Date;
import static org.springframework.data.elasticsearch.annotations.FieldType.Object;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

@Document(indexName = "esmodel")
public class ESModel {
	
	@Id
	private String id;
	
	@Field(type = Keyword)
    private String table;
	
	@Field(type = Object)
	private Map<String, Object> data;
	
	//yyyy-MM-dd'T'HH:mm'Z'
	@Field(type = Date, format = DateFormat.custom, pattern = "yyyy-MM-dd'T'HH:mm'Z'")
	private String sys_updated_on;
	
	@Field(type = Date, format = DateFormat.custom, pattern = "yyyy-MM-dd'T'HH:mm'Z'")
	private String sys_created_on;
	
	@Field(type = Text)
	private String sys_created_by;
	
	@Field(type = Text)
	private String sys_updated_by;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}

	public String getSys_updated_on() {
		return sys_updated_on;
	}

	public void setSys_updated_on(String sys_updated_on) {
		this.sys_updated_on = sys_updated_on;
	}

	public String getSys_created_on() {
		return sys_created_on;
	}

	public void setSys_created_on(String sys_created_on) {
		this.sys_created_on = sys_created_on;
	}

	public String getSys_created_by() {
		return sys_created_by;
	}

	public void setSys_created_by(String sys_created_by) {
		this.sys_created_by = sys_created_by;
	}

	public String getSys_updated_by() {
		return sys_updated_by;
	}

	public void setSys_updated_by(String sys_updated_by) {
		this.sys_updated_by = sys_updated_by;
	}

	@Override
	public String toString() {
		return "ESModel [id=" + id + ", table=" + table + ", data=" + data + ", sys_updated_on=" + sys_updated_on
				+ ", sys_created_on=" + sys_created_on + "]";
	}
	
}
