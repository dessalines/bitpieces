package com.heretic.bitpieces_practice.actions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.NoSuchElementException;

import com.heretic.bitpieces_practice.tables.Tables.Ask;
import com.heretic.bitpieces_practice.tables.Tables.Bid;
import com.heretic.bitpieces_practice.tables.Tables.Pieces_available;
import com.heretic.bitpieces_practice.tables.Tables.Pieces_owned_total;

public class Actions {
	public static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


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
		
			Ask ask = Ask.create("owners_id", userId, 
					"creators_id", creatorId,
					"time_", SDF.format(new Date()),
					"pieces", pieces,
					"ask", ask_amount);

			ask.saveIt();
		

		return ask;

	}





}
