package com.snp.model;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Record {
	
	private List<Field> fields;
	
	private String id;

	public Record(List<Field> fields) {
		this.fields = fields;
	}
	
	public Record() {
		
	}

	public List<Field> getFields() {
		return fields;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}
	
	public String getDisplayValue(String displayField) {

		for (Field f : fields) {
			if (!f.getName().toLowerCase().equals(displayField.toLowerCase())) {
				continue;
			}
			else {
				return f.getValue();
			}
		}
		return "-1";
	}
	
	public String getValue() {
		return this.id;
	}
	
	public void setValue(String id) {
		this.id = id;
	}
	
	public String getId() {
		for (Field f : fields) {
			if (f.getName().equals("SYS_ID")) {
				return f.getValue();
			}
		}
		return "Record has no sys id";
	}

	@Override
	public String toString() {
		return "Record [fields=" + fields + "]";
	}
	
	
		
}
