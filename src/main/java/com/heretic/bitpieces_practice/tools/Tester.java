package com.heretic.bitpieces_practice.tools;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.javalite.activejdbc.Base;

import com.heretic.bitpieces_practice.tables.Tables.Bid;
import com.heretic.bitpieces_practice.tables.Tables.Creators_page_fields;
import com.heretic.bitpieces_practice.tables.Tables.User;
import com.heretic.bitpieces_practice.web_service.HTMLTools;

public class Tester {
	public static void main(String[] args) {
		Properties prop = Tools.loadProperties("/home/tyler/db.properties");
		dbInit(prop);
		List<User> list = User.findAll();
//		System.out.println(c.get(0).getMetaModel().getAttributeNamesSkipId());
		
		String sampleJson = "[{\"creators_id\":1,\"reward_pct\":1.4,\"category_names\":\"Design,Visual Arts\","
				+ "\"number_of_backers\":4,\"worth_current\":117.15643434998,\"creators_name\":\"Leonardo_"
				+ "Davinci\"},{\"creators_id\":2,\"reward_pct\":5.0,\"category_names\":\"Music\",\"number_of_backers"
				+ "\":1,\"worth_current\":124445.52623523,\"creators_name\":\"Dusty_Springfield\"}]";
		List<Map<String, String>> listOfMaps = Tools.ListOfMapsPOJO(sampleJson);
		System.out.println(listOfMaps);
	}
	public static void main3(String[] args) {

//		WebTools.getPiecesOwnedValueAccumSeriesJson("3", null);
//		WebTools.getPiecesOwnedValueCurrentSeriesJson("3", null);
//		WebTools.getUsersFundsAccumSeriesJson("3", null);
//		WebTools.getUsersTransactionsJson("3", null);
//		WebTools.getUsersReputationJson("1", null);
//		System.out.println(WebTools.getPiecesOwnedValueCurrentByCreatorJson("Leonardo_Davinci", null));
//		System.out.println(WebTools.getPricePerPieceCurrentJson("Leonardo_Davinci", null));
//		System.out.println(WebTools.getRewardsOwedJson("Leonardo_Davinci", null));
//		System.out.println(WebTools.getBackersCurrentCountJson("Leonardo_Davinci", null));
//		System.out.println(WebTools.getPiecesOwnedValueCurrentSeriesJson("2", "Leonardo_Davinci", null));
		// The datetime .toJson shows "2014-07-18T20:46Z"
		// The timestamp .toJson shows 2014-07-18T20:46Z
//		Date_testing m = Date_testing.findById(1);
//		Model m = list.get(0);
//		String json = m.toJson(false);
//		System.out.println(json);
		
		Bid bid = (Bid)Bid.findById(1);
		System.out.println(bid.toJson(true));
//		WebTools.customToJson(list.get(0));
		
//		SeriesFetcher sf = new SeriesFetcher();
//		Map<DateTime, Double> dv = sf.getDateValueMapFromTable(list, "price_time_", "value_accum");
//		System.out.println(Tools.GSON2.toJson(dv));
		
		dbClose();
		
	}
	public static void main2(String[] args) {
	
		
		Properties prop = Tools.loadProperties("/home/tyler/db.properties");
		
		dbInit(prop);
		Creators_page_fields page = Creators_page_fields.findFirst("creators_id = ?",  1);
		
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
