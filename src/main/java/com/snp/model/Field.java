package com.snp.model;

public class Field {

	public String name;
	public String value;
	public boolean hasReference;
	public Reference reference;
	
	public Field (String name, String value) {
		this.name = name;
		this.value = value;
		this.hasReference = false;
	}
	

	public Field(String name, String value, boolean hasReference) {
		this.name = name;
		this.value = value;
		this.hasReference = hasReference;
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
	
	@Override
	public String toString() {
		return "Field [name=" + name + ", value=" + value + ", hasReference=" + hasReference + ", reference="
				+ reference + "]";
	}
	
}
