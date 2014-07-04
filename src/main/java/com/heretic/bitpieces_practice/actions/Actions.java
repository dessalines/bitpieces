package com.heretic.bitpieces_practice.actions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.commons.lang3.StringEscapeUtils;
import org.javalite.activejdbc.LazyList;

import com.google.gson.Gson;
import com.heretic.bitpieces_practice.tables.Tables.Ask;
import com.heretic.bitpieces_practice.tables.Tables.Ask_bid_accept_checker;
import com.heretic.bitpieces_practice.tables.Tables.Bid;
import com.heretic.bitpieces_practice.tables.Tables.Creator;
import com.heretic.bitpieces_practice.tables.Tables.Creators_btc_address;
import com.heretic.bitpieces_practice.tables.Tables.Creators_page_fields;
import com.heretic.bitpieces_practice.tables.Tables.Creators_required_fields;
import com.heretic.bitpieces_practice.tables.Tables.Fees;
import com.heretic.bitpieces_practice.tables.Tables.Host_btc_addresses;
import com.heretic.bitpieces_practice.tables.Tables.Pieces_available;
import com.heretic.bitpieces_practice.tables.Tables.Pieces_owned;
import com.heretic.bitpieces_practice.tables.Tables.Pieces_owned_total;
import com.heretic.bitpieces_practice.tables.Tables.Sales_from_creators;
import com.heretic.bitpieces_practice.tables.Tables.Sales_from_users;
import com.heretic.bitpieces_practice.tables.Tables.User;
import com.heretic.bitpieces_practice.tables.Tables.Users_btc_address;
import com.heretic.bitpieces_practice.tables.Tables.Users_required_fields;
import com.heretic.bitpieces_practice.tools.Tools;
import com.heretic.bitpieces_practice.tools.Tools.UserType;
import com.heretic.bitpieces_practice.tools.UserTypeAndId;

public class Actions {
	public static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final Double SERVICE_FEE_PCT = .05d;
	private static final Gson GSON = new Gson();

	public static Bid createBid(String userId, String creatorId, Integer pieces, Double bid_amount, 
			String validUntil, Boolean partial) {

		// First, verify that the creator has that many pieces available
		Pieces_available pieces_available_obj = Pieces_available.findFirst("creators_id = ?", creatorId);
		Integer pieces_available = pieces_available_obj.getInteger("pieces_available");

		if (pieces > pieces_available) {
			throw new NoSuchElementException("You are bidding for " + pieces + " pieces, but only " +
					pieces_available + " are available");
		}

		Bid bid = Bid.create("users_id", userId, 
				"creators_id", creatorId,
				"time_", SDF.format(new Date()),
				"valid_until",validUntil,
				"partial_fill", partial,
				"pieces", pieces,
				"bid", bid_amount);

		bid.saveIt();

		return bid;

	}

	public static Ask createAsk(String userId, String creatorId, Integer pieces, Double ask_amount,
			String validUntil, Boolean partial) {

		// First, verify that you have that many pieces to sell
		Pieces_owned_total pieces_owned_total_obj = 
				Pieces_owned_total.findFirst("creators_id = ? and owners_id = ?", creatorId, userId);


		Integer pieces_owned = (pieces_owned_total_obj !=null) ? pieces_owned_total_obj.getInteger("pieces_owned_total") : 0;

		if (pieces > pieces_owned) {
			throw new NoSuchElementException("You are trying to sell " + pieces + " pieces, but only have " +
					pieces_owned +".");
		}

		Ask ask = Ask.create("users_id", userId, 
				"creators_id", creatorId,
				"time_", SDF.format(new Date()),
				"valid_until",validUntil,
				"partial_fill", partial,
				"pieces", pieces,
				"ask", ask_amount);

		ask.saveIt();

		return ask;

	}

	public static Sales_from_creators sellFromCreator(Creators_btc_address creatorsBtcAddr, Users_btc_address userBtcAddr, 
			Integer pieces, Double price) {

		Integer creatorsId = creatorsBtcAddr.getInteger("creators_id");
		Integer ownersId = userBtcAddr.getInteger("users_id");

		// First, verify that there are that many pieces available from the creator
		Integer pieces_available = Pieces_available.findFirst("creators_id = ?", creatorsId).getInteger("pieces_available");

		if (pieces_available < pieces) {
			throw new NoSuchElementException("You are trying to sell " + pieces + " pieces, but only " +
					pieces_available + " are available");
		}

		Double amount_to_host = price*SERVICE_FEE_PCT;
		Double amount_to_user = price - amount_to_host;


		String dateOfTransactionStr = SDF.format(new Date());
		// Do the transaction
		Sales_from_creators sale = Sales_from_creators.create("from_creators_btc_addr_id", creatorsBtcAddr.getId(),
				"to_users_btc_addr_id", userBtcAddr.getId(),
				"time_", dateOfTransactionStr,
				"pieces", pieces,
				"price", amount_to_user);

		sale.saveIt();

		// Charge the fee
		Fees fee = Fees.create("sales_from_creators_id", sale.getId(),
				"host_btc_addr_id", Host_btc_addresses.findFirst("").getId(),
				"fee", amount_to_host);
		fee.saveIt();




		// User now owns pieces
		Pieces_owned pieces_owned = Pieces_owned.create("owners_id", ownersId,
				"creators_id", creatorsId,
				"time_", dateOfTransactionStr,
				"pieces_owned", pieces);

		pieces_owned.saveIt();


		return sale;

	}

