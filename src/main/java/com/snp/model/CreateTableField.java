package com.snp.model;

public class CreateTableField {
    private String fieldName;
    private String fieldType;
    private Boolean hasReference;
    private Reference referenceField;

    public CreateTableField() {
        // TODO document why this constructor is empty
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public Boolean getHasReference() {
        return hasReference;
    }

    public void setHasReference(Boolean hasReference) {
        this.hasReference = hasReference;
    }

    public Reference getReferenceField() {
        return referenceField;
    }

    public void setReferenceField(Reference referenceField) {
        this.referenceField = referenceField;
    }
}
