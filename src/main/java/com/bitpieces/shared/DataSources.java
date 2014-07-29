package com.bitpieces.shared;

public class DataSources {
	public static final String HOME = System.getProperty( "user.home" );

	public static final String DEV_DB_PROP = HOME + "/bitpieces_dev_db.properties";
	public static final String STAGE_DB_PROP = HOME + "/bitpieces_stage_db.properties";
	public static final String PROD_DB_PROP = HOME + "/bitpieces_prod_db.properties";
	
	public static final String DEV_SESSION_FILE =  HOME + "/bitpieces_dev_session.cache";
}
