package com.heretic.bitpieces_practice.web_service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.lang3.StringEscapeUtils;
import org.javalite.activejdbc.LazyList;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.Paginator;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.heretic.bitpieces_practice.actions.Actions;
import com.heretic.bitpieces_practice.tables.Tables.Ask;
import com.heretic.bitpieces_practice.tables.Tables.Backers_current;
import com.heretic.bitpieces_practice.tables.Tables.Backers_current_count;
import com.heretic.bitpieces_practice.tables.Tables.Bid;
import com.heretic.bitpieces_practice.tables.Tables.Bids_asks;
import com.heretic.bitpieces_practice.tables.Tables.Bids_asks_current;
import com.heretic.bitpieces_practice.tables.Tables.Creator;
import com.heretic.bitpieces_practice.tables.Tables.Creators_activity;
import com.heretic.bitpieces_practice.tables.Tables.Creators_page_fields;
import com.heretic.bitpieces_practice.tables.Tables.Creators_page_fields_view;
import com.heretic.bitpieces_practice.tables.Tables.Creators_reputation;
import com.heretic.bitpieces_practice.tables.Tables.Creators_transactions;
import com.heretic.bitpieces_practice.tables.Tables.Pieces_available;
import com.heretic.bitpieces_practice.tables.Tables.Pieces_available_view;
import com.heretic.bitpieces_practice.tables.Tables.Pieces_issued_view;
import com.heretic.bitpieces_practice.tables.Tables.Pieces_owned_accum;
import com.heretic.bitpieces_practice.tables.Tables.Pieces_owned_value_accum;
import com.heretic.bitpieces_practice.tables.Tables.Pieces_owned_value_current;
import com.heretic.bitpieces_practice.tables.Tables.Pieces_owned_value_current_by_creator;
import com.heretic.bitpieces_practice.tables.Tables.Pieces_owned_value_current_by_owner;
import com.heretic.bitpieces_practice.tables.Tables.Prices;
import com.heretic.bitpieces_practice.tables.Tables.Prices_for_user;
import com.heretic.bitpieces_practice.tables.Tables.Rewards_earned;
import com.heretic.bitpieces_practice.tables.Tables.Rewards_earned_total_by_user;
import com.heretic.bitpieces_practice.tables.Tables.Rewards_owed;
import com.heretic.bitpieces_practice.tables.Tables.Rewards_owed_to_user;
import com.heretic.bitpieces_practice.tables.Tables.Rewards_view;
import com.heretic.bitpieces_practice.tables.Tables.User;
import com.heretic.bitpieces_practice.tables.Tables.Users_activity;
import com.heretic.bitpieces_practice.tables.Tables.Users_funds_accum;
import com.heretic.bitpieces_practice.tables.Tables.Users_funds_current;
import com.heretic.bitpieces_practice.tables.Tables.Users_reputation;
import com.heretic.bitpieces_practice.tables.Tables.Users_transactions;
import com.heretic.bitpieces_practice.tables.Tables.Worth;
import com.heretic.bitpieces_practice.tools.Tools;

public class WebTools {



	public static String saveCreatorPage(String id, String reqBody) {

		System.out.println(reqBody);
		//		Map<String, String> postMap = Tools.createMapFromAjaxPost(reqBody);

		Creators_page_fields page = Creators_page_fields.findFirst("creators_id = ?",  id);
		Creator creator = Creator.findById(id);
		String username = creator.getString("username");

		// The first time filling the page fields
		if (page == null) {
			page = Creators_page_fields.createIt("creators_id", id,
					"main_body", reqBody);
		} else {
			page.set("main_body", reqBody).saveIt();
		}

		// Save the html page
		HTMLTools.saveCreatorHTMLPage(username, page);

		return "Successful";

	}

	public static String getCreatorPageJson(String id, String reqBody) {

		System.out.println(reqBody);
		//		Map<String, String> postMap = Tools.createMapFromAjaxPost(reqBody);
		String json = null;
		try {
			Creators_page_fields page = Creators_page_fields.findFirst("creators_id = ?",  id);
			json = page.toJson(false, "main_body");
		} catch (NullPointerException e) {
			return "{\"main_body\": \"Nothing here yet\"}";

		}



		return json;

	}



