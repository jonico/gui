package com.collabnet.ccf.db;

public class Filter {
	private String columnName;
	private int filterType;
	private String value;
	private boolean stringValue = true;

	public final static int FILTER_TYPE_EQUAL = 0;
	public final static int FILTER_TYPE_NOT_EQUAL = 1;
	public final static int FILTER_TYPE_LESS_THAN = 2;
	public final static int FILTER_TYPE_LESS_THAN_OR_EQUAL_TO = 3;
	public final static int FILTER_TYPE_GREATER_THAN = 4;
	public final static int FILTER_TYPE_GREATER_THAN_OR_EQUAL_TO = 5;
	public final static int FILTER_TYPE_LIKE = 6;
	
	public Filter(String columnName, String value, boolean stringValue, int filterType) {
		this.columnName = columnName;
		this.filterType = filterType;
		this.value = value;
		this.stringValue = stringValue;
	}
	
	public Filter(String columnName, String value, boolean stringValue) {
		this(columnName, value, stringValue, FILTER_TYPE_EQUAL);
	}
	
	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public int getFilterType() {
		return filterType;
	}

	public void setFilterType(int filterType) {
		this.filterType = filterType;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public String toString() {
		StringBuffer expression = new StringBuffer(columnName);
		switch (filterType) {
		case FILTER_TYPE_EQUAL:
			expression.append(" = ");
			break;
		case FILTER_TYPE_NOT_EQUAL:
			expression.append(" != ");
			break;
		case FILTER_TYPE_LESS_THAN:
			expression.append(" < ");
			break;
		case FILTER_TYPE_LESS_THAN_OR_EQUAL_TO:
			expression.append(" <= ");
			break;
		case FILTER_TYPE_GREATER_THAN:
			expression.append(" > ");
			break;
		case FILTER_TYPE_GREATER_THAN_OR_EQUAL_TO:
			expression.append(" >= ");
			break;		
		case FILTER_TYPE_LIKE:
			expression.append(" LIKE ");
			break;						
		default:
			break;
		}
		if (stringValue) expression.append("'");
		expression.append(value);
		if (stringValue) expression.append("'");
		return expression.toString();
	}
	
	public static String getQuery(String sql, Filter[] filters) {
		StringBuffer query = new StringBuffer(sql);
		if (filters != null) {
			query.append(" WHERE ");
			for (int i = 0; i < filters.length; i++) {
				if (i > 0) query.append(" AND ");
				query.append(filters[i].toString());
			}
		}
		return query.toString();
	}
		
}
