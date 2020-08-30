package com.snp.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class DatabaseTable {
	
	private ArrayList<String> columns;
	private HashMap<Integer, String[]> rows;
	
	public DatabaseTable(ArrayList<String >columns, HashMap<Integer, String[]> rows) {
		this.columns = columns;
		this.rows = rows;
	}

	public DatabaseTable() {
		
	}

	public ArrayList<String> getColumns() {
		return columns;
	}

	public void setColumns(ArrayList<String> columns) {
		this.columns = columns;
	}

	public HashMap<Integer, String[]> getRows() {
		return rows;
	}

	public void setRows(HashMap<Integer, String[]> rows) {
		this.rows = rows;
	}

	@Override
	public String toString() {
		return "DatabaseTable [columns=" + columns + ", rows=" + rows + "]";
	}	
}
