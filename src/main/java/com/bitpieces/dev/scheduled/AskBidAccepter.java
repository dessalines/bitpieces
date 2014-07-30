package com.bitpieces.dev.scheduled;

import java.util.Properties;

import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.DBException;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.bitpieces.shared.DataSources;
import com.bitpieces.shared.tools.DBActions;
import com.bitpieces.shared.tools.Tools;

public class AskBidAccepter implements Job {
	public static void main(String[] args) {
		Properties prop = Tools.loadProperties(DataSources.DEV_DB_PROP);
		dbInit(prop);
		DBActions.askBidAccepter();
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

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		main(null);
	}
}