	public static String placeBid(String userId, String body) {
		Map<String, String> postMap = Tools.createMapFromAjaxPost(body);

		// You don't have the creators id, so you have to fetch it:ss
		Creator creator = Creator.findFirst("username = ?", postMap.get("creatorName"));

		Actions.createBid(userId, 
				creator.getId().toString(), 
				Integer.valueOf(postMap.get("bidPieces")), 
				Double.valueOf(postMap.get("bid")), 
				postMap.get("validUntil"), 
				true);


		return body;
	}

	public static String placeAsk(String userId, String body) {
		Map<String, String> postMap = Tools.createMapFromAjaxPost(body);

		// You don't have the creators id, so you have to fetch it:
		Creator creator = Creator.findFirst("username = ?", postMap.get("creatorName"));

		Actions.createAsk(userId, 
				creator.getId().toString(), 
				Integer.valueOf(postMap.get("askPieces")), 
				Double.valueOf(postMap.get("ask")), 
				postMap.get("validUntil"), 
				true);


		return body;
	}

	public static String placeBuy(String userId, String body) {
		Map<String, String> postMap = Tools.createMapFromAjaxPost(body);

		// You don't have the creators id, so you have to fetch it:
		Creator creator = Creator.findFirst("username = ?", postMap.get("creatorName"));

		// Get the most recent price per piece from the creator:
		List<Model> p = Pieces_issued_view.find("creators_name=?",  creator.getString("username")).orderBy("time_ desc").limit(1);
		Double price_per_piece = p.get(0).getDouble("price_per_piece");


		Actions.sellFromCreator(creator.getId().toString(), 
				userId, 
				Integer.valueOf(postMap.get("buyPieces")), 
				price_per_piece);


		return body;
	}

