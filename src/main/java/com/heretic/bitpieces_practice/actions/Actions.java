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

	public static String saveCreatorPage(String id, String reqBody) {
		
		Map<String, String> postMap = Tools.createMapFromAjaxPost(reqBody);
		
		Creators_page_fields page = Creators_page_fields.findFirst("creators_id = ?",  id);
		
		// The first time filling the page fields
		if (page == null) {
		page = Creators_page_fields.createIt("creators_id", id,
				"main_body", postMap.get("main_body"));
		} else {
			page.set("main_body", postMap.get("main_body")).saveIt();
		}
		
		// Save the html page
		saveCreatorHTMLPage(id, page);
		
		return "Successful";
		
	}

	public static void saveCreatorHTMLPage(String id, Creators_page_fields page) {
		
		String mainBody = page.getString("main_body");
		
		String path = Tools.ROOT_DIR + "resources/web/creators_pages/" + id + ".html";
		
		String html = "&lt;!DOCTYPE html&gt;\n"+
				"&lt;html lang=&quot;en&quot;&gt;\n"+
				"&lt;head&gt;\n"+
				" &lt;meta charset=&quot;utf-8&quot;&gt;\n"+
				" &lt;meta http-equiv=&quot;X-UA-Compatible&quot; content=&quot;IE=edge&quot;&gt;\n"+
				" &lt;meta name=&quot;viewport&quot; content=&quot;width=device-width, initial-scale=1&quot;&gt;\n"+
				" &lt;meta name=&quot;description&quot; content=&quot;&quot;&gt;\n"+
				" &lt;meta name=&quot;author&quot; content=&quot;&quot;&gt;\n"+
				" &lt;link rel=&quot;icon&quot; href=&quot;../../favicon.ico&quot;&gt;\n"+
				"\n"+
				" &lt;title&gt;Starter Template for Bootstrap&lt;/title&gt;\n"+
				"\n"+
				" &lt;!-- Bootstrap core CSS --&gt;\n"+
				" &lt;link href=&quot;../darkly.bootstrap.min.css&quot; rel=&quot;stylesheet&quot;&gt;\n"+
				"\n"+
				" &lt;!-- Link to font awesome --&gt;\n"+
				" &lt;link rel=&quot;stylesheet&quot; href=&quot;../font-awesome/css/font-awesome.min.css&quot;&gt;\n"+
				"\n"+
				" &lt;!-- Bootstrap social css --&gt;\n"+
				" &lt;link href=&quot;../bootstrap-social-gh-pages/bootstrap-social.css&quot; rel=&quot;stylesheet&quot;&gt;\n"+
				"\n"+
				" &lt;!-- Bootstrap validator --&gt;\n"+
				" &lt;link rel=&quot;stylesheet&quot; href=&quot;../bootstrap-validator/dist/css/bootstrapValidator.min.css&quot;/&gt;\n"+
				"\n"+
				" &lt;!-- toastr css --&gt;\n"+
				" &lt;link href=&quot;../toastr/toastr.css&quot; rel=&quot;stylesheet&quot;/&gt;\n"+
				"\n"+
				"\t<!-- Pickadate -->\n"+
				"\t<link href=\"../pickadate/lib/themes/default.css\" rel=\"stylesheet\"/>\n"+
				"\t<link href=\"../pickadate/lib/themes/default.date.css\" rel=\"stylesheet\"/>"+
				"\n"+
				" &lt;!-- This main css --&gt;\n"+
				" &lt;link href=&quot;../creators.css&quot; rel=&quot;stylesheet&quot;&gt;\n"+
				"\n"+
				" &lt;!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries --&gt;\n"+
				" &lt;!--[if lt IE 9]&gt;\n"+
				" &lt;script src=&quot;https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js&quot;&gt;&lt;/script&gt;\n"+
				" &lt;script src=&quot;https://oss.maxcdn.com/respond/1.4.2/respond.min.js&quot;&gt;&lt;/script&gt;\n"+
				" &lt;![endif]--&gt;\n"+
				" &lt;/head&gt;\n"+
				"\n"+
				" &lt;body&gt;\n"+
				"\n"+
				" &lt;!-- NAVBAR\n"+
				" ================================================== --&gt;\n"+
				" &lt;div class=&quot;navbar-wrapper&quot;&gt;\n"+
				" &lt;div class=&quot;container&quot;&gt;\n"+
				"\n"+
				" &lt;div class=&quot;navbar navbar-default navbar-fixed-top&quot; role=&quot;navigation&quot;&gt;\n"+
				" &lt;div class=&quot;container&quot;&gt;\n"+
				" &lt;div class=&quot;navbar-header&quot;&gt;\n"+
				" &lt;button type=&quot;button&quot; class=&quot;navbar-toggle&quot; data-toggle=&quot;collapse&quot; data-target=&quot;.navbar-collapse&quot;&gt;\n"+
				" &lt;span class=&quot;sr-only&quot;&gt;Toggle navigation&lt;/span&gt;\n"+
				" &lt;span class=&quot;icon-bar&quot;&gt;&lt;/span&gt;\n"+
				" &lt;span class=&quot;icon-bar&quot;&gt;&lt;/span&gt;\n"+
				" &lt;span class=&quot;icon-bar&quot;&gt;&lt;/span&gt;\n"+
				" &lt;/button&gt;\n"+
				" &lt;a class=&quot;navbar-brand&quot; href=&quot;#&quot;&gt;BitPieces&lt;/a&gt;\n"+
				" &lt;/div&gt;\n"+
				" &lt;div class=&quot;navbar-collapse collapse&quot;&gt;\n"+
				" &lt;ul class=&quot;nav navbar-nav&quot;&gt;\n"+
				" &lt;li&gt;&lt;a href=&quot;/carousel&quot;&gt;Home&lt;/a&gt;&lt;/li&gt;\n"+
				" &lt;li&gt;&lt;a href=&quot;#discover&quot;&gt;Discover&lt;/a&gt;&lt;/li&gt;\n"+
				" &lt;li class=&quot;active&quot;&gt;&lt;a href=&quot;/creators&quot;&gt;Creators&lt;/a&gt;&lt;/li&gt;\n"+
				" &lt;li&gt;&lt;a href=&quot;/userdashboard&quot; id=&quot;dashboardhref&quot; class=&quot;hide&quot;&gt;Dashboard&lt;/a&gt;&lt;/li&gt;\n"+
				" &lt;li&gt;&lt;a href=&quot;#login&quot; id=&quot;loginhref&quot; data-toggle=&quot;modal&quot; data-target=&quot;#userloginModal&quot;&gt;Login/Register&lt;/a&gt;&lt;/li&gt;\n"+
				" &lt;li&gt;&lt;a href=&quot;#logout&quot; id=&quot;logouthref&quot; class=&quot;hide&quot;&gt;Log Out&lt;/a&gt;&lt;/li&gt;\n"+
				" &lt;li class=&quot;dropdown&quot;&gt;\n"+
				" &lt;a href=&quot;#&quot; class=&quot;dropdown-toggle&quot; data-toggle=&quot;dropdown&quot;&gt;Dropdown &lt;b class=&quot;caret&quot;&gt;&lt;/b&gt;&lt;/a&gt;\n"+
				" &lt;ul class=&quot;dropdown-menu&quot;&gt;\n"+
				" &lt;li&gt;&lt;a href=&quot;#&quot;&gt;Action&lt;/a&gt;&lt;/li&gt;\n"+
				" &lt;li&gt;&lt;a href=&quot;#&quot;&gt;Another action&lt;/a&gt;&lt;/li&gt;\n"+
				" &lt;li&gt;&lt;a href=&quot;#&quot;&gt;Something else here&lt;/a&gt;&lt;/li&gt;\n"+
				" &lt;li class=&quot;divider&quot;&gt;&lt;/li&gt;\n"+
				" &lt;li class=&quot;dropdown-header&quot;&gt;Nav header&lt;/li&gt;\n"+
				" &lt;li&gt;&lt;a href=&quot;#&quot;&gt;Separated link&lt;/a&gt;&lt;/li&gt;\n"+
				" &lt;li&gt;&lt;a href=&quot;#&quot;&gt;One more separated link&lt;/a&gt;&lt;/li&gt;\n"+
				" &lt;/ul&gt;\n"+
				" &lt;/li&gt;\n"+
				" &lt;/ul&gt;\n"+
				" &lt;form class=&quot;navbar-form navbar-right&quot;&gt;\n"+
				" &lt;input type=&quot;text&quot; class=&quot;form-control&quot; placeholder=&quot;Search...&quot;&gt;\n"+
				" &lt;/form&gt;\n"+
				" &lt;/div&gt;\n"+
				" &lt;/div&gt;\n"+
				" &lt;/div&gt;\n"+
				"\n"+
				" &lt;/div&gt;\n"+
				" &lt;/div&gt;\n"+
				"\n"+
				"\n"+
				// Here's the container
				" &lt;div class=&quot;container&quot;&gt;\n"+
				"\n"+
				" &lt;div class=&quot;starter-template&quot;&gt;\n"+
				" &lt;h1&gt;Creator # " + id + " Page &lt;/h1&gt;\n"+
				" &lt;p class=&quot;lead&quot;&gt;" + mainBody + "&lt;/p&gt;\n"+
				" &lt;/div&gt;\n"+
	 	
	 			"&lt;div class=&quot;row&quot;&gt;\n"+
	 			"	&lt;div class=&quot;col-md-12&quot;&gt;\n"+
	 			"<button id=\"bidBtn\" type=\"button\" class=\"btn btn-primary\" data-toggle=\"modal\" data-target=\"#bidModal\">Bid</button>\n"+
	 			"<button id=\"askBtn\" type=\"button\" class=\"btn btn-primary\" data-toggle=\"modal\" data-target=\"#askModal\">Ask</button>\n"+
	 				"&lt;/div&gt;\n"+
	 			"&lt;/div&gt;\n" +
	 		"&lt;/div&gt;\n" + 
				" &lt;/div&gt;&lt;!-- /.container --&gt;\n"+
				
				// End of the container
				
				// Start of modal
				"<!-- Modals -->\n"+
				" \t<div id=\"bidModal\" class=\"modal fade bs-example-modal-sm\" tabindex=\"-1\" role=\"dialog\" aria-labelledby=\"mySmallModalLabel\" aria-hidden=\"true\">\n"+
				" \t\t<div class=\"modal-dialog modal-sm\">\n"+
				"\n"+
				" \t\t\t<div class=\"modal-content\">\n"+
				" \t\t\t\t<div class=\"modal-header\">\n"+
				" \t\t\t\t\t<h4 class=\"modal-title\">Bid</h4>\n"+
				" \t\t\t\t</div>\n"+
				" \t\t\t\t<div class=\"modal-body\">\n"+
				" \t\t\t\t\t<form id=\"bidForm\" class=\"form-horizontal\" role=\"form\">\n"+
				" \t\t\t\t\t\t<div class=\"form-group form-group-lg\">\n"+
				" \t\t\t\t\t\t\t<label class=\"col-sm-2 control-label\" for=\"formGroupInputLarge\">Price</label>\n"+
				" \t\t\t\t\t\t\t<div class=\"col-sm-10\">\n"+
				" \t\t\t\t\t\t\t\t<input name=\"bid\" class=\"form-control\" type=\"text\" id=\"formGroupInputLarge\" placeholder=\"Last price\"\n"+
				" \t\t\t\t\t\t\t\tdata-bv-greaterthan=\"true\"\n"+
				" \t\t\t\t\t\t\t\tdata-bv-greaterthan-value=\".01\"\n"+
				" \t\t\t\t\t\t\t\tdata-bv-greaterthan-message=\"Must be > .01\"\n"+
				"\n"+
				" \t\t\t\t\t\t\t\t>\n"+
				" \t\t\t\t\t\t\t</div>\n"+
				" \t\t\t\t\t\t</div>\n"+
				"\n"+
				" \t\t\t\t\t\t<div class=\"form-group form-group-lg\">\n"+
				" \t\t\t\t\t\t\t<label class=\"col-sm-2 control-label\" for=\"formGroupInputLarge\">Pieces</label>\n"+
				" \t\t\t\t\t\t\t<div class=\"col-sm-10\">\n"+
				" \t\t\t\t\t\t\t\t<input name=\"pieces\" class=\"form-control\" type=\"text\" id=\"formGroupInputLarge\" placeholder=\"Pieces\"\n"+
				" \t\t\t\t\t\t\t\ttype=\"text\" \n"+
				"\t\t\t\t\t\t\t\tdata-bv-integer=\"true\"\n"+
				" \t\t\t\t\t\t\t\tdata-bv-integer-message=\"Must be a whole number\"\n"+
				"\n"+
				" \t\t\t\t\t\t\t\t>\n"+
				" \t\t\t\t\t\t\t</div>\n"+
				" \t\t\t\t\t\t</div>\n"+
				"\n"+
				" \t\t\t\t\t\t<div class=\"form-group form-group-lg\">\n"+
				" \t\t\t\t\t\t\t<label class=\"col-sm-2 control-label\" for=\"formGroupInputLarge\">Date</label>\n"+
				" \t\t\t\t\t\t\t<div class=\"col-sm-10\">\n"+
				" \t\t\t\t\t\t\t\t<input name=\"validUntil\" class=\"form-control datepicker\" type=\"text\" id=\"formGroupInputLarge\" placeholder=\"Valid Until...\">\n"+
				" \t\t\t\t\t\t\t</div>\n"+
				" \t\t\t\t\t\t</div>\n"+
				"\n"+
				" \t\t\t\t\t\t<button id=\"placebidBtn\" type=\"submit\" class=\"btn btn-primary\">Place Bid</button>\n"+
				" \t\t\t\t\t</form>\n"+
				"\n"+
				" \t\t\t\t</div>\n"+
				" \t\t\t\t\n"+
				" \t\t\t</div>\n"+
				" \t\t</div>\n"+
				" \t</div>" + 
				"\n"+
				" \t<div id=\"askModal\" class=\"modal fade bs-example-modal-sm\" tabindex=\"-1\" role=\"dialog\" aria-labelledby=\"mySmallModalLabel\" aria-hidden=\"true\">\n"+
				" \t\t<div class=\"modal-dialog modal-sm\">\n"+
				"\n"+
				" \t\t\t<div class=\"modal-content\">\n"+
				" \t\t\t\t<div class=\"modal-header\">\n"+
				" \t\t\t\t\t<h4 class=\"modal-title\">Ask</h4>\n"+
				" \t\t\t\t</div>\n"+
				" \t\t\t\t<div class=\"modal-body\">\n"+
				" \t\t\t\t\t<form id=\"askForm\" class=\"form-horizontal\" role=\"form\">\n"+
				" \t\t\t\t\t\t<div class=\"form-group form-group-lg\">\n"+
				" \t\t\t\t\t\t\t<label class=\"col-sm-2 control-label\" for=\"formGroupInputLarge\">Price</label>\n"+
				" \t\t\t\t\t\t\t<div class=\"col-sm-10\">\n"+
				" \t\t\t\t\t\t\t\t<input name=\"ask\" class=\"form-control\" type=\"text\" id=\"formGroupInputLarge\" placeholder=\"Last price\"\n"+
				" \t\t\t\t\t\t\t\tdata-bv-greaterthan=\"true\"\n"+
				" \t\t\t\t\t\t\t\tdata-bv-greaterthan-value=\".01\"\n"+
				" \t\t\t\t\t\t\t\tdata-bv-greaterthan-message=\"Must be > .01\"\n"+
				"\n"+
				" \t\t\t\t\t\t\t\t>\n"+
				" \t\t\t\t\t\t\t</div>\n"+
				" \t\t\t\t\t\t</div>\n"+
				"\n"+
				" \t\t\t\t\t\t<div class=\"form-group form-group-lg\">\n"+
				" \t\t\t\t\t\t\t<label class=\"col-sm-2 control-label\" for=\"formGroupInputLarge\">Pieces</label>\n"+
				" \t\t\t\t\t\t\t<div class=\"col-sm-10\">\n"+
				" \t\t\t\t\t\t\t\t<input name=\"pieces\" class=\"form-control\" type=\"text\" id=\"formGroupInputLarge\" placeholder=\"Pieces\"\n"+
				" \t\t\t\t\t\t\t\ttype=\"text\" \n"+
				"\t\t\t\t\t\t\t\tdata-bv-integer=\"true\"\n"+
				" \t\t\t\t\t\t\t\tdata-bv-integer-message=\"Must be a whole number\"\n"+
				"\n"+
				" \t\t\t\t\t\t\t\t>\n"+
				" \t\t\t\t\t\t\t</div>\n"+
				" \t\t\t\t\t\t</div>\n"+
				"\n"+
				" \t\t\t\t\t\t<div class=\"form-group form-group-lg\">\n"+
				" \t\t\t\t\t\t\t<label class=\"col-sm-2 control-label\" for=\"formGroupInputLarge\">Date</label>\n"+
				" \t\t\t\t\t\t\t<div class=\"col-sm-10\">\n"+
				" \t\t\t\t\t\t\t\t<input name=\"validUntil\" class=\"form-control datepicker\" type=\"text\" id=\"formGroupInputLarge\" placeholder=\"Valid Until...\">\n"+
				" \t\t\t\t\t\t\t</div>\n"+
				" \t\t\t\t\t\t</div>\n"+
				"\n"+
				" \t\t\t\t\t\t<button id=\"placeaskBtn\" type=\"submit\" class=\"btn btn-primary\">Place Ask</button>\n"+
				" \t\t\t\t\t</form>\n"+
				"\n"+
				" \t\t\t\t</div>\n"+
				" \t\t\t\t\n"+
				" \t\t\t</div>\n"+
				" \t\t</div>\n"+
				" \t</div>\n"+
				
				
				"\n"+
				"\n"+
				" &lt;!-- Bootstrap core JavaScript\n"+
				" ================================================== --&gt;\n"+
				" &lt;!-- Placed at the end of the document so the pages load faster --&gt;\n"+
				" &lt;script src=&quot;https://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js&quot;&gt;&lt;/script&gt;\n"+
				" &lt;script src=&quot;../bootstrap-dist/js/bootstrap.min.js&quot;&gt;&lt;/script&gt;\n"+
				" &lt;script src=&quot;../../assets/js/docs.min.js&quot;&gt;&lt;/script&gt;\n"+
				" &lt;script type=&quot;text/javascript&quot; src=&quot;../bootstrap-validator/dist/js/bootstrapValidator.js&quot;&gt;&lt;/script&gt;\n"+
				" &lt;script src=&quot;../toastr/toastr.js&quot;&gt;&lt;/script&gt;\n"+
				" &lt;script src=&quot;../mustache/mustache.js&quot;&gt;&lt;/script&gt;\n"+
				" &lt;script src=&quot;../holder/holder.js&quot;&gt;&lt;/script&gt;\n"+
				" <script src=\"../pickadate/lib/picker.js\"></script>\n"+
				" <script src=\"../pickadate/lib/picker.date.js\"></script>\n"+
				"\n"+
				"\n"+
				" &lt;!-- my scripts --&gt;\n"+
				" &lt;script src=&quot;../tools.js&quot;&gt;&lt;/script&gt;\n"+
				" &lt;script src=&quot;../login.js&quot;&gt;&lt;/script&gt;\n"+
				" &lt;script src=&quot;../creators.js&quot;&gt;&lt;/script&gt;\n"+
				" &lt;script src=&quot;creatorpage.js&quot;&gt;&lt;/script&gt;\n"+
				" &lt;/body&gt;\n"+
				" &lt;/html&gt;\n"+
				"";
		
			Tools.writeFile(path, StringEscapeUtils.unescapeHtml4(html));
	}

	public static String placeBid(String userId, String body) {
		Map<String, String> postMap = Tools.createMapFromAjaxPost(body);
		
		createBid(userId, 
				postMap.get("creatorid"), 
				Integer.valueOf(postMap.get("pieces")), 
				Double.valueOf(postMap.get("bid")), 
				postMap.get("validUntil"), 
				true);
				
		
		return body;
	}
	
	public static String placeAsk(String userId, String body) {
		Map<String, String> postMap = Tools.createMapFromAjaxPost(body);
		
		createAsk(userId, 
				postMap.get("creatorid"), 
				Integer.valueOf(postMap.get("pieces")), 
				Double.valueOf(postMap.get("ask")), 
				postMap.get("validUntil"), 
				true);
				
		
		return body;
	}





}
