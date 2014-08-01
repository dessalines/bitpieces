package com.bitpieces.shared.tools;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import org.codehaus.jackson.JsonNode;
import org.javalite.activejdbc.LazyList;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.Paginator;

import com.bitpieces.shared.Tables.Ask;
import com.bitpieces.shared.Tables.Backers_current;
import com.bitpieces.shared.Tables.Backers_current_count;
import com.bitpieces.shared.Tables.Bid;
import com.bitpieces.shared.Tables.Bids_asks_current;
import com.bitpieces.shared.Tables.Categories;
import com.bitpieces.shared.Tables.Creator;
import com.bitpieces.shared.Tables.Creators_activity;
import com.bitpieces.shared.Tables.Creators_categories;
import com.bitpieces.shared.Tables.Creators_funds_accum;
import com.bitpieces.shared.Tables.Creators_funds_current;
import com.bitpieces.shared.Tables.Creators_page_fields;
import com.bitpieces.shared.Tables.Creators_page_fields_view;
import com.bitpieces.shared.Tables.Creators_reputation;
import com.bitpieces.shared.Tables.Creators_search_view;
import com.bitpieces.shared.Tables.Creators_settings;
import com.bitpieces.shared.Tables.Creators_transactions;
import com.bitpieces.shared.Tables.Currencies;
import com.bitpieces.shared.Tables.Pieces_available_view;
import com.bitpieces.shared.Tables.Pieces_issued;
import com.bitpieces.shared.Tables.Pieces_issued_view;
import com.bitpieces.shared.Tables.Pieces_owned_accum;
import com.bitpieces.shared.Tables.Pieces_owned_value_accum;
import com.bitpieces.shared.Tables.Pieces_owned_value_current;
import com.bitpieces.shared.Tables.Pieces_owned_value_current_by_creator;
import com.bitpieces.shared.Tables.Pieces_owned_value_current_by_owner;
import com.bitpieces.shared.Tables.Prices;
import com.bitpieces.shared.Tables.Prices_for_user;
import com.bitpieces.shared.Tables.Rewards_earned_accum;
import com.bitpieces.shared.Tables.Rewards_earned_total;
import com.bitpieces.shared.Tables.Rewards_earned_total_by_user;
import com.bitpieces.shared.Tables.Rewards_owed;
import com.bitpieces.shared.Tables.Rewards_owed_to_user;
import com.bitpieces.shared.Tables.Rewards_view;
import com.bitpieces.shared.Tables.User;
import com.bitpieces.shared.Tables.Users_activity;
import com.bitpieces.shared.Tables.Users_funds_accum;
import com.bitpieces.shared.Tables.Users_funds_current;
import com.bitpieces.shared.Tables.Users_reputation;
import com.bitpieces.shared.Tables.Users_settings;
import com.bitpieces.shared.Tables.Users_transactions;
import com.bitpieces.shared.Tables.Worth;
import com.bitpieces.shared.tools.Tools.UserType;
import com.coinbase.api.Coinbase;
import com.coinbase.api.exception.CoinbaseException;

public class WebTools {



	private static final Integer PAGINATOR_ROWS = 100;

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


