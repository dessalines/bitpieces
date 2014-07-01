package com.heretic.bitpieces_practice;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.javalite.activejdbc.Base;

import com.heretic.bitpieces_practice.actions.Actions;
import com.heretic.bitpieces_practice.tables.Tables.Ask;
import com.heretic.bitpieces_practice.tables.Tables.Bid;
import com.heretic.bitpieces_practice.tables.Tables.Creator;
import com.heretic.bitpieces_practice.tables.Tables.Creators_btc_address;
import com.heretic.bitpieces_practice.tables.Tables.Creators_required_fields;
import com.heretic.bitpieces_practice.tables.Tables.Fees;
import com.heretic.bitpieces_practice.tables.Tables.Host_btc_addresses;
import com.heretic.bitpieces_practice.tables.Tables.Pieces_issued;
import com.heretic.bitpieces_practice.tables.Tables.Pieces_owned;
import com.heretic.bitpieces_practice.tables.Tables.Pieces_total;
import com.heretic.bitpieces_practice.tables.Tables.Sales_from_creators;
import com.heretic.bitpieces_practice.tables.Tables.Sales_from_users;
import com.heretic.bitpieces_practice.tables.Tables.User;
import com.heretic.bitpieces_practice.tables.Tables.Users_btc_address;
import com.heretic.bitpieces_practice.tables.Tables.Users_required_fields;
import com.heretic.bitpieces_practice.tools.Tools;


public class InitializeTables {
	public static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");



	public static void main(String[] args) {

		System.out.println( "Hello World!" );

		Properties prop = Tools.loadProperties("/home/tyler/db.properties");

		Base.open("com.mysql.jdbc.Driver", 
				prop.getProperty("dburl"), 
				prop.getProperty("dbuser"), 
				prop.getProperty("dbpassword"));

		delete_all();

		setup_users();

		setup_host_btc_address();

		setup_creators();

		issue_pieces();

		sell_from_creator();

		sell_from_user();

		create_bid();

		create_ask();

		password_checker();
		
		ask_bid_acceptor();


	}


	private static void ask_bid_acceptor() {
		Actions.askBidAccepter();
		
	}


	private static void password_checker() {
		Creators_required_fields leo = Creators_required_fields.findFirst("username like 'Leonardo%'");
		String encrypted_password = leo.getString("password_encrypted");

		String passwordChecked = (Tools.PASS_ENCRYPT.checkPassword("cat", encrypted_password)==true) ? "Success" : "Failure";

		System.out.println("password check is a " + passwordChecked);

	}


	private static void setup_host_btc_address() {
		Host_btc_addresses.createIt("btc_addr", "fake");
	}

	private static void sell_from_user() {

		// Bill is buying some from dick
		Users_required_fields bill = Users_required_fields.findFirst("username like 'Bill%'");
		Integer billUserId = bill.getInteger("users_id");
		Users_btc_address billBtcAddr = Users_btc_address.findFirst("users_id = ?", billUserId);

		Users_required_fields dick = Users_required_fields.findFirst("username like 'Dick%'");
		Integer dickUserId = dick.getInteger("users_id");
		Users_btc_address dickBtcAddr = Users_btc_address.findFirst("users_id = ?", dickUserId);

		Creators_required_fields leonardo = Creators_required_fields.findFirst("username like 'Leonardo%'");
		Integer leonardoUserId = leonardo.getInteger("creators_id");

		Actions.sellFromUser(dickBtcAddr, billBtcAddr, leonardoUserId, 5, 3d);


	}

	private static void sell_from_creator() {
		// Dick is buying from leonardo, the creator
		Users_required_fields dick = Users_required_fields.findFirst("username like 'Dick%'");
		Integer dickUserId = dick.getInteger("users_id");

		Users_btc_address userBtcAddr = Users_btc_address.findFirst("users_id = ?", dickUserId);

		Creators_required_fields leonardo = Creators_required_fields.findFirst("username like 'Leonardo%'");
		Integer leonardoUserId = leonardo.getInteger("creators_id");
		Creators_btc_address creatorBtcAddr = Creators_btc_address.findFirst("creators_id = ?", leonardoUserId);

		Actions.sellFromCreator(creatorBtcAddr, userBtcAddr, 165, 10d);



	}


