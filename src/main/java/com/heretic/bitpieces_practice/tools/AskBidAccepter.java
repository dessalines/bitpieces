package com.heretic.bitpieces_practice.tools;

import java.util.Properties;

import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.DBException;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.heretic.bitpieces_practice.actions.Actions;

public class AskBidAccepter implements Job {
	public static void main(String[] args) {
		Properties prop = Tools.loadProperties("/home/tyler/db.properties");
		dbInit(prop);
		Actions.askBidAccepter();
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
