package com.snp.model;

import java.util.List;

public class Record {
	
	private List<Field> fields;

	public Record(List<Field> fields) {
		this.fields = fields;
	}

	public List<Field> getFields() {
		return fields;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}
	
	public String getDisplayValue(String displayField) {
		for (Field f : fields) {
			if (!f.getName().equals(displayField)) {
				continue;
			}
			else {
				return f.getValue();
			}
		}
		return "-1";
	}
		
}
