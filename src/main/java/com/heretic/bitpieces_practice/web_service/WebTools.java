package com.heretic.bitpieces_practice.web_service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;
import org.javalite.activejdbc.LazyList;
import org.javalite.activejdbc.Model;

import com.heretic.bitpieces_practice.actions.Actions;
import com.heretic.bitpieces_practice.tables.Tables.Creator;
import com.heretic.bitpieces_practice.tables.Tables.Creators_page_fields;
import com.heretic.bitpieces_practice.tables.Tables.Pieces_owned_value_accum;
import com.heretic.bitpieces_practice.tables.Tables.Prices_for_user;
import com.heretic.bitpieces_practice.tools.Tools;

public class WebTools {


	
	public static String saveCreatorPage(String id, String reqBody) {
		
		Map<String, String> postMap = Tools.createMapFromAjaxPost(reqBody);
		
		Creators_page_fields page = Creators_page_fields.findFirst("creators_id = ?",  id);
		Creator creator = Creator.findById(id);
		String username = creator.getString("username");
		
		// The first time filling the page fields
		if (page == null) {
		page = Creators_page_fields.createIt("creators_id", id,
				"main_body", postMap.get("main_body"));
		} else {
			page.set("main_body", postMap.get("main_body")).saveIt();
		}
		
		// Save the html page
		HTMLTools.saveCreatorHTMLPage(username, page);
		
		return "Successful";
		
	}

	

	public static String placeBid(String userId, String body) {
		Map<String, String> postMap = Tools.createMapFromAjaxPost(body);
		
		Actions.createBid(userId, 
				postMap.get("creatorid"), 
				Integer.valueOf(postMap.get("pieces")), 
				Double.valueOf(postMap.get("bid")), 
				postMap.get("validUntil"), 
				true);
				
		
		return body;
	}
	
	public static String placeAsk(String userId, String body) {
		Map<String, String> postMap = Tools.createMapFromAjaxPost(body);
		
		Actions.createAsk(userId, 
				postMap.get("creatorid"), 
				Integer.valueOf(postMap.get("pieces")), 
				Double.valueOf(postMap.get("ask")), 
				postMap.get("validUntil"), 
				true);
				
		
		return body;
	}

	public static String createHighchartsJSONForMultipleCreators(List<Model> list, String dateColName,
			String valueColName, String creatorsIdentifier) {
		
List<Map<String, String>> listOfMaps = new ArrayList<Map<String, String>>();
		
		List<String[]> oneCreatorsData = new ArrayList<String[]>();
		
		for (int i = 0; i < list.size(); i++) {
			Model p = list.get(i);

		
			System.out.println(p);
			String UTCTime = p.getString(dateColName);
//			String highchartsDate = convertDateStrToHighchartsDateUTCString(UTCTime);
			String millis = convertDateStrToMillis(UTCTime);
			String val = p.getString(valueColName);
			
			String[] pair = {millis, val};
			oneCreatorsData.add(pair);
			
			String cCreatorsId = p.getString(creatorsIdentifier);
			
			// If its the last one, add it to the map
			if (i == list.size() -1) {
				String oneCreatorsDataStr = Tools.GSON.toJson(oneCreatorsData).replaceAll("\"", "");
				
				Map<String, String> map = new LinkedHashMap<String, String>();
				System.out.println(cCreatorsId);
				map.put("name", cCreatorsId);
				map.put("data", oneCreatorsDataStr);
				
				listOfMaps.add(map);
			} else {
				String nextCreatorsId = list.get(i+1).getString(creatorsIdentifier);
				
				if (!cCreatorsId.equals(nextCreatorsId)) {
					String oneCreatorsDataStr = Tools.GSON.toJson(oneCreatorsData).replaceAll("\"", "");
//					String oneCreatorsDataStr = Arrays.toString(oneCreatorsData.toArray());
					Map<String, String> map = new LinkedHashMap<String, String>();
					System.out.println(cCreatorsId);
					map.put("name", cCreatorsId);
					map.put("data", oneCreatorsDataStr);
					
					listOfMaps.add(map);
					oneCreatorsData.clear();
				}
				
			}
					
		}
		
		String listOfMapsStr = Tools.GSON2.toJson(listOfMaps).replaceAll("\"\\[", "[").replaceAll("\\]\"", "]");
		
		System.out.println(listOfMapsStr);
		
		
		
		return listOfMapsStr;
		
	}

	public static String getPiecesOwnedValueAccumSeriesJson(String userId, String body) {
//		Map<String, String> postMap = Tools.createMapFromAjaxPost(body);
		
		// First fetch from the table
		List<Model> list = Pieces_owned_value_accum.find("owners_id=?", userId);
		
		return createHighchartsJSONForMultipleCreators(list, "price_time_", "value_accum", "creators_id");
		
		
	}
	
	
	
	public static String getPricesForUserSeriesJson(String userId, String body) {
		
		// First fetch from the table
		List<Model> list = Prices_for_user.find("owners_id=?", userId);
		
		return createHighchartsJSONForMultipleCreators(list, "time_", "price_per_piece", "creators_id");
		
		
	}



	// Sample highcharts data
	// http://jsfiddle.net/gh/get/jquery/1.7.2/highslide-software/highcharts.com/tree/master/samples/highcharts/series/data-array-of-arrays-datetime/
	// [Date.UTC(2010, 5, 1), 71.5], 
    // [Date.UTC(2010, 10, 1), 106.4]
	public static String convertDateStrToHighchartsDateUTCString(String UTC) {
		String split[] = UTC.split("-|:|\\s");
		String str = "Date.UTC(" + split[0] + ", " + split[1] + ", " + split[2] + ", " + split[3] 
				+ ", " + split[4] + ", " + split[5] + ")";
			
		return str;
		
		
		
	}
	
	public static String convertDateStrToMillis(String UTC) {
		try {
			return String.valueOf(Tools.SDF.parse(UTC).getTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}


}
