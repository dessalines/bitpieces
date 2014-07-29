package com.bitpieces.dev;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.Model;

import com.bitpieces.shared.Tables.Bid;
import com.bitpieces.shared.Tables.Creators_page_fields;
import com.bitpieces.shared.Tables.Pieces_owned_value_accum;
import com.bitpieces.shared.tools.HTMLTools;
import com.bitpieces.shared.tools.Tools;
import com.bitpieces.shared.tools.WebTools;

public class Tester {
	public static void main(String[] args) {
		Properties prop = Tools.loadProperties("/home/tyler/db.properties");
		dbInit(prop);
		List<Model> list = Pieces_owned_value_accum.findAll().limit(2);
		System.out.println(list);
//		WebTools.getPiecesOwnedValueAccumSeriesJson("3", null);

		String sampleJson = "[{\"creators_id\":1,\"reward_pct\":1.4,\"category_names\":\"Design,Visual Arts\","
				+ "\"number_of_backers\":4,\"worth_current\":117.15643434998,\"creators_name\":\"Leonardo_"
				+ "Davinci\"},{\"creators_id\":2,\"reward_pct\":5.0,\"category_names\":\"Music\",\"number_of_backers"
				+ "\":1,\"worth_current\":124445.52623523,\"creators_name\":\"Dusty_Springfield\"}]";
		
		String sampleJson2 = "[{\"owners_id\":3,\"price_time_\":\"2014-07-22T15:05:01\",\"creators_id\":1,\"pieces_accum\":5,"
				+ "\"price_per_piece\":1.2,\"value_accum\":6.0,\"creators_username\":\"Leonardo_Davinci\",\"price_end_time_\":\""
				+ "2014-07-22T15:05:08\",\"end_time_\":\"2014-07-22T15:05:11\",\"start_time_\":\"2014-07-22T15:05:01\",\""
				+ "timediff_seconds\":7},{\"owners_id\":3,\"price_time_\":\"2014-07-22T15:05:08\",\"creators_id\":1,\"pieces_accum\""
				+ ":5,\"price_per_piece\":1.5,\"value_accum\":7.5,\"creators_username\":\"Leonardo_Davinci\",\"price_end_time_\":\""
				+ "2014-07-22T15:05:11\",\"end_time_\":\"2014-07-22T15:05:11\",\"start_time_\":\"2014-07-22T15:05:01\",\"timediff_seconds\":3}]";
		
		List<Map<String, String>> listOfMaps = Tools.ListOfMapsPOJO(sampleJson2);
//		WebTools.createTableJSON(list);
		
		
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