	private static void create_ask() {



		// TODO don't use the Parent thing, just get the users_id
		Users_required_fields dick = Users_required_fields.findFirst("username like 'Dick%'");
		Integer dickUserId = dick.getInteger("users_id");

		Creators_required_fields leonardo = Creators_required_fields.findFirst("username like 'Leonardo%'");
		Integer leonardoCreatorId = leonardo.getInteger("creators_id");

		Actions.createAsk(dickUserId, leonardoCreatorId, 160, 100d,"2014-06-28", true);

	}

	private static void create_bid() {

		// TODO find a way to validate a bid

		// Find Bill
		Users_required_fields bill = Users_required_fields.findFirst("username like 'Bill%'");
		Integer billUserId = bill.getInteger("users_id");

		Creators_required_fields leonardo = Creators_required_fields.findFirst("username like 'Leonardo%'");
		Integer leonardoCreatorId = leonardo.getInteger("creators_id");

		Actions.createBid(billUserId, leonardoCreatorId, 5, 120d, "2014-06-28", true);
		
		// Find John, also wants to bid on it, at a higher bid, and more pieces
		Users_required_fields john = Users_required_fields.findFirst("username like 'John%'");
		Integer johnUserId = john.getInteger("users_id");
		
		Actions.createBid(johnUserId, leonardoCreatorId, 10, 130d, "2014-06-28", true);
		
		// Finally Terry, wants to bid the lowest, but more than the asker has to sell
		Users_required_fields terry = Users_required_fields.findFirst("username like 'Terry%'");
		Integer terryUserId = terry.getInteger("users_id");
		
		Actions.createBid(terryUserId, leonardoCreatorId, 30, 110d, "2014-06-28", true);
		
	}

	private static final void delete_all() {
		Fees.deleteAll();
		Host_btc_addresses.deleteAll();
		Sales_from_creators.deleteAll();
		Sales_from_users.deleteAll();
		Pieces_owned.deleteAll();
		Pieces_issued.deleteAll();
		Users_required_fields.deleteAll();
		Users_btc_address.deleteAll();
		Bid.deleteAll();
		Ask.deleteAll();
		User.deleteAll();
		Creators_required_fields.deleteAll();
		Creators_btc_address.deleteAll();
		Creator.deleteAll();

	}

	private static void issue_pieces() {
		// A creator issues some pieces
		// Find leonardo davinci
		//		Creators_required_fields leonardo = Creators_required_fields.findFirst("username = 'Leonardo'");
		Creators_required_fields leonardo = Creators_required_fields.findFirst("username like ?", "Leonardo%");

		Pieces_issued.createIt("creators_id",  leonardo.get("creators_id"), "time_", SDF.format(new Date()), "pieces_issued", 200);
		Pieces_issued.createIt("creators_id", leonardo.get("creators_id"), 
				"time_", SDF.format(new Date(new Date().getTime()+86400000)), 
				"pieces_issued", 300);

		Pieces_total piecesTotal = Pieces_total.findFirst("creators_id = ?", leonardo.get("creators_id"));
		System.out.println(piecesTotal);
	}

	private static void setup_creators() {

		Creator creator1 = new Creator();
		creator1.saveIt();

		Creator creator2= new Creator();
		creator2.saveIt();

		Creators_required_fields.createIt("creators_id", creator1.getId(), "username", "Leonardo_Davinci",
				"password_encrypted", Tools.PASS_ENCRYPT.encryptPassword("dog"),
				"email", "asdf@gmail.com");
		Creators_required_fields.createIt("creators_id", creator2.getId(), "username", "Dusty_Springfield",
				"password_encrypted", Tools.PASS_ENCRYPT.encryptPassword("dog"),
				"email", "asdf@gmail.com");

		Creators_btc_address.createIt("creators_id", creator1.getId(), "btc_addr", "fake");
		Creators_btc_address.createIt("creators_id", creator2.getId(), "btc_addr", "fake");



	}

	private static void setup_users() {

		List<Object> userIds = new ArrayList<Object>();
		for (int i = 0; i < 4; i++) {
			User user1 = new User();
			user1.saveIt();
			
			userIds.add(user1.getId());
		}

		Iterator<Object> it = userIds.iterator();
		for (String name : Arrays.asList("Bill_Jeffries", "Dick_Tatum", "John_Himperdinkle", "Terry_Westworth")) {
			Object cUserId = it.next();
			Users_required_fields.createIt("users_id", cUserId, "username", name, 
					"password_encrypted", Tools.PASS_ENCRYPT.encryptPassword("dog"),
					"email", "asdf@gmail.com");
			Users_btc_address.createIt("users_id", cUserId, "btc_addr", "fake");
		}



	}

}