	public static void askBidAccepter() {

		Boolean rerun = false;
		// Look at the view, and get the list of rows
		List<Ask_bid_accept_checker> rows = Ask_bid_accept_checker.findAll();

		// Iterate over each row
		for (Ask_bid_accept_checker cRow : rows) {

			// Partial fill options : either create/update the row and do the query again
			// If it does any updating, then exit the loop, and put a flag to rerun it again
			Integer askersId = cRow.getInteger("askers_id");
			Integer biddersId = cRow.getInteger("bidders_id");
			Integer creatorsId = cRow.getInteger("creators_id");

			Integer askPieces = cRow.getInteger("ask_pieces");
			Integer bidPieces = cRow.getInteger("bid_pieces");

			Integer askId = cRow.getInteger("ask_id");
			Integer bidId = cRow.getInteger("bid_id");

			Double askPrice = cRow.getDouble("ask");
			Double bidPrice = cRow.getDouble("bid");

			String askValidUntil = cRow.getString("ask_valid_until");
			String bidValidUntil = cRow.getString("bid_valid_until");

			Users_btc_address fromUserBtcAddr = Users_btc_address.findFirst("users_id = ?", askersId);
			Users_btc_address toUserBtcAddr = Users_btc_address.findFirst("users_id = ?", biddersId);

			// If the bidder wants more than the asker has:
			Integer askMinusBidPieces = askPieces - bidPieces;
			Integer piecesForTransaction = Math.min(askPieces, bidPieces);
			System.out.println("ask minus bid pieces = " + askMinusBidPieces);
			System.out.println("pieces for transaction = " + piecesForTransaction);


			String dateOfTransaction = SDF.format(new Date());
			// Do the sale at the askers price
			sellFromUser(fromUserBtcAddr, toUserBtcAddr, creatorsId, piecesForTransaction, bidPrice);


			if (bidPieces > askPieces) {

				// close out the askers, cause he's sold them all
				Ask ask = Ask.findById(askId);
				ask.set("valid_until", dateOfTransaction);
				ask.saveIt();

				// update the valid until, and create a new bid row
				Bid bid = Bid.findById(bidId);
				bid.set("valid_until", dateOfTransaction);
				bid.saveIt();

				Integer newPieces = bidPieces - piecesForTransaction;

				// Create a new bid row, with the same params except 
				Bid.createIt("users_id", biddersId,
						"creators_id", creatorsId,
						"time_", dateOfTransaction,
						"valid_until", bidValidUntil,
						"partial_fill", true,
						"pieces", newPieces,
						"bid", bidPrice);

				rerun = true;
				break;



			} else if (askPieces >= bidPieces){
				// close out the bidders, cause he's sold them all
				Bid bid = Bid.findById(bidId);
				bid.set("valid_until", dateOfTransaction);
				bid.saveIt();

				// update the valid until, and create a new ask row
				Ask ask = Ask.findById(askId);
				ask.set("valid_until", dateOfTransaction);
				ask.saveIt();

				Integer newPieces = askPieces - piecesForTransaction;

				// Create a new ask row, with the same params except 
				// Only do this if the pieces are greater than 0
				if (!(askMinusBidPieces == 0)) {
					Ask.createIt("users_id", askersId,
							"creators_id", creatorsId,
							"time_", dateOfTransaction,
							"valid_until", askValidUntil,
							"partial_fill", true,
							"pieces", newPieces,
							"ask", askPrice);
				}

				rerun = true;
				break;

			} else {
				System.out.println("got here!");
			}





			// now update the valid_until on those bid/ask rows
			//			Integer bidPiecesLeft = bidPieces

			// Create new bid/ask rows for the new amounts of pieces, with the valid until date





		}

		if (rerun) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			askBidAccepter();
		}

	}

	public static Sales_from_users sellFromUser(Users_btc_address fromUserBtcAddr,
			Users_btc_address toUserBtcAddr, Integer creatorsId, Integer pieces,
			Double price) {

		String dateOfTransactionStr = SDF.format(new Date());
		Integer sellersId = fromUserBtcAddr.getInteger("users_id");
		Integer buyersId = toUserBtcAddr.getInteger("users_id");


		// Make sure that the from user actually has those pieces, and subtract them from pieces owned
		Pieces_owned_total pieces_owned_total_obj = Pieces_owned_total.findFirst("owners_id = ? and creators_id = ?", sellersId, creatorsId);
		Integer pieces_owned_total = pieces_owned_total_obj.getInteger("pieces_owned_total");


		if (pieces_owned_total < pieces) {
			throw new NoSuchElementException("You are trying to sell " + pieces + " pieces, but you only own " +
					pieces_owned_total + ".");
		}

		Pieces_owned pieces_owned_seller = Pieces_owned.create("owners_id", sellersId,
				"creators_id", creatorsId,
				"time_", dateOfTransactionStr,
				"pieces_owned", -pieces);
		pieces_owned_seller.saveIt();

		Pieces_owned pieces_owned_buyer = Pieces_owned.create("owners_id", buyersId,
				"creators_id", creatorsId,
				"time_", dateOfTransactionStr,
				"pieces_owned", pieces);
		pieces_owned_buyer.saveIt();


		Sales_from_users sale = Sales_from_users.create("from_users_btc_addr_id", fromUserBtcAddr.getId(),
				"to_users_btc_addr_id", toUserBtcAddr.getId(),
				"creators_id", creatorsId,
				"time_", dateOfTransactionStr,
				"pieces", pieces,
				"price", price);

		sale.saveIt();

		// Change the pieces owned

		return sale;
	}

	public static UserTypeAndId createUserFromAjax(String reqBody) {

		// Create a user
		User user = new User();
		user.saveIt();

		System.out.println("got here");

		// create user 
		Map<String, String> postMap = Tools.createMapFromAjaxPost(reqBody);

		// Create the required fields 
		try {
			Users_required_fields userRequiredFields = Users_required_fields.createIt("users_id", user.getId(),
					"username", postMap.get("username"),
					"password_encrypted", Tools.PASS_ENCRYPT.encryptPassword(postMap.get("password")),
					"email", postMap.get("email"));
		} catch (org.javalite.activejdbc.DBException e) {
			return null;
		}

		UserTypeAndId uid = new UserTypeAndId(UserType.User, String.valueOf(user.getId()));
		return uid;
	}
	
	public static UserTypeAndId createCreatorFromAjax(String reqBody) {

		// Create a user
		Creator creator = new Creator();
		creator.saveIt();


		Map<String, String> postMap = Tools.createMapFromAjaxPost(reqBody);

		// Create the required fields 
		try {
			Creators_required_fields creatorRequiredFields = Creators_required_fields.createIt("creators_id", creator.getId(),
					"username", postMap.get("username"),
					"password_encrypted", Tools.PASS_ENCRYPT.encryptPassword(postMap.get("password")),
					"email", postMap.get("email"));
		} catch (org.javalite.activejdbc.DBException e) {
			return null;
		}
		
		// TODO Create the static html5 page for that creator

		UserTypeAndId uid = new UserTypeAndId(UserType.Creator, String.valueOf(creator.getId()));
		return uid;
	}

	public static UserTypeAndId userLogin(String reqBody) {

		Map<String, String> postMap = Tools.createMapFromAjaxPost(reqBody);

		// fetch the required fields
		Users_required_fields user = Users_required_fields.findFirst("username = '" + postMap.get("username") + "'");
		if (user==null) {
			return null;
		}

		String encryptedPassword = user.getString("password_encrypted");

		Boolean correctPass = Tools.PASS_ENCRYPT.checkPassword(postMap.get("password"), encryptedPassword);

		UserTypeAndId returnVal = (correctPass == true) ? new UserTypeAndId(UserType.User, user.getString("users_id")) : null;
		
		return returnVal;

	}
	
	public static UserTypeAndId creatorLogin(String reqBody) {

		Map<String, String> postMap = Tools.createMapFromAjaxPost(reqBody);

		// fetch the required fields
		Creators_required_fields user = Creators_required_fields.findFirst("username = '" + postMap.get("username") + "'");
		if (user==null) {
			return null;
		}

		String encryptedPassword = user.getString("password_encrypted");

		Boolean correctPass = Tools.PASS_ENCRYPT.checkPassword(postMap.get("password"), encryptedPassword);

		UserTypeAndId returnVal = (correctPass == true) ? new UserTypeAndId(UserType.Creator, user.getString("creators_id")) : null;
		
		return returnVal;



	}

	public static String getPiecesOwnedTotal(String userId) {
		LazyList<Pieces_owned_total> pieces_owned_total = Pieces_owned_total.where("owners_id = ?", userId);

		return pieces_owned_total.toJson(true, "creators_id", "pieces_owned_total");
		
//		return GSON.toJson(pieces_owned_total.toMaps());


	}




}
