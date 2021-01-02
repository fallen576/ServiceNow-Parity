package com.snp.model;

public class Reference {
	
	public String value;
	public String displayValue;
	
	public Reference(String value, String displayValue) {
		this.value = value;
		this.displayValue = displayValue;
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
	
	
}
