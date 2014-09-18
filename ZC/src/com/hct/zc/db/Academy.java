package com.hct.zc.db;

public class Academy {

	public static final String _ID = "_id";
	public static final String TID = "tid";
	public static final String NAME = "name";
	public static final String ID = "id";
	public static final String AREA = "area";
	public static final String IMG = "img";
	public static final String SELECT = "selected";

	public static final String TABLE_NAME = "subscribe";
	public static final String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE_NAME
			+ " (" + _ID + " integer primary key autoincrement, " + TID
			+ " text not null, " + NAME + " text not null, " + ID
			+ " text not null, " + AREA + " text not null, " + IMG
			+ " text not null, " + SELECT + " integer not null);";

}