		DBActions.createBid(uid.getId(), 
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

		DBActions.createAsk(uid.getId(), 
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


		DBActions.sellFromCreator(creator.getId().toString(), 
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

	public static String newReward(UID uid, String body, UnitConverter sf) {
		Map<String, String> postMap = Tools.createMapFromAjaxPost(body);

		UsersSettings settings = new UsersSettings(uid);

		Double rewardPerPiecePerYear = Double.valueOf(postMap.get("reward_per_piece_per_year"));
		Double btcReward = rewardPerPiecePerYear;

		String message = null;
		// Convert amount if necessary
		if (!settings.getIso().equals("BTC")) {
			Double spotRate = sf.getSpotRate(settings.getIso());
			btcReward = rewardPerPiecePerYear / spotRate;
			System.out.println(rewardPerPiecePerYear + " / " + spotRate + " = " + btcReward);
			message = "Reward at " + btcReward + " BTC" + "(or "  + 
					rewardPerPiecePerYear + " " + settings.getIso() + " @ " + spotRate + settings.getIso() + "/BTC";
		} else {
			message = "Reward at " + btcReward + " BTC";
		}

		DBActions.issueReward(uid.getId(), btcReward);

		return message;


	}

	public static String raiseFunds(UID uid, String body, UnitConverter sf) {

		String message = newReward(uid, body, sf);
		String message2 = issuePieces(uid, body, sf);


		return message + "<br>" + message2;
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
		DBActions.makeDepositFake(uid.getId(),btcAmount, "fake1");

		return message;

	}

	public static void makeDepositFromCoinbaseCallback(String userId, String body) {


		try {
			JsonNode root = Tools.JACKSON.readTree(body);

			JsonNode order = root.get("order");


			String bitcents = order.get("total_btc").get("cents").asText();
			Double btcAmount = Double.valueOf(Double.parseDouble(bitcents)/1E8);

			String cb_tid = order.get("transaction").get("id").asText();
			String orderNumber = order.get("id").asText();
			String status = order.get("status").asText();

			System.out.println(bitcents + "|" + btcAmount + "|" + "cb_tid");
			
			DBActions.makeOrUpdateOrder(cb_tid, orderNumber);


			DBActions.makeOrUpdateDeposit(userId,btcAmount,cb_tid, status);




		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	public static String makeUserWithdrawal(Coinbase cb, UID uid, String body, UnitConverter sf) {
		Map<String, String> postMap = Tools.createMapFromAjaxPost(body);
		UsersSettings settings = new UsersSettings(uid);
		
		String addr = postMap.get("address");
		Double amount = Double.valueOf(postMap.get("withdrawAmount"));
		
		// For safety, convert the amount to BTC and make sure the user has that much
		Double btcAmount = amount;
		String message = null;
		if (!settings.getIso().equals("BTC")) {
			Double spotRate = sf.getSpotRate(settings.getIso());
			btcAmount = amount / spotRate;
			System.out.println(amount + " / " + spotRate + " = " + btcAmount);
			message = "withdrawal(pending) at " + btcAmount + " BTC" + "(or "  + 
					amount + " " + settings.getIso() + " @ " + spotRate + settings.getIso() + "/BTC";
		} else {
			message = "withdrawal(pending)  at " + btcAmount + " BTC";
		}
		
		Double currentFunds = 
				Users_funds_current.findFirst("users_id=?", uid.getId()).getDouble("current_funds");
		
		if (currentFunds >= btcAmount) {
			try {
			// Do the coinbase half, with the btc amount
			Map<String, String> results;
		
			results = CoinbaseTools.userWithdrawal(cb, btcAmount, addr);

			
			String cb_tid = results.get("cb_tid");
			String status = results.get("status");
			
			// Do the DB side
			DBActions.userWithdrawal(uid.getId(), cb_tid, btcAmount, status);
			
			} catch (CoinbaseException | IOException e) {

				throw new NoSuchElementException(e.getMessage());
			}
		} else {
			throw new NoSuchElementException("You only have " + currentFunds + " BTC, "
					+ "but are trying to withdraw " + btcAmount + " BTC");
		}
				
				
		return message;
	}
	
	public static String makeCreatorWithdrawal(Coinbase cb, UID uid, String body, UnitConverter sf) {
		Map<String, String> postMap = Tools.createMapFromAjaxPost(body);
		UsersSettings settings = new UsersSettings(uid);
		
		String addr = postMap.get("address");
		Double amount = Double.valueOf(postMap.get("withdrawAmount"));
		
		// For safety, convert the amount to BTC and make sure the user has that much
		Double btcAmount = amount;
		String message = null;
		
		if (!settings.getIso().equals("BTC")) {
			Double spotRate = sf.getSpotRate(settings.getIso());
			btcAmount = amount / spotRate;
			
			
			message = "withdrawal(pending) at " + btcAmount*(1d-DBActions.SERVICE_FEE_PCT) + " BTC (after fee)" + "(or "  + 
					amount*(1d-DBActions.SERVICE_FEE_PCT)+ " (after fee) " + settings.getIso() + " @ " + spotRate + settings.getIso() + "/BTC";
		} else {
			message = "withdrawal(pending)  at " + btcAmount*(1d-DBActions.SERVICE_FEE_PCT) + " BTC (after fee)";
		}
		
		Double currentFunds = 
				Creators_funds_current.findFirst("creators_id=?", uid.getId()).getDouble("current_funds");
		
		Double fee = DBActions.SERVICE_FEE_PCT * btcAmount;

		Double amountAfterFee = btcAmount - fee;
		
		
		if (currentFunds >= amountAfterFee) {
			try {
			// Do the coinbase half, with the btc amount
			Map<String, String> results;
		
			// Give this one the amount after the fee
			results = CoinbaseTools.userWithdrawal(cb, amountAfterFee, addr);

			
			String cb_tid = results.get("cb_tid");
			String status = results.get("status");
			
			// Do the DB side
			// give this one the full amount, because it does the fee on its own
			DBActions.creatorWithdrawal(uid.getId(), cb_tid, btcAmount, status);
			
			} catch (CoinbaseException | IOException e) {

				throw new NoSuchElementException(e.getMessage());
			}
		} else {
			throw new NoSuchElementException("You only have " + currentFunds + " BTC, "
					+ "but are trying to withdraw " + btcAmount + " BTC");
		}
				
				
		return message;
	}









	





	public static String getPiecesOwnedValueAccumSeriesJson(String userName, UID uid,UnitConverter sf) {

		UsersSettings settings = new UsersSettings(uid);
		// First fetch from the table
		List<Model> list = Pieces_owned_value_accum.find("owners_name=?", userName);

		if (list.size() > 0 ) { 
			return createHighChartsJSONForMultipleCreatorsV2(list, "price_time_", "value_accum", "creators_username",
					sf, settings.getPrecision(), settings.getIso());
		} else {
			return "0";
		}

	}

	public static String getPiecesOwnedAccumSeriesJson(String userName, UID uid,
			UnitConverter sf) {
		UsersSettings settings = new UsersSettings(uid);
		// First fetch from the table
		List<Model> list = Pieces_owned_accum.find("owners_name=?", userName);
		if (list.size() > 0 ) { 
			return createHighChartsJSONForMultipleCreatorsV2(list, "start_time_", "pieces_accum", "creators_username",
					sf, settings.getPrecision(), settings.getIso());
		} else {
			return "0";
		}
	}



	public static String getPricesForUserSeriesJson(String userName, UID uid, UnitConverter sf) {
		UsersSettings settings = new UsersSettings(uid);
		// First fetch from the table
		List<Model> list = Prices_for_user.find("owners_name=?", userName);

		if (list.size() > 0 ) { 
			return createHighChartsJSONForMultipleCreatorsV2(list, "time_", "price_per_piece", "creators_username",
					sf, settings.getPrecision(), settings.getIso());
		} else {
			return "0";
		}


	}

	public static String getPiecesOwnedValueCurrentSeriesJson(String userName, UID uid, UnitConverter sf) {

		// First fetch from the table
		List<Model> list = Pieces_owned_value_current.find("owners_name=?", userName);

		UsersSettings settings = new UsersSettings(uid);

		if (list.size()>0) { 
			return createHighChartsJSONForCurrentV2(list, "value_total_current", "creators_username", sf,
					settings.getPrecision(), settings.getIso());
		} else {
			return "0";
		}

	}

	public static String getRewardsEarnedTotalJson(String userName, UID uid, UnitConverter sf) {
		UsersSettings settings = new UsersSettings(uid);
		// First fetch from the table
		List<Model> list = Rewards_earned_total.find("owners_name=?", userName);

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

		if (list.size() > 0) {
			return createHighChartsJSONForCurrentV2(list, "value_total_current", "owners_name",
					sf, settings.getPrecision(), settings.getIso());
		} else {
			return "0";
		}
	}

	public static String getPiecesOwnedValueCurrentSeriesJson(String userName, String creatorName, UID uid, UnitConverter sf) {
		UsersSettings settings = new UsersSettings(uid);
		String json = null;
		try {
			// First fetch from the table
			Pieces_owned_value_current p = Pieces_owned_value_current.findFirst("owners_name=? and creators_username=?",
					userName, creatorName);

			String val = p.getString("value_total_current");
			json = sf.convertSingleValueCurrentJson(val, settings.getIso(), settings.getPrecision());
		} catch(NullPointerException e) {
			return "0";
		}
		return json;

	}

	public static String getPiecesOwnedCurrentSeriesJson(String userName, String creatorName, UID uid, UnitConverter sf) {

		try {
			// First fetch from the table
			Pieces_owned_value_current p = Pieces_owned_value_current.findFirst("owners_name=? and creators_username=?", 
					userName, creatorName);
			String val = p.getString("pieces_total");
			return val;

		} catch(NullPointerException e) {
			return "0";
		}


	}

	public static String getRewardsEarnedAccumSeriesJson(String userName, UID uid, UnitConverter sf) {
		UsersSettings settings = new UsersSettings(uid);
		// First fetch from the table
		List<Model> list = Rewards_earned_accum.find("owners_name=?", userName);

		if (list.size() > 0 ) {
			return createHighChartsJSONForMultipleCreatorsV2(list, "start_time_", "reward_accum", "creators_username",
					sf, settings.getPrecision(), settings.getIso());
		} else {
			return "0";
		}
	}

	public static String getUsersFundsAccumSeriesJson(String userName, UID uid, UnitConverter sf) {

		UsersSettings settings = new UsersSettings(uid);

		// First fetch from the table
		List<Model> list = Users_funds_accum.find("owners_name=?", userName);

		return createHighChartsJSONForSingleCreatorV2(list, "time_", "funds_accum", "Funds",
				sf, settings.getPrecision(), settings.getIso());


	}


	public static String getUsersTransactionsJson(String userName, UID uid, UnitConverter sf, Integer pageNum) {

		UsersSettings settings = new UsersSettings(uid);

		Paginator p = new Paginator(Users_transactions.class, PAGINATOR_ROWS, "owners_name=?",  userName);
		List<Model> list = p.getPage(pageNum);

		if (list.size() > 0) {
			return convertLOMtoJson(doUnitConversions(list, sf, settings.getPrecision(), settings.getIso(), false));
		} else {
			return "0";
		}


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

	public static String getUsersActivityJson(String userName, UID uid, UnitConverter sf, Integer pageNum) {
		UsersSettings settings = new UsersSettings(uid);

		Paginator p = new Paginator(Users_activity.class, PAGINATOR_ROWS, "owners_name=?",  userName);
		List<Model> list = p.getPage(pageNum);

		if (list.size() > 0) {
			return convertLOMtoJson(doUnitConversions(list, sf, settings.getPrecision(), settings.getIso(), false));
		} else {
			return "0";
		}

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

		if (email != null) {
			s.put("email", email);
		}
		if (currency != null) {
			s.put("local_currency_id", currId);
		}
		if (precision != null) {
			s.put("precision_", precision);
		}


		for (Entry<String, String> e : s.entrySet()) {
			user.set(e.getKey(), e.getValue()).saveIt();
		}


		return "Settings updated";

	}

	public static String saveCreatorsCategories(UID uid, String body) {
		//		Map<String, String> postMap = Tools.createMapFromAjaxPost(body);
		List<String> categories = Tools.createArrayFromAjaxPostSelect(body);
		System.out.println(categories);


		// first, delete them all for that creator
		Creators_categories.delete("creators_id = ?", uid.getId());

		for (String cCat : categories) {
			// Find the categories by id and add them to the creator
			Categories catRow = Categories.findFirst("name = ?", cCat);
			Creators_categories.createIt("creators_id", uid.getId(), "categories_id", catRow.getId());

		}
		return "Categories Saved";
	}

	public static String getUsersBidsAsksCurrentJson(String userName, UID uid, UnitConverter sf) {
		UsersSettings settings = new UsersSettings(uid);

		List<Model> list = Bids_asks_current.find("users_name=?",  userName).orderBy("time_ desc");
		if (list.size()>0) {
			String json = convertLOMtoJson(doUnitConversions(list, sf, settings.getPrecision(), settings.getIso(), false));
			return json;
		} else {
			return "0";
		}

	}

	public static String getUsersFundsCurrentJson(String userName, UID uid, UnitConverter sf) {
		UsersSettings settings = new UsersSettings(uid);
		String json = null;
		try {
			Users_funds_current usersFundsCurrent = Users_funds_current.findFirst("owners_name=?",  userName);

			String val = usersFundsCurrent.getString("current_funds");
			json = sf.convertSingleValueCurrentJson(val, settings.getIso(), settings.getPrecision());
		} catch(NullPointerException e) {
			return "0";
		}
		return json;

	}

	public static String getCreatorsFundsCurrentJson(String creatorName, UID uid, UnitConverter sf) {
		UsersSettings settings = new UsersSettings(uid);
		String json = null;
		try {
			Creators_funds_current creatorsFundsCurrent = Creators_funds_current.findFirst("creators_name=?",  creatorName);

			String val = creatorsFundsCurrent.getString("current_funds");
			json = sf.convertSingleValueCurrentJson(val, settings.getIso(), settings.getPrecision());
		} catch(NullPointerException e) {
			return "0";
		}
		return json;

	}

	public static String getRewardsEarnedTotalByUserJson(String userName, UID uid, UnitConverter sf) {
		UsersSettings settings = new UsersSettings(uid);
		String json = null;
		try {
			Rewards_earned_total_by_user rewardsEarned = Rewards_earned_total_by_user.findFirst(
					"owners_name=?",  userName);

			String val = rewardsEarned.getString("reward_earned_total");
			json = sf.convertSingleValueCurrentJson(val, settings.getIso(), settings.getPrecision());
		} catch(NullPointerException e) {
			return "0";
		}
		return json;

	}

	public static String getPiecesValueCurrentByOwnerJson(String userName, UID uid, UnitConverter sf) {
		UsersSettings settings = new UsersSettings(uid);
		String json = null;
		try {
			Pieces_owned_value_current_by_owner value = Pieces_owned_value_current_by_owner.findFirst(
					"owners_name=?",  userName);

			String val = value.getString("value_total_current");
			json = sf.convertSingleValueCurrentJson(val, settings.getIso(), settings.getPrecision());
		} catch(NullPointerException e) {
			return "0";
		}
		return json;

	}

	public static String getUsersReputationJson(String userName) {


		String json = null;

		try {
			Users_reputation value = Users_reputation.findFirst("owners_name=?",  userName);

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

		if (p != null) {
			String val = p.getString("value_total_current");

			String json = sf.convertSingleValueCurrentJson(val, settings.getIso(), settings.getPrecision());
			return json;
		} else {
			return "0";
		}

	}

	public static String getPricePerPieceCurrentJson(
			String creatorName, UID uid, UnitConverter sf) {
		UsersSettings settings = new UsersSettings(null);
		if (uid != null) {
			settings = new UsersSettings(uid);
		}


		LazyList<Prices> prices = Prices.where("creators_name = ?", creatorName).orderBy("time_ desc").limit(1);
		if (prices.size() > 0) {
			Prices p = prices.get(0);
			String val = p.getString("price_per_piece");

			return  sf.convertSingleValueCurrentJson(val, settings.getIso(), settings.getPrecision());
		} else {
			return "0";
		}

	}

	public static String getRewardsOwedJson(
			String creatorName, UID uid, UnitConverter sf) {
		UsersSettings settings = new UsersSettings(null);
		if (uid != null) {
			settings = new UsersSettings(uid);
		}

		Rewards_owed r = Rewards_owed.findFirst("creators_username = ?", creatorName);

		if (r != null) {
			String val = r.getString("total_owed");

			String json = sf.convertSingleValueCurrentJson(val, settings.getIso(), settings.getPrecision());

			return json; 
		} else {
			return "0.0";
		}

	}

	public static String getBackersCurrentCountJson(
			String creatorName) {

		Backers_current_count r = Backers_current_count.findFirst("creators_username = ?", creatorName);

		if (r != null) {
			String json = r.getString("number_of_backers");

			return json;
		} else {
			return "0";
		}

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
		if (list.size() > 0) {
			return createHighChartsJSONForSingleCreatorV2(list, "time_", "price_per_piece", "Pricing",
					sf, settings.getPrecision(), settings.getIso());
		} else {
			return "0";
		}

	}

	public static String getWorthJson(
			String creatorName, UID uid, UnitConverter sf) {

		UsersSettings settings = new UsersSettings(null);
		if (uid != null) {
			settings = new UsersSettings(uid);
		}

		List<Model> list = Worth.find("creators_username=?", creatorName);
		if (list.size() > 0) {
			return createHighChartsJSONForSingleCreatorV2(list, "price_time_", "worth", "Worth",
					sf, settings.getPrecision(), settings.getIso());
		} else {
			return "0";
		}

	}

	public static String getBidsAsksCurrentJson(String creatorName, UID uid, UnitConverter sf, Integer pageNum) {

		Paginator p = new Paginator(Bids_asks_current.class, PAGINATOR_ROWS, "creators_name=?",  creatorName);
		List<Model> list = p.getPage(pageNum);
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

	public static String getRewardsJson(String creatorName, UID uid, UnitConverter sf, Integer pageNum) {

		Paginator p = new Paginator(Rewards_view.class, PAGINATOR_ROWS,"creators_name=?",  creatorName);
		List<Model> list = p.getPage(pageNum);

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

	public static String getRewardsCurrentJson(String creatorName, UID uid, UnitConverter sf) {

		List<Model> list = Rewards_view.find("creators_name=?",  creatorName).orderBy("time_ desc").limit(1);

		UsersSettings settings = new UsersSettings(null);
		if (uid != null) {
			settings = new UsersSettings(uid);
		} 
		if (list.size() > 0) {
			String val = list.get(0).getString("reward_per_piece_per_year");
			String json = sf.convertSingleValueCurrentJson(val, settings.getIso(), settings.getPrecision());
			return json;
		} else {
			return "0";
		}

	}

	public static String getRewardsOwedToUserJson(String creatorName, UID uid, UnitConverter sf,Integer pageNum) {

		Paginator p = new Paginator(Rewards_owed_to_user.class, PAGINATOR_ROWS,"creators_username=?",  creatorName);
		List<Model> list = p.getPage(pageNum);

		UsersSettings settings = new UsersSettings(null);
		if (uid != null) {
			settings = new UsersSettings(uid);
		} 
		if (list.size() > 0) {
			return convertLOMtoJson(doUnitConversions(list, sf, settings.getPrecision(), settings.getIso(), false,
					"creators_username", "owners_name", "total_owed"));
		} else {
			return "0";
		}
	}

	public static String getPiecesIssuedJson(String creatorName, UID uid, UnitConverter sf, Integer pageNum) {


		Paginator p = new Paginator(Pieces_issued_view.class, PAGINATOR_ROWS, "creators_name=?",  creatorName);
		List<Model> list = p.getPage(pageNum);

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

	public static String getPiecesIssuedMostRecentPriceJson(String creatorName, UID uid, UnitConverter sf) {
		UsersSettings settings = new UsersSettings(null);
		if (uid != null) {
			settings = new UsersSettings(uid);
		} 

		List<Model> p = Pieces_issued_view.find("creators_name=?",  creatorName).orderBy("time_ desc").limit(1);
		String val = p.get(0).getString("price_per_piece");

		return sf.convertSingleValueCurrentJson(val, settings.getIso(), settings.getPrecision());

	}

	public static String getBackersCurrentJson(String creatorName, UID uid, UnitConverter sf, Integer pageNum) {

		Paginator p = new Paginator(Backers_current.class, PAGINATOR_ROWS, "creators_username=?",  creatorName);
		List<Model> list = p.getPage(pageNum);

		UsersSettings settings = new UsersSettings(null);
		if (uid != null) {
			settings = new UsersSettings(uid);
		} 

		if (list.size() >0 ) {
			return convertLOMtoJson(doUnitConversions(list, sf, settings.getPrecision(), settings.getIso(), false,
					"users_username", "pieces_total", "value_total_current"));
		} else {
			return "0";
		}

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

	public static String getCreatorsActivityJson(String creatorName, UID uid, UnitConverter sf, Integer pageNum) {

		//		List<Model> list = Creators_activity.find("creators_name=?",  creatorName);

		UsersSettings settings = new UsersSettings(null);
		if (uid != null) {
			settings = new UsersSettings(uid);
		} 
		Paginator p = new Paginator(Creators_activity.class, PAGINATOR_ROWS, "creators_name=?", creatorName);
		List<Model> items = p.getPage(pageNum);

		if (items.size() > 0 ) {
			return convertLOMtoJson(doUnitConversions(items, sf, settings.getPrecision(), settings.getIso(), false));
		} else {
			return "0";
		}

	}

	public static String getCreatorsTransactionsJson(String creatorName, UID uid, UnitConverter sf, Integer pageNum) {

		UsersSettings settings = new UsersSettings(null);
		if (uid != null) {
			settings = new UsersSettings(uid);
		} 

		Paginator p = new Paginator(Creators_transactions.class, PAGINATOR_ROWS, "creators_name=?", creatorName);

		List<Model> list = p.getPage(pageNum);


		if (list.size() > 0) {
			return convertLOMtoJson(doUnitConversions(list, sf, settings.getPrecision(), settings.getIso(), false));
		} else {
			return "0";
		}
	}

	public static String getCreatorsFundsAccumJson(String creatorName, UID uid, UnitConverter sf) {
		UsersSettings settings = new UsersSettings(null);
		if (uid != null) {
			settings = new UsersSettings(uid);
		}

		List<Model> list = Creators_funds_accum.find("creators_name=?",  creatorName);
		if (list.size() > 0) {
			return createHighChartsJSONForSingleCreatorV2(list, "time_", "funds_accum", "Funds", 
					sf, settings.getPrecision(), settings.getIso());
		} else {
			return "0";
		}
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
		List<Map<String, String>> lom = doUnitConversions(list, sf, 19, iso, true);

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

		List<Map<String, String>> lom = doUnitConversions(list, sf, 20, iso, true);

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
