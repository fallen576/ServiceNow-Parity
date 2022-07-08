package com.snp.model;

import java.util.List;

public class CreateTable {

    private String tableName;
    private List<CreateTableField> tableFields;

    public CreateTable() {

    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<CreateTableField> getTableFields() {
        return tableFields;
    }

    public void setTableFields(List<CreateTableField> tableFields) {
        this.tableFields = tableFields;
    }
}