	public static String deleteBidAsk(String userId, String body) {
		Map<String, String> postMap = Tools.createMapFromAjaxPost(body);

		String type = postMap.get("type");
		String time_ = postMap.get("time_");
		String creatorName = postMap.get("creatorName");

		// First find the creator
		Creator c = Creator.findFirst("username=?", creatorName);

		// format the time correctly
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
//		DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
		String dateCorrectFormat = null;
		try {
			Date date = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")).parse(time_.replaceAll("Z$", "+0000"));
			dateCorrectFormat = Tools.SDF.get().format(date);
			System.out.println(dateCorrectFormat);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		

		if (type.equals("bid")) {
			Bid bid = Bid.findFirst("users_id=? and creators_id=? and time_ = ?", userId, c.getId().toString(), dateCorrectFormat);
			System.out.println(bid);
			bid.delete();
			System.out.println("deleted it");
			return "Bid deleted";
			
		} else if (type.equals("ask")) {
			Ask ask = Ask.findFirst("users_id=? and creators_id=? and time_ = ?", 
					userId, c.getId().toString(), dateCorrectFormat);
			System.out.println(ask);
			ask.delete();
			System.out.println("deleted it");
			return "Ask deleted";
		}

		return body;
	}
	
	public static String makeDepositFake(String userId, String body) {
				Map<String, String> postMap = Tools.createMapFromAjaxPost(body);

				Double amount = Double.parseDouble(postMap.get("deposit"));
		// First fetch from the table
		Actions.makeDepositFake(userId,amount);
		
		return amount + " deposited.";
		
		



	}

	public static String getPiecesOwnedValueAccumSeriesJson(String userId, String body) {
		//		Map<String, String> postMap = Tools.createMapFromAjaxPost(body);

		// First fetch from the table
		List<Model> list = Pieces_owned_value_accum.find("owners_id=?", userId);

		return createHighChartsJSONForMultipleCreators(list, "price_time_", "value_accum", "creators_username");

	}

	public static String getPiecesOwnedAccumSeriesJson(String userId,
			String body) {

		// First fetch from the table
		List<Model> list = Pieces_owned_accum.find("owners_id=?", userId);

		return createHighChartsJSONForMultipleCreators(list, "start_time_", "pieces_accum", "creators_username");
	}



	public static String getPricesForUserSeriesJson(String userId, String body) {

		// First fetch from the table
		List<Model> list = Prices_for_user.find("owners_id=?", userId);

		return createHighChartsJSONForMultipleCreators(list, "time_", "price_per_piece", "creators_username");


	}

	public static String getPiecesOwnedValueCurrentSeriesJson(String userId, String body) {

		// First fetch from the table
		List<Model> list = Pieces_owned_value_current.find("owners_id=?", userId);


		return createHighChartsJSONForCurrent(list, "value_total", "creators_username");

	}

	public static String getPiecesOwnedValueCurrentSeriesJson(String userId, String creatorName, String body) {
		String val = null;
		try {
			// First fetch from the table
			Pieces_owned_value_current p = Pieces_owned_value_current.findFirst("owners_id=? and creators_username=?", userId, creatorName);

			val = p.getString("value_total");
		} catch(NullPointerException e) {
			return "0";
		}
		return val;

	}

	public static String getPiecesOwnedCurrentSeriesJson(String userId, String creatorName, String body) {
		String val = null;
		try {
			// First fetch from the table
			Pieces_owned_value_current p = Pieces_owned_value_current.findFirst("owners_id=? and creators_username=?", userId, creatorName);

			val = p.getString("pieces_total");
		} catch(NullPointerException e) {
			return "0";
		}
		return val;

	}

	public static String getRewardsEarnedSeriesJson(String userId, String body) {
		// First fetch from the table
		List<Model> list = Rewards_earned.find("owners_id=?", userId);

		return createHighChartsJSONForMultipleCreators(list, "price_time_", "reward_earned", "creators_username");
	}

	public static String getUsersFundsAccumSeriesJson(String userId, String body) {
		// First fetch from the table
		List<Model> list = Users_funds_accum.find("users_id=?", userId);

		return createHighChartsJSONForSingleCreator(list, "time_", "funds_accum", "Funds");
	}

	public static String getUsersDataJson(String userId, String body) {
		User user  = User.findById(userId);

		String json = user.toJson(false, "email", "username");
		System.out.println(json);

		return json;
	}

	public static String getUsersTransactionsJson(String userId, String body) {

		List<Model> list = Users_transactions.find("users_id=?",  userId);

		return createTableJSON(list);

	}

	public static String getUsersActivityJson(String userId, String body) {

		List<Model> list = Users_activity.find("users_id=?",  userId);

		return createTableJSON(list);

	}

	public static String getUsersBidsAsksCurrentJson(String userId, String body) {

		List<Model> list = Bids_asks_current.find("users_id=?",  userId).orderBy("time_ desc");

		return createTableJSON(list);

	}

	public static String getUsersFundsCurrentJson(String userId, String body) {
		String json = null;
		try {
			Users_funds_current usersFundsCurrent = Users_funds_current.findFirst("users_id=?",  userId);

			json = usersFundsCurrent.getString("current_funds");
		} catch(NullPointerException e) {
			return "0";
		}
		return json;

	}

	public static String getRewardsEarnedTotalByUserJson(String userId, String body) {
		String json = null;
		try {
			Rewards_earned_total_by_user rewardsEarned = Rewards_earned_total_by_user.findFirst("owners_id=?",  userId);

			json = rewardsEarned.getString("reward_earned_total");
		} catch(NullPointerException e) {
			return "0";
		}
		return json;

	}

	public static String getPiecesValueCurrentByOwnerJson(String userId, String body) {
		String json = null;
		try {
			Pieces_owned_value_current_by_owner value = Pieces_owned_value_current_by_owner.findFirst("owners_id=?",  userId);

			json = value.getString("value_total");
		} catch(NullPointerException e) {
			return "0";
		}
		return json;

	}

	public static String getUsersReputationJson(String userId, String body) {


		String json = null;

		try {
			Users_reputation value = Users_reputation.findFirst("users_id=?",  userId);

			json = value.getString("reputation");

			// If they have no reputation, then return a 0
		} catch (NullPointerException e) {
			return "0";
		}

		System.out.println(json);
		return json;

	}


	public static String creatorsSearchJson(String query) {
		List<Model> list = Creator.find("username like '%" + query + "%'");

		String json = createTableJSON(list, "id", "username");

		return json;


	}


	public static String getPiecesOwnedValueCurrentByCreatorJson(
			String creatorName, String body) {

		Pieces_owned_value_current_by_creator p = 
				Pieces_owned_value_current_by_creator.findFirst("username = ?", creatorName);

		String json = p.getString("value_total");
		return json;

	}

	public static String getPricePerPieceCurrentJson(
			String creatorName, String body) {


		LazyList<Prices> prices = Prices.where("creators_name = ?", creatorName).orderBy("time_ desc").limit(1);
		Prices p = prices.get(0);
		String json = p.getString("price_per_piece");
		return json;

	}

	public static String getRewardsOwedJson(
			String creatorName, String body) {

		Rewards_owed r = Rewards_owed.findFirst("creators_username = ?", creatorName);
		String json = r.getString("total_owed");

		return json;

	}

	public static String getBackersCurrentCountJson(
			String creatorName, String body) {

		Backers_current_count r = Backers_current_count.findFirst("creators_username = ?", creatorName);
		String json = r.getString("number_of_backers");

		return json;

	}

	public static String getMainBodyJson(String creatorName, String body) {
		String json = null;
		try {
			Creators_page_fields_view p = Creators_page_fields_view.findFirst("username = ?", creatorName);

			json = p.getString("main_body");
		} catch(NullPointerException e) {
			return "The creator hasn't made a page yet";
		}
		return json;

	}

	public static String getPricesJson(
			String creatorName, String body) {


		List<Model> list = Prices.find("creators_name=?", creatorName);
		return createHighChartsJSONForSingleCreator(list, "time_", "price_per_piece", "Pricing");

	}

	public static String getWorthJson(
			String creatorName, String body) {


		List<Model> list = Worth.find("creators_username=?", creatorName);
		return createHighChartsJSONForSingleCreator(list, "price_time_", "worth", "Worth");

	}

	public static String getBidsAsksCurrentJson(String creatorName, String body) {

		List<Model> list = Bids_asks_current.find("creators_name=?",  creatorName);

		return createTableJSON(list);

	}

	public static String getRewardsPctJson(String creatorName, String body) {

		List<Model> list = Rewards_view.find("creators_name=?",  creatorName);

		return createTableJSON(list);

	}

	public static String getRewardsOwedToUserJson(String creatorName, String body) {

		List<Model> list = Rewards_owed_to_user.find("creators_username=?",  creatorName);

		return createTableJSON(list, "creators_username", "owners_name", "total_owed");

	}

	public static String getPiecesIssuedJson(String creatorName, String body) {

		List<Model> list = Pieces_issued_view.find("creators_name=?",  creatorName);

		return createTableJSON(list);

	}

	public static String getPiecesIssuedMostRecentPriceJson(String creatorName, String body) {

		List<Model> p = Pieces_issued_view.find("creators_name=?",  creatorName).orderBy("time_ desc").limit(1);
		String val = p.get(0).getString("price_per_piece");
		return val;

	}

	public static String getBackersCurrentJson(String creatorName, String body) {

		List<Model> list = Backers_current.find("creators_username=?",  creatorName);

		return createTableJSON(list, "users_username", "pieces_total", "value_total");

	}

	public static String getCreatorsReputationJson(String creatorName, String body) {


		String json = null;

		try {
			Creators_reputation value = Creators_reputation.findFirst("creators_name=?",  creatorName);

			json = value.getString("reputation");

			// If they have no reputation, then return a 0
		} catch (NullPointerException e) {
			return "0";
		}

		System.out.println(json);
		return json;

	}

	public static String getPiecesAvailableJson(String creatorName, String body) {


		String json = null;

		try {
			Pieces_available_view value = Pieces_available_view.findFirst("creators_name=?",  creatorName);

			json = value.getString("pieces_available");

			// If they have no reputation, then return a 0
		} catch (NullPointerException e) {
			return "0";
		}

		System.out.println(json);
		return json;

	}
	
	public static String getPiecesOwnedTotalJson(String creatorName, String body) {


		String json = null;

		try {
			Pieces_available_view value = Pieces_available_view.findFirst("creators_name=?",  creatorName);

			json = value.getString("pieces_owned_total");

			// If they have no reputation, then return a 0
		} catch (NullPointerException e) {
			return "0";
		}

		System.out.println(json);
		return json;

	}

	public static String getCreatorsActivityJson(String creatorName, String body) {

		List<Model> list = Creators_activity.find("creators_name=?",  creatorName);

		Paginator p = new Paginator(Creators_activity.class, 5, "creators_name=?", creatorName);

		List<Model> items = p.getPage(1);


		return createTableJSON(items);

	}

	public static String getCreatorsTransactionsJson(String creatorName, String body) {


		List<Model> list = Creators_transactions.find("creators_name=?",  creatorName);


		return createTableJSON(list);

	}

	public static String createTableJSON(List<Model> list, String... params) {

		String json = "[";
		for (int i = 0; i < list.size(); i++) {
			if (params != null) {
				json += list.get(i).toJson(false, params); 
			} else {
				json += list.get(i).toJson(false);
			}
			if (i < list.size()-1) {
				json += ",";
			}
		}
		json += "]";

		System.out.println(json);

		return json;
	}

	public static String createTableJSON(List<Model> list) {
		return createTableJSON(list, null);
	}




	public static String createHighChartsJSONForMultipleCreators(List<Model> list, String dateColName,
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

		String listOfMapsStr = Tools.GSON.toJson(listOfMaps).replaceAll("\"\\[", "[").replaceAll("\\]\"", "]");

		System.out.println(listOfMapsStr);



		return listOfMapsStr;

	}



	public static String createHighChartsJSONForSingleCreator(List<Model> list, String dateColName,
			String valueColName, String seriesName) {

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


			// If its the last one, add it to the map
			if (i == list.size() -1) {
				String oneCreatorsDataStr = Tools.GSON.toJson(oneCreatorsData).replaceAll("\"", "");

				Map<String, String> map = new LinkedHashMap<String, String>();
				map.put("name", seriesName);
				map.put("data", oneCreatorsDataStr);

				listOfMaps.add(map);
			} 

		}

		String listOfMapsStr = Tools.GSON.toJson(listOfMaps).replaceAll("\"\\[", "[").replaceAll("\\]\"", "]");

		System.out.println(listOfMapsStr);



		return listOfMapsStr;

	}



