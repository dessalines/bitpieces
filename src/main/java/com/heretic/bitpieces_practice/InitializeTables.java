package com.heretic.bitpieces_practice;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

import org.javalite.activejdbc.Base;

import com.heretic.bitpieces_practice.actions.Actions;
import com.heretic.bitpieces_practice.tables.Tables.Ask;
import com.heretic.bitpieces_practice.tables.Tables.Bid;
import com.heretic.bitpieces_practice.tables.Tables.Creator;
import com.heretic.bitpieces_practice.tables.Tables.Creators_btc_address;
import com.heretic.bitpieces_practice.tables.Tables.Creators_page_fields;
import com.heretic.bitpieces_practice.tables.Tables.Fees;
import com.heretic.bitpieces_practice.tables.Tables.Host_btc_addresses;
import com.heretic.bitpieces_practice.tables.Tables.Pieces_issued;
import com.heretic.bitpieces_practice.tables.Tables.Pieces_owned;
import com.heretic.bitpieces_practice.tables.Tables.Pieces_total;
import com.heretic.bitpieces_practice.tables.Tables.Reward;
import com.heretic.bitpieces_practice.tables.Tables.Sales_from_creators;
import com.heretic.bitpieces_practice.tables.Tables.Sales_from_users;
import com.heretic.bitpieces_practice.tables.Tables.User;
import com.heretic.bitpieces_practice.tables.Tables.Users_btc_address;
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
		
		withdraw_rewards();
		
		


	}


	private static void withdraw_rewards() {
		
		
		
	}


	private static void ask_bid_acceptor() {
		Actions.askBidAccepter();
		
	}


	private static void password_checker() {
		Creator leo = Creator.findFirst("username like 'Leonardo%'");
		String encrypted_password = leo.getString("password_encrypted");

		String passwordChecked = (Tools.PASS_ENCRYPT.checkPassword("dog", encrypted_password)==true) ? "Success" : "Failure";

		System.out.println("password check is a " + passwordChecked);

	}


	private static void setup_host_btc_address() {
		Host_btc_addresses.createIt("btc_addr", "fake");
	}

	private static void sell_from_user() {

		// Bill is buying some from dick
		User bill = User.findFirst("username like 'Bill%'");
		String billUserId = bill.getId().toString();
		Users_btc_address billBtcAddr = Users_btc_address.findFirst("users_id = ?", billUserId);

		User dick = User.findFirst("username like 'Dick%'");
		String dickUserId = dick.getId().toString();
		Users_btc_address dickBtcAddr = Users_btc_address.findFirst("users_id = ?", dickUserId);

		Creator leonardo = Creator.findFirst("username like 'Leonardo%'");
		String leonardoUserId = leonardo.getId().toString();

		Actions.sellFromUser(dickBtcAddr, billBtcAddr, Integer.valueOf(leonardoUserId), 5, 6d);

		Tools.Sleep(1000L);

	}

	private static void sell_from_creator() {
		// Dick is buying from leonardo, the creator
		User dick = User.findFirst("username like 'Dick%'");
		String dickUserId = dick.getId().toString();

		Users_btc_address userBtcAddr = Users_btc_address.findFirst("users_id = ?", dickUserId);

		Creator leonardo = Creator.findFirst("username like 'Leonardo%'");
		String leonardoUserId = leonardo.getId().toString();
		Creators_btc_address creatorBtcAddr = Creators_btc_address.findFirst("creators_id = ?", leonardoUserId);

		Actions.sellFromCreator(creatorBtcAddr, userBtcAddr, 100, 100d);

		Tools.Sleep(1000L);

	}


	private static void create_ask() {



		// TODO don't use the Parent thing, just get the users_id
		User dick = User.findFirst("username like 'Dick%'");
		String dickUserId = dick.getId().toString();

		Creator leonardo = Creator.findFirst("username like 'Leonardo%'");
		String leonardoCreatorId = leonardo.getId().toString();

		Actions.createAsk(dickUserId, leonardoCreatorId, 75, 1d,"2014-12-12", true);
		Tools.Sleep(1000L);

	}

	private static void create_bid() {

		// TODO find a way to validate a bid

		// Find Bill
		User bill = User.findFirst("username like 'Bill%'");
		String billUserId = bill.getId().toString();

		Creator leonardo = Creator.findFirst("username like 'Leonardo%'");
		String leonardoCreatorId = leonardo.getId().toString();

		Actions.createBid(billUserId, leonardoCreatorId, 5, 1.2263d, "2014-12-12", true);
		Tools.Sleep(1000L);
		
		// Find John, also wants to bid on it, at a higher bid, and more pieces
		User john = User.findFirst("username like 'John%'");
		String johnUserId = john.getId().toString();
		
		Actions.createBid(johnUserId, leonardoCreatorId, 10, 1.5d, "2014-12-12", true);
		Tools.Sleep(1000L);
		
		// Finally Terry, wants to bid the lowest, but more than the asker has to sell
		User terry = User.findFirst("username like 'Terry%'");
		String terryUserId = terry.getId().toString();
		
		Actions.createBid(terryUserId, leonardoCreatorId, 30, 3d, "2014-12-12", true);
		Tools.Sleep(1000L);
		
	}

	private static final void delete_all() {
		Fees.deleteAll();
		Host_btc_addresses.deleteAll();
		Sales_from_creators.deleteAll();
		Sales_from_users.deleteAll();
		Pieces_owned.deleteAll();
		Pieces_issued.deleteAll();
		Users_btc_address.deleteAll();
		Creators_btc_address.deleteAll();
		Creators_page_fields.deleteAll();
		Bid.deleteAll();
		Ask.deleteAll();
		Reward.deleteAll();
		User.deleteAll();
		Creator.deleteAll();
		
		

	}

	private static void issue_pieces() {
		// A creator issues some pieces
		// Find leonardo davinci
		//		Creators_required_fields leonardo = Creators_required_fields.findFirst("username = 'Leonardo'");
		Creator leonardo = Creator.findFirst("username like ?", "Leonardo%");

		Pieces_issued.createIt("creators_id",  leonardo.getId().toString(), "time_", SDF.format(new Date()), "pieces_issued", 200);
		Pieces_issued.createIt("creators_id", leonardo.getId().toString(), 
				"time_", SDF.format(new Date(new Date().getTime()+86400000)), 
				"pieces_issued", 300);

		Pieces_total piecesTotal = Pieces_total.findFirst("creators_id = ?", leonardo.getId().toString());
		System.out.println(piecesTotal);
	}

	private static void setup_creators() {

		String now = SDF.format(new Date());
		Creator creator1 = Creator.createIt("username", "Leonardo_Davinci",
				"password_encrypted", Tools.PASS_ENCRYPT.encryptPassword("dog"),
				"email", "asdf@gmail.com");
		Creator creator2 = Creator.createIt("username", "Dusty_Springfield",
				"password_encrypted", Tools.PASS_ENCRYPT.encryptPassword("dog"),
				"email", "asdf@gmail.com");

		Creators_btc_address.createIt("creators_id", creator1.getId(), "btc_addr", "fake");
		Creators_btc_address.createIt("creators_id", creator2.getId(), "btc_addr", "fake");

		Reward.createIt("creators_id", creator1.getId(),
				"time_", now,
				"reward_pct", 1.0d);
		
		Reward.createIt("creators_id", creator2.getId(),
				"time_", now,
				"reward_pct", 5.0d);
				

	}

	private static void setup_users() {


		for (String name : Arrays.asList("Bill_Jeffries", "Dick_Tatum", "John_Himperdinkle", "Terry_Westworth")) {
			User cUser = User.createIt("username", name, 
					"password_encrypted", Tools.PASS_ENCRYPT.encryptPassword("dog"),
					"email", "asdf@gmail.com");
			Users_btc_address.createIt("users_id", cUser.getId().toString(), "btc_addr", "fake");
		}

	}

}
