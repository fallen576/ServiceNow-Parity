package com.snp.model;

public class Reference {
	
	public String value;
	public String displayValue;
	public String table;

	public Reference() {

	}
	
	public Reference(String value, String displayValue) {
		this.value = value;
		this.displayValue = displayValue;
	}
	
	public Reference(String value, String displayValue, String table) {
		this.value = value;
		this.displayValue = displayValue;
		this.table = table;
	}
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getDisplayValue() {
		return displayValue;
	}
	public void setDisplayValue(String displayValue) {
		this.displayValue = displayValue;
	}
	
	public void setTable(String table) {
		this.table = table;
	}
	
	public String getTable() {
		return table;
	}

	@Override
	public String toString() {
		return "Reference [value=" + value + ", displayValue=" + displayValue + "]";
	}
	
	
}