	public static String createHighChartsJSONForCurrent(List<Model> list, 
			String valueColName, String creatorsIdentifier) {

		List<String[]> data = new ArrayList<String[]>();

		for (int i = 0; i < list.size(); i++) {
			Model p = list.get(i);
			System.out.println(p);

			String val = p.getString(valueColName);
			String cCreatorsId = p.getString(creatorsIdentifier);
			String[] pair = {cCreatorsId, val};
			data.add(pair);
		}


		String arrayStr = Tools.GSON.toJson(data).replaceAll(",\"", ",").replaceAll("\"]","]");

		System.out.println(arrayStr);

		return arrayStr;

	}



	// Sample highcharts data
	// http://jsfiddle.net/gh/get/jquery/1.7.2/highslide-software/highcharts.com/tree/master/samples/highcharts/series/data-array-of-arrays-datetime/
	// [Date.UTC(2010, 5, 1), 71.5], 
	// [Date.UTC(2010, 10, 1), 106.4]
	@Deprecated
	public static String convertDateStrToHighchartsDateUTCString(String UTC) {
		String split[] = UTC.split("-|:|\\s");
		String str = "Date.UTC(" + split[0] + ", " + split[1] + ", " + split[2] + ", " + split[3] 
				+ ", " + split[4] + ", " + split[5] + ")";

		return str;



	}

	public static String convertDateStrToMillis(String UTC) {
		try {
			return String.valueOf(Tools.SDF.get().parse(UTC).getTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}













}
