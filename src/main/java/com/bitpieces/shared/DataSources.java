package com.bitpieces.shared;

import java.util.Arrays;
import java.util.List;

public class DataSources {
	public static final String HOME = System.getProperty( "user.home" );

	public static final String DEV_DB_PROP = HOME + "/bitpieces_dev_db.properties";
	public static final String STAGE_DB_PROP = HOME + "/bitpieces_stage_db.properties";
	public static final String PROD_DB_PROP = HOME + "/bitpieces_prod_db.properties";
	
	public static final String DEV_SESSION_FILE =  HOME + "/bitpieces_dev_session.cache";
	
	public static final Integer DEV_WEB_PORT = 4567;
	
	
	public static final List<String> ALLOW_ACCESS_ADDRESSES = Arrays.asList("http://localhost", "http://68.56.177.238:8080");
	
	public static final String COINBASE_PROP = HOME + "/coinbase.properties";
}
