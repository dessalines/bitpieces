package com.heretic.bitpieces_practice.actions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.NoSuchElementException;

import com.heretic.bitpieces_practice.tables.Tables.Ask;
import com.heretic.bitpieces_practice.tables.Tables.Bid;
import com.heretic.bitpieces_practice.tables.Tables.Creators_btc_address;
import com.heretic.bitpieces_practice.tables.Tables.Fees;
import com.heretic.bitpieces_practice.tables.Tables.Host_btc_addresses;
import com.heretic.bitpieces_practice.tables.Tables.Pieces_available;
import com.heretic.bitpieces_practice.tables.Tables.Pieces_owned;
import com.heretic.bitpieces_practice.tables.Tables.Pieces_owned_total;
import com.heretic.bitpieces_practice.tables.Tables.Sales_from_creators;
import com.heretic.bitpieces_practice.tables.Tables.Sales_from_users;
import com.heretic.bitpieces_practice.tables.Tables.Users_btc_address;

public class Actions {
	public static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final Double SERVICE_FEE_PCT = .05d;


	public static Bid createBid(Object userId, Object creatorId, Integer pieces, Double bid_amount) {

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
				"pieces", pieces,
				"bid", bid_amount);

		bid.saveIt();

		return bid;

	}

	public static Ask createAsk(Object userId, Object creatorId, Integer pieces, Double ask_amount) {

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
				"host_btc_addr_id", Host_btc_addresses.findById(1).getId(),
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





}
