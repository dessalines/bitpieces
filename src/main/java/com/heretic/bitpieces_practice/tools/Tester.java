package com.heretic.bitpieces_practice.tools;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.javalite.activejdbc.Base;

import com.heretic.bitpieces_practice.tables.Tables.Bid;
import com.heretic.bitpieces_practice.tables.Tables.Creators_page_fields;
import com.heretic.bitpieces_practice.web_service.HTMLTools;

public class Tester {
	public static void main(String[] args) {
		
		String symbol = "\u00A5";
//		String symbol = "";
		Integer precision = 4;
		String dfPattern = symbol + "###,###.";
		for (int i = 0; i < precision; i++) {
			dfPattern +="#";
		}
		
		System.out.println(dfPattern);
		
		DecimalFormat df = new DecimalFormat(dfPattern);
		String sampleJson = "[{\"creators_id\":1,\"reward_pct\":1.4,\"category_names\":\"Design,Visual Arts\","
				+ "\"number_of_backers\":4,\"worth_current\":117.15643434998,\"creators_name\":\"Leonardo_"
				+ "Davinci\"},{\"creators_id\":2,\"reward_pct\":5.0,\"category_names\":\"Music\",\"number_of_backers"
				+ "\":1,\"worth_current\":124445.0,\"creators_name\":\"Dusty_Springfield\"}]";
		
		System.out.println("sample Json = " + sampleJson);
		
		List<String> monetaryColNames = Arrays.asList("worth_current", "reward_pct");
		String names = "";
		
		Iterator<String> it = monetaryColNames.iterator();
		while (it.hasNext()) {
			String cName = it.next();
			names += "\"" + cName + "\"";
			if (it.hasNext()) {
				names += "|";
			}
		}
		
		String regex = "(" + names + "):[-+]?[0-9]*\\.?[0-9]+";
		
		System.out.println(regex);
		

		Pattern pattern = Pattern.compile(regex);

		Matcher matcher = pattern.matcher(sampleJson);
		
		while (matcher.find()) {
		      System.out.print("Start index: " + matcher.start());
		      System.out.print(" End index: " + matcher.end() + "|");
		      System.out.println(matcher.group());
		      
		      String regex2 = "[-+]?[0-9]*\\.?[0-9]+";
		      
				Pattern pattern2 = Pattern.compile(regex2);

				Matcher matcher2= pattern2.matcher(matcher.group());
				
				while (matcher2.find()) {
				      System.out.print("Start index: " + matcher2.start());
				      System.out.print(" End index: " + matcher2.end() + "|");
				      System.out.println(matcher2.group());
				      
				      // FORMAT THE NUMBER
				      Double numberBefore = Double.valueOf(matcher2.group());
				      String formatted = "\"" + df.format(numberBefore) + "\"";
				      
				      System.out.println(numberBefore + " after : " + formatted);
				      sampleJson = sampleJson.replaceAll(matcher2.group(), formatted);
				}
		      
		      
		    }
		
		System.out.println(sampleJson);
		
	}
	public static void main3(String[] args) {
		Properties prop = Tools.loadProperties("/home/tyler/db.properties");
		dbInit(prop);
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
