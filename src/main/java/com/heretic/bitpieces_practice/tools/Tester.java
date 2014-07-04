package com.heretic.bitpieces_practice.tools;

import java.util.Properties;

import org.javalite.activejdbc.Base;

import com.heretic.bitpieces_practice.actions.Actions;
import com.heretic.bitpieces_practice.tables.Tables.Creators_page_fields;

public class Tester {
	public static void main(String[] args) {
	
		
		Properties prop = Tools.loadProperties("/home/tyler/db.properties");
		
		dbInit(prop);
		Creators_page_fields page = Creators_page_fields.findFirst("creators_id = ?",  1);
		
		dbClose();
		Actions.saveCreatorHTMLPage(page.getString("creators_id"), page);
		
		
	}
	
	private static final void dbInit(Properties prop) {
		Base.open("com.mysql.jdbc.Driver", 
				prop.getProperty("dburl"), 
				prop.getProperty("dbuser"), 
				prop.getProperty("dbpassword"));
	}
	private static final void dbClose() {
		Base.close();
	}

}
