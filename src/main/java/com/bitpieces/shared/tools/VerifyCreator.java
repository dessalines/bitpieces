package com.bitpieces.shared.tools;

import java.util.Properties;

import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.DBException;

import com.bitpieces.shared.DataSources;

// a sample line is java -cp target/bitpieces_practice-0.0.1-SNAPSHOT-jar-with-dependencies.jar com.bitpieces.shared.tools.VerifyCreator dev testcreator

public class VerifyCreator {
	public static void main(String[] args) {

		String devOrStage = args[0];
		String creatorName = args[1];
		
		Properties prop = null;
		if (devOrStage.equals("dev")) {
			prop = Tools.loadProperties(DataSources.DEV_DB_PROP);
		} else if (devOrStage.equals("stage")) {
			prop = Tools.loadProperties(DataSources.STAGE_DB_PROP);
		}
		
		dbInit(prop);
		DBActions.verifyCreator(creatorName);
		dbClose();
	}
	
	private static final void dbInit(Properties prop) {
		try {
		Base.open("com.mysql.jdbc.Driver", 
				prop.getProperty("dburl"), 
				prop.getProperty("dbuser"), 
				prop.getProperty("dbpassword"));
		} catch (DBException e) {
			dbClose();
			dbInit(prop);
		}
	}
	private static final void dbClose() {
		Base.close();
	}
		
	
}
