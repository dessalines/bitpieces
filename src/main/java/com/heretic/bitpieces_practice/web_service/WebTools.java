package com.heretic.bitpieces_practice.web_service;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.javalite.activejdbc.LazyList;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.Paginator;

import com.heretic.bitpieces_practice.actions.Actions;
import com.heretic.bitpieces_practice.tables.Tables.Ask;
import com.heretic.bitpieces_practice.tables.Tables.Backers_current;
import com.heretic.bitpieces_practice.tables.Tables.Backers_current_count;
import com.heretic.bitpieces_practice.tables.Tables.Bid;
import com.heretic.bitpieces_practice.tables.Tables.Bids_asks_current;
import com.heretic.bitpieces_practice.tables.Tables.Categories;
import com.heretic.bitpieces_practice.tables.Tables.Creator;
import com.heretic.bitpieces_practice.tables.Tables.Creators_activity;
import com.heretic.bitpieces_practice.tables.Tables.Creators_funds_accum;
import com.heretic.bitpieces_practice.tables.Tables.Creators_funds_current;
import com.heretic.bitpieces_practice.tables.Tables.Creators_page_fields;
import com.heretic.bitpieces_practice.tables.Tables.Creators_page_fields_view;
import com.heretic.bitpieces_practice.tables.Tables.Creators_reputation;
import com.heretic.bitpieces_practice.tables.Tables.Creators_search_view;
import com.heretic.bitpieces_practice.tables.Tables.Creators_settings;
import com.heretic.bitpieces_practice.tables.Tables.Creators_transactions;
import com.heretic.bitpieces_practice.tables.Tables.Currencies;
import com.heretic.bitpieces_practice.tables.Tables.Pieces_available_view;
import com.heretic.bitpieces_practice.tables.Tables.Pieces_issued;
import com.heretic.bitpieces_practice.tables.Tables.Pieces_issued_view;
import com.heretic.bitpieces_practice.tables.Tables.Pieces_owned_accum;
import com.heretic.bitpieces_practice.tables.Tables.Pieces_owned_value_accum;
import com.heretic.bitpieces_practice.tables.Tables.Pieces_owned_value_current;
import com.heretic.bitpieces_practice.tables.Tables.Pieces_owned_value_current_by_creator;
import com.heretic.bitpieces_practice.tables.Tables.Pieces_owned_value_current_by_owner;
import com.heretic.bitpieces_practice.tables.Tables.Prices;
import com.heretic.bitpieces_practice.tables.Tables.Prices_for_user;
import com.heretic.bitpieces_practice.tables.Tables.Rewards_earned_accum;
import com.heretic.bitpieces_practice.tables.Tables.Rewards_earned_total;
import com.heretic.bitpieces_practice.tables.Tables.Rewards_earned_total_by_user;
import com.heretic.bitpieces_practice.tables.Tables.Rewards_owed;
import com.heretic.bitpieces_practice.tables.Tables.Rewards_owed_to_user;
import com.heretic.bitpieces_practice.tables.Tables.Rewards_view;
import com.heretic.bitpieces_practice.tables.Tables.User;
import com.heretic.bitpieces_practice.tables.Tables.Users_activity;
import com.heretic.bitpieces_practice.tables.Tables.Users_funds_accum;
import com.heretic.bitpieces_practice.tables.Tables.Users_funds_current;
import com.heretic.bitpieces_practice.tables.Tables.Users_reputation;
import com.heretic.bitpieces_practice.tables.Tables.Users_settings;
import com.heretic.bitpieces_practice.tables.Tables.Users_transactions;
import com.heretic.bitpieces_practice.tables.Tables.Worth;
import com.heretic.bitpieces_practice.tools.Tools;
import com.heretic.bitpieces_practice.tools.Tools.UserType;
import com.heretic.bitpieces_practice.tools.UnitConverter;
import com.heretic.bitpieces_practice.tools.UID;

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



	public static String placeBid(UID uid, String body, UnitConverter sf) {
		Map<String, String> postMap = Tools.createMapFromAjaxPost(body);

		UsersSettings settings = new UsersSettings(uid);

		// You don't have the creators id, so you have to fetch it:ss
		Creator creator = Creator.findFirst("username = ?", postMap.get("creatorName"));

		Integer bidPieces = Integer.valueOf(postMap.get("bidPieces"));
		Double bid = Double.valueOf(postMap.get("bid"));
		Double btcBid = bid;
		String message = null;

		// Convert amount if necessary
		if (!settings.getIso().equals("BTC")) {
			Double spotRate = sf.getSpotRate(settings.getIso());
			btcBid = bid / spotRate;
			System.out.println(bid + " / " + spotRate + " = " + btcBid);
			message = "Bid for " + bidPieces + " pieces placed at " + btcBid + " BTC" + "(or "  + 
					bid + " " + settings.getIso() + " @ " + spotRate + settings.getIso() + "/BTC";
		} else {
			message = "Bid for " + bidPieces + " pieces placed at " + btcBid + " BTC";
		}


		Actions.createBid(uid.getId(), 
				creator.getId().toString(), 
				bidPieces, 
				btcBid, 
				postMap.get("validUntil"), 
				true);


		return message;
	}

	public static String placeAsk(UID uid, String body, UnitConverter sf) {
		Map<String, String> postMap = Tools.createMapFromAjaxPost(body);

		UsersSettings settings = new UsersSettings(uid);

		// You don't have the creators id, so you have to fetch it:ss
		Creator creator = Creator.findFirst("username = ?", postMap.get("creatorName"));

		Integer askPieces = Integer.valueOf(postMap.get("askPieces"));
		Double ask = Double.valueOf(postMap.get("ask"));
		Double btcAsk = ask;
		String message = null;

		// Convert amount if necessary
		if (!settings.getIso().equals("BTC")) {
			Double spotRate = sf.getSpotRate(settings.getIso());
			btcAsk = ask / spotRate;
			System.out.println(ask + " / " + spotRate + " = " + btcAsk);
			message = "Ask for " + askPieces + " pieces placed at " + btcAsk + " BTC" + "(or "  + 
					ask + " " + settings.getIso() + " @ " + spotRate + settings.getIso() + "/BTC";
		} else {
			message = "Ask for " + askPieces + " pieces placed at " + btcAsk + " BTC";
		}

		Actions.createAsk(uid.getId(), 
				creator.getId().toString(), 
				askPieces,
				btcAsk,
				postMap.get("validUntil"), 
				true);


		return message;
	}

	// Don't need to do currency conversion for this one, since it just pulls the most recent price per piece
	// in BTC already
	public static String placeBuy(UID uid, String body) {
		Map<String, String> postMap = Tools.createMapFromAjaxPost(body);

		// You don't have the creators id, so you have to fetch it:
		Creator creator = Creator.findFirst("username = ?", postMap.get("creatorName"));

		// Get the most recent price per piece from the creator:
		List<Model> p = Pieces_issued_view.find("creators_name=?",  creator.getString("username")).orderBy("time_ desc").limit(1);
		Double price_per_piece = p.get(0).getDouble("price_per_piece");


		Actions.sellFromCreator(creator.getId().toString(), 
				uid.getId(), 
				Integer.valueOf(postMap.get("buyPieces")), 
				price_per_piece);


		return body;
	}

	public static String issuePieces(UID uid, String body, UnitConverter sf) {
		Map<String, String> postMap = Tools.createMapFromAjaxPost(body);

		UsersSettings settings = new UsersSettings(uid);

		Integer pieces = Integer.valueOf(postMap.get("issuePieces"));
		Double price = Double.valueOf(postMap.get("issuePrice"));
		Double btcIssuePrice = price;
		String message = null;

		// Convert amount if necessary
		if (!settings.getIso().equals("BTC")) {
			Double spotRate = sf.getSpotRate(settings.getIso());
			btcIssuePrice = price / spotRate;
			System.out.println(price + " / " + spotRate + " = " + btcIssuePrice);
			message = "Issued " + pieces + " pieces at " + btcIssuePrice + " BTC" + "(or "  + 
					price + " " + settings.getIso() + " @ " + spotRate + settings.getIso() + "/BTC";
		} else {
			message = "Issued " + pieces + " pieces at " + btcIssuePrice + " BTC";
		}


		Pieces_issued.createIt(
				"creators_id",  uid.getId(), 
				"time_", Tools.SDF.get().format(new Date()), 
				"pieces_issued", pieces,
				"price_per_piece", btcIssuePrice);


		return message;
	}
	
	public static String newReward(UID uid, String body) {
		Map<String, String> postMap = Tools.createMapFromAjaxPost(body);
		
		Double rewardPct = Double.valueOf(postMap.get("reward_pct"));
		
		Actions.issueReward(uid.getId(), rewardPct);
		
		return rewardPct + "% reward in effect";
		
		
	}

	public static String deleteBidAsk(UID uid, String body) {
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
			Date date = Tools.SDF2.get().parse(time_);
			dateCorrectFormat = Tools.SDF.get().format(date);
			System.out.println(dateCorrectFormat);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



		if (type.equals("bid")) {
			Bid bid = Bid.findFirst("users_id=? and creators_id=? and time_ = ?", uid.getId(), c.getId().toString(), dateCorrectFormat);
			System.out.println(bid);
			bid.delete();
			System.out.println("deleted it");
			return "Bid deleted";

		} else if (type.equals("ask")) {
			Ask ask = Ask.findFirst("users_id=? and creators_id=? and time_ = ?", 
					uid.getId(), c.getId().toString(), dateCorrectFormat);
			System.out.println(ask);
			ask.delete();
			System.out.println("deleted it");
			return "Ask deleted";
		}

		return body;
	}

	public static String makeDepositFake(UID uid, String body, UnitConverter sf) {
		Map<String, String> postMap = Tools.createMapFromAjaxPost(body);
		UsersSettings settings = new UsersSettings(uid);

		Double amount = Double.parseDouble(postMap.get("deposit"));
		Double btcAmount = amount;
		String message = null;

		// Convert amount if necessary
		if (!settings.getIso().equals("BTC")) {
			Double spotRate = sf.getSpotRate(settings.getIso());
			btcAmount = amount / spotRate;
			System.out.println(amount + " / " + spotRate + " = " + btcAmount);
			message = btcAmount + " BTC deposited ( or " + amount + " " + settings.getIso() + 
					" @ " + spotRate + settings.getIso() + "/BTC";
		} else {
			message = btcAmount + " BTC deposited";
		}


		// First fetch from the table
		Actions.makeDepositFake(uid.getId(),btcAmount);

		return message;





	}

	public static String getPiecesOwnedValueAccumSeriesJson(UID uid, UnitConverter sf) {

		UsersSettings settings = new UsersSettings(uid);
		// First fetch from the table
		List<Model> list = Pieces_owned_value_accum.find("owners_id=?", uid.getId());

		if (list.size() > 0 ) { 
			return createHighChartsJSONForMultipleCreatorsV2(list, "price_time_", "value_accum", "creators_username",
					sf, settings.getPrecision(), settings.getIso());
		} else {
			return "0";
		}

	}

	public static String getPiecesOwnedAccumSeriesJson(UID uid,
			UnitConverter sf) {
		UsersSettings settings = new UsersSettings(uid);
		// First fetch from the table
		List<Model> list = Pieces_owned_accum.find("owners_id=?", uid.getId());
		if (list.size() > 0 ) { 
			return createHighChartsJSONForMultipleCreatorsV2(list, "start_time_", "pieces_accum", "creators_username",
					sf, settings.getPrecision(), settings.getIso());
		} else {
			return "0";
		}
	}



	public static String getPricesForUserSeriesJson(UID uid, UnitConverter sf) {
		UsersSettings settings = new UsersSettings(uid);
		// First fetch from the table
		List<Model> list = Prices_for_user.find("owners_id=?", uid.getId());

		if (list.size() > 0 ) { 
			return createHighChartsJSONForMultipleCreatorsV2(list, "time_", "price_per_piece", "creators_username",
					sf, settings.getPrecision(), settings.getIso());
		} else {
			return "0";
		}


	}

	public static String getPiecesOwnedValueCurrentSeriesJson(UID uid, UnitConverter sf) {

		// First fetch from the table
		List<Model> list = Pieces_owned_value_current.find("owners_id=?", uid.getId());

		UsersSettings settings = new UsersSettings(uid);

		if (list.size()>0) { 
			return createHighChartsJSONForCurrentV2(list, "value_total_current", "creators_username", sf,
					settings.getPrecision(), settings.getIso());
		} else {
			return "0";
		}

	}

	public static String getRewardsEarnedTotalJson(UID uid, UnitConverter sf) {
		UsersSettings settings = new UsersSettings(uid);
		// First fetch from the table
		List<Model> list = Rewards_earned_total.find("owners_id=?", uid.getId());

		if (list.size() > 0 ) { 
			return createHighChartsJSONForCurrentV2(list, "reward_earned_total", "creators_username",
					sf, settings.getPrecision(), settings.getIso());
		} else {
			return "0";
		}

	}

	public static String getPiecesOwnedValueCurrentCreatorSeriesJson(String creatorName, UID uid, UnitConverter sf) {

		UsersSettings settings = new UsersSettings(null);
		if (uid != null) {
			settings = new UsersSettings(uid);
		} 

		// First fetch from the table
		List<Model> list = Pieces_owned_value_current.find("creators_username=?", creatorName);

		return createHighChartsJSONForCurrentV2(list, "value_total_current", "owners_name",
				sf, settings.getPrecision(), settings.getIso());

	}

	public static String getPiecesOwnedValueCurrentSeriesJson(UID uid, String creatorName, UnitConverter sf) {
		UsersSettings settings = new UsersSettings(uid);
		String json = null;
		try {
			// First fetch from the table
			Pieces_owned_value_current p = Pieces_owned_value_current.findFirst("owners_id=? and creators_username=?",
					uid.getId(), creatorName);

			String val = p.getString("value_total_current");
			json = sf.convertSingleValueCurrentJson(val, settings.getIso(), settings.getPrecision());
		} catch(NullPointerException e) {
			return "0";
		}
		return json;

	}

	public static String getPiecesOwnedCurrentSeriesJson(UID uid, String creatorName, UnitConverter sf) {

		try {
			// First fetch from the table
			Pieces_owned_value_current p = Pieces_owned_value_current.findFirst("owners_id=? and creators_username=?", 
					uid.getId(), creatorName);
			String val = p.getString("pieces_total");
			return val;

		} catch(NullPointerException e) {
			return "0";
		}


	}

	public static String getRewardsEarnedAccumSeriesJson(UID uid, UnitConverter sf) {
		UsersSettings settings = new UsersSettings(uid);
		// First fetch from the table
		List<Model> list = Rewards_earned_accum.find("owners_id=?", uid.getId());

		if (list.size() > 0 ) { 
			return createHighChartsJSONForMultipleCreatorsV2(list, "price_time_", "reward_accum", "creators_username",
					sf, settings.getPrecision(), settings.getIso());
		} else {
			return "0";
		}
	}

	public static String getUsersFundsAccumSeriesJson(UID uid, UnitConverter sf) {

		UsersSettings settings = new UsersSettings(uid);

		// First fetch from the table
		List<Model> list = Users_funds_accum.find("users_id=?", uid.getId());

		return createHighChartsJSONForSingleCreatorV2(list, "time_", "funds_accum", "Funds",
				sf, settings.getPrecision(), settings.getIso());


	}


	public static String getUsersTransactionsJson(UID uid, String body, UnitConverter sf) {

		UsersSettings settings = new UsersSettings(uid);
		List<Model> list = Users_transactions.find("users_id=?",  uid.getId());

		String json = convertLOMtoJson(doUnitConversions(list, sf, settings.getPrecision(), settings.getIso(), false));

		return json;

	}

	public static class UsersSettings {
		private final String iso;
		private final Integer precision;
		public UsersSettings(UID uid) {

			if (uid != null) {

				if (uid.getType() == UserType.User) {
					Users_settings settings = Users_settings.findById(uid.getId());
					this.precision = settings.getInteger("precision_");
					this.iso = settings.getString("curr_iso"); 
				} else {
					Creators_settings settings = Creators_settings.findById(uid.getId());
					this.precision = settings.getInteger("precision_");
					this.iso = settings.getString("curr_iso"); 
				}
			} else {
				this.precision = null;
				this.iso = null;
			}
		}
		public String getIso() {
			return iso;
		}
		public Integer getPrecision() {
			return precision;
		}

	}

	public static String getUsersActivityJson(UID uid, UnitConverter sf) {
		UsersSettings settings = new UsersSettings(uid);
		List<Model> list = Users_activity.find("users_id=?",  uid.getId());

		String json = convertLOMtoJson(doUnitConversions(list, sf, settings.getPrecision(), settings.getIso(), false));

		return json;

	}

	public static String getUsersSettingsJson(UID uid) {


		if (uid.getType() == UserType.User) {
			Users_settings user =  Users_settings.findById(uid.getId());
			return user.toJson(false);
		} else {
			Creators_settings user =  Creators_settings.findById(uid.getId());
			return user.toJson(false);
		}




	}

	public static String saveSettings(UID uid, String body) {
		Map<String, String> postMap = Tools.createMapFromAjaxPost(body);

		Model user = null;
		if (uid.getType() == UserType.User) {
			user = User.findById(uid.getId());
		} else {
			user = Creator.findById(uid.getId());
		}

		String email = postMap.get("email");
		String currency = postMap.get("currency");
		String precision = postMap.get("precision");

		// Find the correct currency id
		String currId = Currencies.findFirst("iso=?", currency).getId().toString();

		// Make a map from sql column names to value changes
		Map<String, String> s= new HashMap<String, String>();
		s.put("email", email);
		s.put("local_currency_id", currId);
		s.put("precision_", precision);


		for (Entry<String, String> e : s.entrySet()) {
			user.set(e.getKey(), e.getValue()).saveIt();
		}


		return "Settings updated";

	}

	public static String getUsersBidsAsksCurrentJson(UID uid, UnitConverter sf) {
		UsersSettings settings = new UsersSettings(uid);

		List<Model> list = Bids_asks_current.find("users_id=?",  uid.getId()).orderBy("time_ desc");
		if (list.size()>0) {
			String json = convertLOMtoJson(doUnitConversions(list, sf, settings.getPrecision(), settings.getIso(), false));
			return json;
		} else {
			return "0";
		}

	}

	public static String getUsersFundsCurrentJson(UID uid, UnitConverter sf) {
		UsersSettings settings = new UsersSettings(uid);
		String json = null;
		try {
			Users_funds_current usersFundsCurrent = Users_funds_current.findFirst("users_id=?",  uid.getId());

			String val = usersFundsCurrent.getString("current_funds");
			json = sf.convertSingleValueCurrentJson(val, settings.getIso(), settings.getPrecision());
		} catch(NullPointerException e) {
			return "0";
		}
		return json;

	}

	public static String getCreatorsFundsCurrentJson(UID uid, UnitConverter sf) {
		UsersSettings settings = new UsersSettings(uid);
		String json = null;
		try {
			Creators_funds_current creatorsFundsCurrent = Creators_funds_current.findFirst("creators_id=?",  uid.getId());

			String val = creatorsFundsCurrent.getString("current_funds");
			json = sf.convertSingleValueCurrentJson(val, settings.getIso(), settings.getPrecision());
		} catch(NullPointerException e) {
			return "0";
		}
		return json;

	}

	public static String getRewardsEarnedTotalByUserJson(UID uid, UnitConverter sf) {
		UsersSettings settings = new UsersSettings(uid);
		String json = null;
		try {
			Rewards_earned_total_by_user rewardsEarned = Rewards_earned_total_by_user.findFirst("owners_id=?",  uid.getId());

			String val = rewardsEarned.getString("reward_earned_total");
			json = sf.convertSingleValueCurrentJson(val, settings.getIso(), settings.getPrecision());
		} catch(NullPointerException e) {
			return "0";
		}
		return json;

	}

	public static String getPiecesValueCurrentByOwnerJson(UID uid, UnitConverter sf) {
		UsersSettings settings = new UsersSettings(uid);
		String json = null;
		try {
			Pieces_owned_value_current_by_owner value = Pieces_owned_value_current_by_owner.findFirst("owners_id=?",  uid.getId());

			String val = value.getString("value_total_current");
			json = sf.convertSingleValueCurrentJson(val, settings.getIso(), settings.getPrecision());
		} catch(NullPointerException e) {
			return "0";
		}
		return json;

	}

	public static String getUsersReputationJson(UID uid, String body) {


		String json = null;

		try {
			Users_reputation value = Users_reputation.findFirst("users_id=?",  uid.getId());

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

		String json = createJSONListOfMapsFromModelList(list, "id", "username");
		return json;


	}

	public static String getCategoriesJson(String query) {
		List<Model> list = Categories.findAll();

		String json = createJSONListOfMapsFromModelList(list, "name");

		return json;


	}

	public static String getCurrenciesJson(String query) {
		List<Model> list = Currencies.findAll();

		String json = createJSONListOfMapsFromModelList(list);

		return json;


	}

	// TODO need to handle the case where the user IS logged in, because of worth_current
	public static String getDiscoverJson(String body, UID uid, UnitConverter sf) {

		UsersSettings settings = new UsersSettings(null);
		if (uid != null) {
			settings = new UsersSettings(uid);
		}


		List<Model> list = null;
		Long limit = 30L;
		if (body.equals("")) {
			list = Creators_search_view.findAll().limit(limit);
		} else {
			System.out.println(body);
			Map<String, String> postMap = Tools.createMapFromAjaxPost(body);
			String category = postMap.get("category");
			if (category == null || category.equals("All")) {
				list = Creators_search_view.findAll().limit(limit);
			} else {
				list = Creators_search_view.find("category_names like '%" + category + "%'").limit(limit);;
			}
		}
		String json = "0";
		if (list.size() > 0) {
			json = convertLOMtoJson(doUnitConversions(list, sf, settings.getPrecision(), settings.getIso(), false));
		} 




		return json;


	}


	public static String getPiecesOwnedValueCurrentByCreatorJson(
			String creatorName, UID uid, UnitConverter sf) {
		UsersSettings settings = new UsersSettings(null);
		if (uid != null) {
			settings = new UsersSettings(uid);
		}
		Pieces_owned_value_current_by_creator p = 
				Pieces_owned_value_current_by_creator.findFirst("username = ?", creatorName);

		String val = p.getString("value_total_current");

		String json = sf.convertSingleValueCurrentJson(val, settings.getIso(), settings.getPrecision());
		return json;

	}

	public static String getPricePerPieceCurrentJson(
			String creatorName, UID uid, UnitConverter sf) {
		UsersSettings settings = new UsersSettings(null);
		if (uid != null) {
			settings = new UsersSettings(uid);
		}


		LazyList<Prices> prices = Prices.where("creators_name = ?", creatorName).orderBy("time_ desc").limit(1);
		Prices p = prices.get(0);
		String val = p.getString("price_per_piece");

		String json = sf.convertSingleValueCurrentJson(val, settings.getIso(), settings.getPrecision());

		return json;

	}

	public static String getRewardsOwedJson(
			String creatorName, UID uid, UnitConverter sf) {
		UsersSettings settings = new UsersSettings(null);
		if (uid != null) {
			settings = new UsersSettings(uid);
		}

		Rewards_owed r = Rewards_owed.findFirst("creators_username = ?", creatorName);
		String val = r.getString("total_owed");

		String json = sf.convertSingleValueCurrentJson(val, settings.getIso(), settings.getPrecision());

		return json;

	}

	public static String getBackersCurrentCountJson(
			String creatorName) {

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
			String creatorName, UID uid, UnitConverter sf) {

		UsersSettings settings = new UsersSettings(null);
		if (uid != null) {
			settings = new UsersSettings(uid);
		}

		List<Model> list = Prices.find("creators_name=?", creatorName);
		return createHighChartsJSONForSingleCreatorV2(list, "time_", "price_per_piece", "Pricing",
				sf, settings.getPrecision(), settings.getIso());

	}

	public static String getWorthJson(
			String creatorName, UID uid, UnitConverter sf) {

		UsersSettings settings = new UsersSettings(null);
		if (uid != null) {
			settings = new UsersSettings(uid);
		}

		List<Model> list = Worth.find("creators_username=?", creatorName);
		return createHighChartsJSONForSingleCreatorV2(list, "price_time_", "worth", "Worth",
				sf, settings.getPrecision(), settings.getIso());

	}

	public static String getBidsAsksCurrentJson(String creatorName, UID uid, UnitConverter sf) {
		List<Model> list = Bids_asks_current.find("creators_name=?",  creatorName);

		UsersSettings settings = new UsersSettings(null);
		if (uid != null) {
			settings = new UsersSettings(uid);
		} 
		if (list.size() > 0) {
			return convertLOMtoJson(doUnitConversions(list, sf, settings.getPrecision(), settings.getIso(), false));
		} else {
			return "0";
		}

	}

	public static String getRewardsPctJson(String creatorName, UID uid, UnitConverter sf) {

		List<Model> list = Rewards_view.find("creators_name=?",  creatorName);

		UsersSettings settings = new UsersSettings(null);
		if (uid != null) {
			settings = new UsersSettings(uid);
		} 

		return convertLOMtoJson(doUnitConversions(list, sf, settings.getPrecision(), settings.getIso(), false));

	}
	
	public static String getRewardsPctCurrentJson(String creatorName, UID uid) {

		List<Model> list = Rewards_view.find("creators_name=?",  creatorName).orderBy("time_ desc").limit(1);
		
		UsersSettings settings = new UsersSettings(null);
		if (uid != null) {
			settings = new UsersSettings(uid);
		} 
		String reward = list.get(0).getString("reward_pct");
		return reward;

	}

	public static String getRewardsOwedToUserJson(String creatorName, UID uid, UnitConverter sf) {

		List<Model> list = Rewards_owed_to_user.find("creators_username=?",  creatorName);

		UsersSettings settings = new UsersSettings(null);
		if (uid != null) {
			settings = new UsersSettings(uid);
		} 

		return convertLOMtoJson(doUnitConversions(list, sf, settings.getPrecision(), settings.getIso(), false,
				"creators_username", "owners_name", "total_owed"));

	}

	public static String getPiecesIssuedJson(String creatorName, UID uid, UnitConverter sf) {

		List<Model> list = Pieces_issued_view.find("creators_name=?",  creatorName);
		UsersSettings settings = new UsersSettings(null);
		if (uid != null) {
			settings = new UsersSettings(uid);
		} 


		return convertLOMtoJson(doUnitConversions(list, sf, settings.getPrecision(), settings.getIso(), false));

	}

	public static String getPiecesIssuedMostRecentPriceJson(String creatorName, UID uid, UnitConverter sf) {
		UsersSettings settings = new UsersSettings(null);
		if (uid != null) {
			settings = new UsersSettings(uid);
		} 

		List<Model> p = Pieces_issued_view.find("creators_name=?",  creatorName).orderBy("time_ desc").limit(1);
		String val = p.get(0).getString("price_per_piece");

		return sf.convertSingleValueCurrentJson(val, settings.getIso(), settings.getPrecision());

	}

	public static String getBackersCurrentJson(String creatorName, UID uid, UnitConverter sf) {

		List<Model> list = Backers_current.find("creators_username=?",  creatorName);

		UsersSettings settings = new UsersSettings(null);
		if (uid != null) {
			settings = new UsersSettings(uid);
		} 

		return convertLOMtoJson(doUnitConversions(list, sf, settings.getPrecision(), settings.getIso(), false,
				"users_username", "pieces_total", "value_total_current"));

	}

	public static String getCreatorsReputationJson(String creatorName) {


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

	public static String getPiecesAvailableJson(String creatorName) {


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

	public static String getPiecesOwnedTotalJson(String creatorName) {


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

	public static String getCreatorsActivityJson(String creatorName, UID uid, UnitConverter sf) {

		List<Model> list = Creators_activity.find("creators_name=?",  creatorName);

		UsersSettings settings = new UsersSettings(null);
		if (uid != null) {
			settings = new UsersSettings(uid);
		} 
		Paginator p = new Paginator(Creators_activity.class, 5, "creators_name=?", creatorName);

		List<Model> items = p.getPage(1);

		return convertLOMtoJson(doUnitConversions(items, sf, settings.getPrecision(), settings.getIso(), false));


	}

	public static String getCreatorsTransactionsJson(String creatorName, UID uid, UnitConverter sf) {

		UsersSettings settings = new UsersSettings(null);
		if (uid != null) {
			settings = new UsersSettings(uid);
		} 
		List<Model> list = Creators_transactions.find("creators_name=?",  creatorName);

		return convertLOMtoJson(doUnitConversions(list, sf, settings.getPrecision(), settings.getIso(), false));

	}

	public static String getCreatorsFundsAccumJson(String creatorName, UID uid, UnitConverter sf) {
		UsersSettings settings = new UsersSettings(null);
		if (uid != null) {
			settings = new UsersSettings(uid);
		}

		List<Model> list = Creators_funds_accum.find("creators_name=?",  creatorName);

		return createHighChartsJSONForSingleCreatorV2(list, "time_", "funds_accum", "Funds", 
				sf, settings.getPrecision(), settings.getIso());
	}

	public static List<Map<String, String>> doUnitConversions(List<Model> list,
			UnitConverter sf, Integer precision, String iso, Boolean convertTimeToMillis, String... params) {

		// First, add the [ and commas ]
		String json = createJSONListOfMapsFromModelList(list, params);

		System.out.println(json);

		// Now get the Object
		List<Map<String, String>> lom = Tools.ListOfMapsPOJO(json);

		// Do the necessary unit conversions
		if (sf != null) {
			DecimalFormat df = null;
			if (precision != null && iso != null) {
				df = UnitConverter.setupDecimalFormat(iso, precision);
			} else {
				// This is the case when no user is logged in
				precision = 15;
				iso = "BTC";
				df = UnitConverter.setupDecimalFormat(iso, precision);
			}
			lom = sf.convertAndFormatMoney(lom, convertTimeToMillis,  iso, df);

		}

		return lom;
	}


	public static String convertLOMtoJson(List<Map<String, String>> lom) {
		return Tools.GSON.toJson(lom);
	}





	public static String createJSONListOfMapsFromModelList(List<Model> list, String... params) {
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

		return json;
	}




	public static String createHighChartsJSONForMultipleCreatorsV2(List<Model> list, String dateColName,
			String valueColName, String creatorsIdentifier, UnitConverter sf, Integer precision, String iso) {
		// TODO right now, doing 30 digits, and ignoring precision
		List<Map<String, String>> lom = doUnitConversions(list, sf, 30, iso, true);

		List<Map<String, Object>> highChartsLOM = new ArrayList<Map<String, Object>>();

		List<Object[]> oneCreatorsData = new ArrayList<Object[]>();

		for (int i = 0; i < lom.size(); i++) {
			Map<String, String> cMap = lom.get(i);

			Long millis = Long.parseLong(cMap.get(dateColName));
			// Strip the unicode char
			String valStr = cMap.get(valueColName);
			valStr = valStr.replaceAll("[^\\d.]", "");
			//			System.out.println(valStr);
			Double val = Double.parseDouble(valStr);

			Object[] pair = {millis, val};

			oneCreatorsData.add(pair);

			String cCreatorsId = cMap.get(creatorsIdentifier);
			//			System.out.println("creator = " + cCreatorsId + " map = " + Tools.GSON2.toJson(oneCreatorsData));
			// If its the last one, add it to the map
			if (i == list.size() -1) {

				Map<String, Object> map = new LinkedHashMap<String, Object>();
				//				System.out.println(cCreatorsId);
				map.put("name", cCreatorsId);
				map.put("data", oneCreatorsData);
				//				System.out.println("this is the last map" + Tools.GSON2.toJson(map));
				highChartsLOM.add(map);

				//				System.out.println("the final state = " + Tools.GSON2.toJson(highChartsLOM));
			} else {
				String nextCreatorsId = list.get(i+1).getString(creatorsIdentifier);

				if (!cCreatorsId.equals(nextCreatorsId)) {

					//					String oneCreatorsDataStr = Arrays.toString(oneCreatorsData.toArray());
					Map<String, Object> map = new LinkedHashMap<String, Object>();
					//					System.out.println(cCreatorsId);
					map.put("name", cCreatorsId);
					// NEEDED to add the new arraylist, because clearing the one data fucked it up
					map.put("data", new ArrayList<Object[]>(oneCreatorsData));


					highChartsLOM.add(map);
					//					System.out.println("just added this map" + Tools.GSON2.toJson(map));
					//					System.out.println("before clearing final state = " + Tools.GSON2.toJson(highChartsLOM));
					oneCreatorsData.clear();
					//					System.out.println("before final state = " + Tools.GSON2.toJson(highChartsLOM));
				}

			}

		}

		String json = Tools.GSON.toJson(highChartsLOM);
		//		System.out.println(json);
		return json;


	}

	public static String createHighChartsJSONForSingleCreatorV2(List<Model> list, String dateColName,
			String valueColName, String seriesName, UnitConverter sf, Integer precision, String iso) {

		List<Map<String, String>> lom = doUnitConversions(list, sf, precision, iso, true);

		List<Map<String, Object>> highChartsLOM = new ArrayList<Map<String, Object>>();

		List<Object[]> oneCreatorsData = new ArrayList<Object[]>();
		for (Map<String, String> e : lom) {
			Long millis = Long.parseLong(e.get(dateColName));
			// Strip the unicode char
			String valStr = e.get(valueColName);
			valStr = valStr.replaceAll("[^\\d.]", "");
			System.out.println(valStr);
			Double val = Double.parseDouble(valStr);

			Object[] pair = {millis, val};

			oneCreatorsData.add(pair);
		}

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", seriesName);
		map.put("data", oneCreatorsData);

		highChartsLOM.add(map);

		String json = Tools.GSON.toJson(highChartsLOM);
		System.out.println(json);
		return json;

	}

	@Deprecated
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

	@Deprecated
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


	@Deprecated
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

	public static String createHighChartsJSONForCurrentV2(List<Model> list, 
			String valueColName, String creatorsIdentifier, UnitConverter sf, Integer precision, String iso) {

		List<Map<String, String>> lom = doUnitConversions(list, sf, precision, iso, true);

		List<Object[]> data = new ArrayList<Object[]>();

		for (Map<String, String> cMap : lom) {

			String valStr = cMap.get(valueColName);
			valStr = valStr.replaceAll("[^\\d.]", "");
			Double val = Double.parseDouble(valStr);
			String cCreatorsId = cMap.get(creatorsIdentifier);
			Object[] pair = {cCreatorsId, val};
			data.add(pair);
		}

		String json = Tools.GSON.toJson(data);
		System.out.println(json);
		return json;

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
