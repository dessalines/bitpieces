package com.heretic.bitpieces_practice.actions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.javalite.activejdbc.LazyList;

import com.google.gson.Gson;
import com.heretic.bitpieces_practice.tables.Tables.Ask;
import com.heretic.bitpieces_practice.tables.Tables.Ask_bid_accept_checker;
import com.heretic.bitpieces_practice.tables.Tables.Badge;
import com.heretic.bitpieces_practice.tables.Tables.Bid;
import com.heretic.bitpieces_practice.tables.Tables.Creator;
import com.heretic.bitpieces_practice.tables.Tables.Creators_funds_current;
import com.heretic.bitpieces_practice.tables.Tables.Creators_withdrawals;
import com.heretic.bitpieces_practice.tables.Tables.Pieces_available;
import com.heretic.bitpieces_practice.tables.Tables.Pieces_owned;
import com.heretic.bitpieces_practice.tables.Tables.Pieces_owned_total;
import com.heretic.bitpieces_practice.tables.Tables.Pieces_owned_value_current_by_creator;
import com.heretic.bitpieces_practice.tables.Tables.Rewards_current;
import com.heretic.bitpieces_practice.tables.Tables.Sales_from_creators;
import com.heretic.bitpieces_practice.tables.Tables.Sales_from_users;
import com.heretic.bitpieces_practice.tables.Tables.User;
import com.heretic.bitpieces_practice.tables.Tables.Users_badges;
import com.heretic.bitpieces_practice.tables.Tables.Users_deposits;
import com.heretic.bitpieces_practice.tables.Tables.Users_funds_current;
import com.heretic.bitpieces_practice.tables.Tables.Users_withdrawals;
import com.heretic.bitpieces_practice.tools.Tools;
import com.heretic.bitpieces_practice.tools.Tools.UserType;
import com.heretic.bitpieces_practice.tools.UserTypeAndId;

public class Actions {
	public static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final Double SERVICE_FEE_PCT = .05d;
	private static final Gson GSON = new Gson();

	public static Bid createBid(String userId, String creatorId, Integer pieces, Double bid_per_piece, 
			String validUntil, Boolean partial) {

		Double amount = bid_per_piece * pieces;

		// First verify that the user has the funds to buy that amount
		try {
			Double userFunds = Users_funds_current.findFirst("users_id = ?", userId).getDouble("current_funds");

			if (userFunds < amount) {
				throw new NoSuchElementException("You have only " + userFunds + " $, but are trying to buy " +
						amount);
			}
		} catch(NullPointerException e) {
			throw new NoSuchElementException("the user has no funds");
		}

		Bid bid = Bid.create("users_id", userId, 
				"creators_id", creatorId,
				"time_", SDF.format(new Date()),
				"valid_until",validUntil,
				"partial_fill", partial,
				"pieces", pieces,
				"bid_per_piece", bid_per_piece);

		bid.saveIt();

		return bid;

	}

	public static Ask createAsk(String userId, String creatorId, Integer pieces, Double ask_per_piece,
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
				"ask_per_piece", ask_per_piece);

		ask.saveIt();

