package com.snp.model;

import java.util.Arrays;

public class Field {

	public String name;
	public String value;
	public boolean hasReference;
	public Reference reference;
	public boolean readOnly;
	private String[] readOnlyFields = new String[] {"password", "sys_id", "sys_created_on", "sys_created_by", "sys_updated_by"};
	//private boolean required;
	private String[] nonRequiredFields = new String[] {"sys_id", "sys_created_on", "sys_created_by", "sys_updated_by"};
	
	public Field (String name, String value) {
		this.name = name;
		this.value = value;
		this.readOnly = (Arrays.stream(readOnlyFields).anyMatch(name.toLowerCase()::equals)) ? true : false;
		this.hasReference = false;
	}
	

	public Field(String name, String value, boolean hasReference) {
		this.name = name;
		this.value = value;
		this.hasReference = hasReference;
		this.readOnly = (Arrays.stream(readOnlyFields).anyMatch(name.toLowerCase()::equals)) ? true : false;
	}
	
	public Field(String name, String value, boolean hasReference, boolean readOnly) {
		this.name = name;
		this.value = value;
		this.hasReference = hasReference;
		this.readOnly = readOnly;
	}

	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getValue() {
		return value;
	}


	public void setValue(String value) {
		this.value = value;
	}


	public boolean isHasReference() {
		return hasReference;
	}


	public void setHasReference(boolean hasReference) {
		this.hasReference = hasReference;
	}


	public Reference getReference() {
		return reference;
	}


	public void setReference(Reference reference) {
		this.hasReference = reference != null ? true : false;
		this.reference = reference;
	}
	
	public boolean isReadOnly() {
		return readOnly;
	}


	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	
	public boolean isRequired() {
		return (Arrays.stream(nonRequiredFields).anyMatch(this.name.toLowerCase()::equals)) ? false : true;
	}
	
	@Override
	public String toString() {
		return "Field [name=" + name + ", value=" + value + ", hasReference=" + hasReference + ", reference="
				+ reference + ", readOnly=" + readOnly + "]";
	}
	
}
