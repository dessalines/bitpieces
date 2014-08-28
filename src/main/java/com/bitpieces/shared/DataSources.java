package com.bitpieces.shared;

import java.util.Arrays;
import java.util.List;

/**
 * Not much
 * @author tyler
 *
 */
public class DataSources {
	public static final String HOME = System.getProperty( "user.home" );
	
	public static final String EMAIL_PROP = HOME + "/bitpieces_email.properties";
	
	public static final String DEV_DB_PROP = HOME + "/bitpieces_dev_db.properties";
	public static final String STAGE_DB_PROP = HOME + "/bitpieces_stage_db.properties";
	public static final String PROD_DB_PROP = HOME + "/bitpieces_prod_db.properties";
	
	// The keystore location for the backend
	public static final String KEYSTORE = HOME + "/keystore.jks";
	public static final String DEV_SESSION_FILE =  HOME + "/bitpieces_dev_session.cache";
	public static final String STAGE_SESSION_FILE =  HOME + "/bitpieces_stage_session.cache";
	
	public static final Integer DEV_WEB_PORT = 8080;
	public static final Integer STAGE_WEB_PORT = 8081;
//	public static final Integer PROD_WEB_PORT = 4566;
	
	public static final String IP_ADDRESS = "107.170.137.106";

	
	public static final List<String> ALLOW_ACCESS_ADDRESSES = Arrays.asList(
			"http://localhost", "http://107.170.137.106", "https://coinbase.com", 
			"http://173.163.212.165:4568", "http://173.163.212.165:8080", "http://173.163.212.165",
			"http://test.bitpieces.com","http://bitpieces.com",
			"http://www.test.bitpieces.com","http://www.bitpieces.com",
			"https://bitpieces.com",
			"107.170.137.106", "http://107.170.137.106:4566");
	
	public static final String COINBASE_PROP = HOME + "/coinbase.properties";
}
