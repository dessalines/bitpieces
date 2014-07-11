package com.heretic.bitpieces_practice.tools;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.Model;
import org.joda.time.DateTime;

import com.heretic.bitpieces_practice.tables.Tables.Creators_page_fields;
import com.heretic.bitpieces_practice.tables.Tables.Pieces_owned_value_accum;
import com.heretic.bitpieces_practice.web_service.HTMLTools;
import com.heretic.bitpieces_practice.web_service.WebTools;

public class Tester {
	public static void main(String[] args) {
		Properties prop = Tools.loadProperties("/home/tyler/db.properties");
		dbInit(prop);
		WebTools.getPiecesOwnedValueAccumSeriesJson("3", null);
		
		List<Model> list = Pieces_owned_value_accum.find("owners_id=?", "3");
		
//		SeriesFetcher sf = new SeriesFetcher();
//		Map<DateTime, Double> dv = sf.getDateValueMapFromTable(list, "price_time_", "value_accum");
//		System.out.println(Tools.GSON2.toJson(dv));
		
		dbClose();
		
	}
	public static void main2(String[] args) {
	
		
		Properties prop = Tools.loadProperties("/home/tyler/db.properties");
		
		dbInit(prop);
		Creators_page_fields page = Creators_page_fields.findFirst("creators_id = ?",  3);
		
		dbClose();
		HTMLTools.saveCreatorHTMLPage(page.getString("creators_id"), page);
		
		
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
