package com.snp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="sys_log")
public class SystemLog extends BaseTable {
	
	public SystemLog(LogLevel logLevel, String message, String source, String user) {
		super(user);
		this.level = logLevel.name();
		this.message = message;
		this.source = source;
	}
	
	public SystemLog(LogLevel logLevel, String message, String source) {
		super("administrator");
		this.level = logLevel.name();
		this.message = message;
		this.source = source;
	}
	
	@Column(name = "message", length=8000)
	public String message;
	
	@Column(name = "level")
	public String level;
	
	@Column
	public String source;
	

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}
	
	public enum LogLevel {
		Info,
		Warning,
		Error
	}
	
}