		return ask;

	}

	public static Sales_from_creators sellFromCreator(String creatorsId, String usersId, 
			Integer pieces, Double price_per_piece) {

		// First, verify that there are that many pieces available from the creator
		Integer pieces_available = Pieces_available.findFirst("creators_id = ?", creatorsId).getInteger("pieces_available");

		if (pieces_available < pieces) {
			throw new NoSuchElementException("You are trying to sell " + pieces + " pieces, but only " +
					pieces_available + " are available");
		}


		Double total = price_per_piece * pieces;

		// Also verify that the user has the funds to buy that amount
		try {
			Double userFunds = Users_funds_current.findFirst("users_id = ?", usersId).getDouble("current_funds");

			if (userFunds < total) {
				throw new NoSuchElementException("You have only " + userFunds + " $, but are trying to buy " +
						pieces + " pieces worth $" + total);
			}
		} catch(NullPointerException e) {
			throw new NoSuchElementException("You have no funds");
		}





		String dateOfTransactionStr = SDF.format(new Date());
		// Do the transaction
		Sales_from_creators sale = Sales_from_creators.create("from_creators_id", creatorsId,
				"to_users_id", usersId,
				"time_", dateOfTransactionStr,
				"pieces", pieces,
				"price_per_piece", price_per_piece,
				"total", total);

		sale.saveIt();


		// User now owns pieces
		Pieces_owned pieces_owned = Pieces_owned.create("owners_id", usersId,
				"creators_id", creatorsId,
				"time_", dateOfTransactionStr,
				"pieces_owned", pieces);

		pieces_owned.saveIt();


		return sale;

	}

	public static Sales_from_users sellFromUser(String sellersId,
			String buyersId, String creatorsId, Integer pieces,
			Double price_per_piece) {

		String dateOfTransactionStr = SDF.format(new Date());


		// Make sure that the from user actually has those pieces, and subtract them from pieces owned
		Pieces_owned_total pieces_owned_total_obj = Pieces_owned_total.findFirst("owners_id = ? and creators_id = ?", sellersId, creatorsId);
		Integer pieces_owned_total = pieces_owned_total_obj.getInteger("pieces_owned_total");
		Double amount = price_per_piece*pieces;


		// Make sure the buyer has enough to cover the buy
		try {
			Double userFunds = Users_funds_current.findFirst("users_id = ?", buyersId).getDouble("current_funds");

			if (userFunds < amount) {
				throw new NoSuchElementException("The buyer has only " + userFunds + " $, but is trying to buy " +
						amount +  " worth of pieces");
			}
		} catch(NullPointerException e) {
			throw new NoSuchElementException("You have no funds");
		}


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


		Sales_from_users sale = Sales_from_users.create("from_users_id", sellersId,
				"to_users_id", buyersId,
				"creators_id", creatorsId,
				"time_", dateOfTransactionStr,
				"pieces", pieces,
				"price_per_piece", price_per_piece,
				"total", amount);

		sale.saveIt();


		return sale;
	}

	public static void askBidAccepter() {

		System.out.println("Starting ask bid acceptor ...");
		Boolean rerun = false;
		// Look at the view, and get the list of rows
		List<Ask_bid_accept_checker> rows = Ask_bid_accept_checker.findAll();

		// Iterate over each row
		for (Ask_bid_accept_checker cRow : rows) {

			// Partial fill options : either create/update the row and do the query again
			// If it does any updating, then exit the loop, and put a flag to rerun it again
			String askersId = cRow.getString("askers_id");
			String biddersId = cRow.getString("bidders_id");
			String creatorsId = cRow.getString("creators_id");

			Integer askPieces = cRow.getInteger("ask_pieces");
			Integer bidPieces = cRow.getInteger("bid_pieces");

			Integer askId = cRow.getInteger("ask_id");
			Integer bidId = cRow.getInteger("bid_id");

			Double askPerPiece = cRow.getDouble("ask_per_piece");
			Double bidPerPiece = cRow.getDouble("bid_per_piece");

			String askValidUntil = cRow.getString("ask_valid_until");
			String bidValidUntil = cRow.getString("bid_valid_until");

			// If the bidder wants more than the asker has:
			Integer askMinusBidPieces = askPieces - bidPieces;
			Integer piecesForTransaction = Math.min(askPieces, bidPieces);
			System.out.println("\ncreators id = " + creatorsId + " bidders id = " + biddersId + " askers id = " + askersId);
			System.out.println("ask minus bid pieces = " + askMinusBidPieces);
			System.out.println("pieces for transaction = " + piecesForTransaction);

			String dateOfTransaction = SDF.format(new Date());
			// Do the sale at the bidders price, or penalize them for overbidding
				
		
			
			// This method already makes sure the bidder has the money
			sellFromUser(askersId, biddersId, creatorsId, piecesForTransaction, bidPerPiece);


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
						"bid_per_piece", bidPerPiece);

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
							"ask_per_piece", askPerPiece);
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
			Tools.Sleep(1000L);
			askBidAccepter();
		} else {
			System.out.println("Finished.");
		}

	}

	public static UserTypeAndId createUserFromAjax(String reqBody) {


		// create user 
		Map<String, String> postMap = Tools.createMapFromAjaxPost(reqBody);

		// Create the user 
		try {
			User user = User.createIt(
					"username", postMap.get("username"),
					"password_encrypted", Tools.PASS_ENCRYPT.encryptPassword(postMap.get("password")),
					"email", postMap.get("email"));
			
			// Give them the padowan badge
			Badge padawanBadge = Badge.findFirst("name=?", "Padawan Learner");
			Users_badges.createIt("users_id", user.getId().toString(), "badges_id", padawanBadge.getId().toString());
			
			// Give them $100BTC in play money
			makeDepositFake(user.getId().toString(), 100d);
			

			UserTypeAndId uid = new UserTypeAndId(UserType.User, 
					String.valueOf(user.getId()),
					user.getString("username"));
			return uid;

		} catch (org.javalite.activejdbc.DBException e) {
			e.printStackTrace();
			return null;
		}


	}
	
	public static Users_deposits makeDepositFake(String usersId, Double btc_amount) {
		
		String timeStr = SDF.format(new Date());
		return Users_deposits.createIt("users_id", usersId,
				"cb_tid", "fake", 
				"time_", timeStr, 
				"btc_amount", btc_amount, 
				"status", "completed");
		
	}
	
	


	public static UserTypeAndId createCreatorFromAjax(String reqBody) {


		Map<String, String> postMap = Tools.createMapFromAjaxPost(reqBody);

		// Create the required fields 
		try {
			Creator creator = Creator.createIt(
					"username", postMap.get("username"),
					"password_encrypted", Tools.PASS_ENCRYPT.encryptPassword(postMap.get("password")),
					"email", postMap.get("email"));

			// TODO Create the static html5 page for that creator

			UserTypeAndId uid = new UserTypeAndId(UserType.Creator, 
					String.valueOf(creator.getId()), 
					creator.getString("username"));
			return uid;

		} catch (org.javalite.activejdbc.DBException e) {
			e.printStackTrace();
			return null;
		}


	}

	public static UserTypeAndId userLogin(String reqBody) {

		Map<String, String> postMap = Tools.createMapFromAjaxPost(reqBody);

		// fetch the required fields
		User user = User.findFirst("username = '" + postMap.get("username") + "'");
		if (user==null) {
			return null;
		}

		String encryptedPassword = user.getString("password_encrypted");

		Boolean correctPass = Tools.PASS_ENCRYPT.checkPassword(postMap.get("password"), encryptedPassword);

		UserTypeAndId returnVal = (correctPass == true) ? new UserTypeAndId(
				UserType.User, 
				user.getId().toString(),
				user.getString("username")) : null;

		return returnVal;

	}

	public static UserTypeAndId creatorLogin(String reqBody) {

		Map<String, String> postMap = Tools.createMapFromAjaxPost(reqBody);

		// fetch the required fields
		Creator user = Creator.findFirst("username = '" + postMap.get("username") + "'");
		if (user==null) {
			return null;
		}

		String encryptedPassword = user.getString("password_encrypted");

		Boolean correctPass = Tools.PASS_ENCRYPT.checkPassword(postMap.get("password"), encryptedPassword);

		UserTypeAndId returnVal = (correctPass == true) ? new UserTypeAndId(
				UserType.Creator, 
				user.getId().toString(),
				user.getString("username")) : null;

		return returnVal;



	}

	public static String getPiecesOwnedTotal(String userId) {
		LazyList<Pieces_owned_total> pieces_owned_total = Pieces_owned_total.where("owners_id = ?", userId);

		return pieces_owned_total.toJson(true, "creators_id", "pieces_owned_total");

		//		return GSON.toJson(pieces_owned_total.toMaps());


	}

	public static void userWithdrawal(String userId, Double amount) {

		// Make sure the user has enough to cover the withdraw
		try {
			Double userFunds = Users_funds_current.findFirst("users_id = ?", userId).getDouble("current_funds");

			if (userFunds < amount) {
				throw new NoSuchElementException("You have only " + userFunds + " $, but are trying to withdraw " +
						amount);
			}
		} catch(NullPointerException e) {
			throw new NoSuchElementException("You have no funds");
		}

		Users_withdrawals.createIt("users_id",userId,
				"cb_tid", "fake",
				"time_", SDF.format(new Date()),
				"btc_amount", amount, 
				"status", "completed");

	}

	public static void creatorWithdrawal(String creatorId, Double amount) {

		// Make sure the creator has enough to cover the withdraw
		try {
			Double creatorsFunds = Creators_funds_current.findFirst("creators_id = ?", creatorId).getDouble("current_funds");
			Double rewardPct = Rewards_current.findFirst("creators_id = ?", creatorId).getDouble("reward_pct")/100d;

			// This is based on the value of the current pieces
			Double creatorsValue = Pieces_owned_value_current_by_creator.findFirst("creators_id = ?", creatorId).
					getDouble("value_total");

			Double rewardsOwedForOneYear = creatorsValue*(Math.exp(rewardPct)-1.0d);

			Double availableFunds = creatorsFunds - rewardsOwedForOneYear;

			if (availableFunds < amount) {
				throw new NoSuchElementException("You have only " + availableFunds + " available, but are trying to withdraw " +
						amount +"\nNote: For users safety, a years worth of rewards can't be withdrawn, which is $" 
						+ rewardsOwedForOneYear);
			}

			Double fee = SERVICE_FEE_PCT * amount;
			
			Double amountAfterFee = amount - fee;
			
			Creators_withdrawals.createIt("creators_id",creatorId,
					"cb_tid", "fake",
					"time_", SDF.format(new Date()),
					"btc_amount_before_fee", amount, 
					"fee", fee,
					"btc_amount_after_fee", amountAfterFee,
					"status", "completed");

			
		} catch(NullPointerException e) {
			throw new NoSuchElementException("You have no funds");
		}


	}




}
