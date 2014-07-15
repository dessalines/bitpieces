package com.heretic.bitpieces_practice.tables;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.javalite.activejdbc.Base;

import com.heretic.bitpieces_practice.actions.Actions;
import com.heretic.bitpieces_practice.tables.Tables.Ask;
import com.heretic.bitpieces_practice.tables.Tables.Badge;
import com.heretic.bitpieces_practice.tables.Tables.Bid;
import com.heretic.bitpieces_practice.tables.Tables.Creator;
import com.heretic.bitpieces_practice.tables.Tables.Creators_btc_address;
import com.heretic.bitpieces_practice.tables.Tables.Creators_page_fields;
import com.heretic.bitpieces_practice.tables.Tables.Creators_withdrawals;
import com.heretic.bitpieces_practice.tables.Tables.Host_btc_addresses;
import com.heretic.bitpieces_practice.tables.Tables.Pieces_issued;
import com.heretic.bitpieces_practice.tables.Tables.Pieces_owned;
import com.heretic.bitpieces_practice.tables.Tables.Reward;
import com.heretic.bitpieces_practice.tables.Tables.Sales_from_creators;
import com.heretic.bitpieces_practice.tables.Tables.Sales_from_users;
import com.heretic.bitpieces_practice.tables.Tables.User;
import com.heretic.bitpieces_practice.tables.Tables.Users_badges;
import com.heretic.bitpieces_practice.tables.Tables.Users_btc_address;
import com.heretic.bitpieces_practice.tables.Tables.Users_deposits;
import com.heretic.bitpieces_practice.tables.Tables.Users_withdrawals;
import com.heretic.bitpieces_practice.tools.Tools;

/**
 * TODO
 * 1) Do a deposit / withdrawal check
 * 2) Before buying anything, do a users funds check
 * 3) Before buying creators pieces, make sure users have deposited
 * 4) implement recaptcha
 * 5) Start using jsoup to write creators and users pages
 * @author tyler
 *
 */
public class InitializeTables {
	public static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");



	public static void main(String[] args) {

		Properties prop = Tools.loadProperties("/home/tyler/db.properties");

		Base.open("com.mysql.jdbc.Driver", 
				prop.getProperty("dburl"), 
				prop.getProperty("dbuser"), 
				prop.getProperty("dbpassword"));

		System.out.println("Initializing tables...");
		delete_all();

		setup_users();
		
		setup_badges();

		setup_host_btc_address();

		setup_creators();

		issue_pieces();
		
		user_deposit();

		sell_from_creator();

		sell_from_user();

		create_bid();

		create_ask();
		
		issue_new_reward();

		password_checker();
		
		ask_bid_acceptor();
		
		user_withdrawal();
		
		creator_withdrawal();
		
		


	}


	private static void setup_badges() {
		

		

			Badge.createIt("name", "Padawan Learner", "description", "Created an account");
		
		
		Badge padawanBadge = Badge.findFirst("name=?", "Padawan Learner");
		
		User bill = User.findFirst("username like 'Bill%'");
		
		// Give bill a padawan badge for registering
		
		Users_badges.createIt("users_id", bill.getId().toString(), "badges_id", padawanBadge.getId().toString());
		
		
		
		
		
		
	}


	private static void issue_new_reward() {
		String now = SDF.format(new Date());
		
		Creator leo = Creator.findFirst("username like 'Leonardo%'");
		Reward.createIt("creators_id", leo.getId(),
				"time_", now,
				"reward_pct", 1.4d);
		
	}


	private static void creator_withdrawal() {
		Creator leo = Creator.findFirst("username like 'Leonardo%'");
		
		Actions.creatorWithdrawal(leo.getId().toString(), 80d);
	
		
	}


	private static void user_deposit() {
		
		User dick = User.findFirst("username like 'Dick%'");
		User john = User.findFirst("username like 'John%'");
		User bill = User.findFirst("username like 'Bill%'");
		User terry = User.findFirst("username like 'Terry%'");
		
		
		Users_deposits dickDep = Users_deposits.createIt("users_id", dick.getId().toString(),
				"cb_tid", "fake",
				"time_", SDF.format(new Date()),
				"btc_amount", 150d, 
				"status", "completed");
		
		// an exact amount, or a straight up pieces buy
		Users_deposits johnDep = Users_deposits.createIt("users_id", john.getId().toString(),
				"cb_tid", "fake",
				"time_", SDF.format(new Date()),
				"btc_amount", 170d, 
				"status", "completed");
		
		Users_deposits billDep = Users_deposits.createIt("users_id", bill.getId().toString(),
				"cb_tid", "fake",
				"time_", SDF.format(new Date()),
				"btc_amount", 15d, 
				"status", "completed");
		
		Users_deposits terryDep = Users_deposits.createIt("users_id", terry.getId().toString(),
				"cb_tid", "fake",
				"time_", SDF.format(new Date()),
				"btc_amount", 300d, 
				"status", "completed");
				
		Tools.Sleep(1000L);
	}


	private static void user_withdrawal() {
		
		User terry = User.findFirst("username like 'Terry%'");
		
		Actions.userWithdrawal(terry.getId().toString(), 140d);
		
		
		
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

		User dick = User.findFirst("username like 'Dick%'");

		Creator leonardo = Creator.findFirst("username like 'Leonardo%'");

		Actions.sellFromUser(dick.getId().toString(), bill.getId().toString(), leonardo.getId().toString(), 5, 1.2d);
		
		Tools.Sleep(1000L);

	}

	private static void sell_from_creator() {
		// Dick is buying from leonardo, the creator
		User dick = User.findFirst("username like 'Dick%'");

		Creator leonardo = Creator.findFirst("username like 'Leonardo%'");


		Actions.sellFromCreator(leonardo.getId().toString(), dick.getId().toString(), 101, 1.0d);
		
		Tools.Sleep(1000L);
		
		User john = User.findFirst("username like 'John%'");
		Creator dusty = Creator.findFirst("username like 'Dusty%'");


		Actions.sellFromCreator(dusty.getId().toString(), john.getId().toString(), 50, 2.0d);
			

		Tools.Sleep(1000L);

	}


	private static void create_ask() {

		// TODO don't use the Parent thing, just get the users_id
		User dick = User.findFirst("username like 'Dick%'");
		String dickUserId = dick.getId().toString();

		Creator leonardo = Creator.findFirst("username like 'Leonardo%'");
		String leonardoCreatorId = leonardo.getId().toString();
		
		Creator dusty = Creator.findFirst("username like 'Dusty%'");
		String dustyCreatorId = dusty.getId().toString();

		Actions.createAsk(dickUserId, leonardoCreatorId, 75, 1d,"2014-12-12", true);
		Tools.Sleep(1000L);
		
		User john = User.findFirst("username like 'John%'");
		String johnUserId = john.getId().toString();
		
		Actions.createAsk(johnUserId, dustyCreatorId, 50, 2.05d,"2014-12-12", true);
		Tools.Sleep(1000L);

	}

	private static void create_bid() {

		// TODO find a way to validate a bid

		// Find Bill
		User bill = User.findFirst("username like 'Bill%'");
		String billUserId = bill.getId().toString();

		Creator leonardo = Creator.findFirst("username like 'Leonardo%'");
		String leonardoCreatorId = leonardo.getId().toString();
		
		Creator dusty = Creator.findFirst("username like 'Dusty%'");
		String dustyCreatorId = dusty.getId().toString();

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
		
		Actions.createBid(terryUserId, leonardoCreatorId, 30, 1.16d, "2014-12-12", true);
		Tools.Sleep(1000L);
		
		Actions.createBid(terryUserId, dustyCreatorId, 60, 2.5d, "2014-12-12", true);
		Tools.Sleep(1000L);
		
	}

	private static final void delete_all() {
		Host_btc_addresses.deleteAll();
		Users_deposits.deleteAll();
		Sales_from_creators.deleteAll();
		Sales_from_users.deleteAll();
		Pieces_owned.deleteAll();
		Pieces_issued.deleteAll();
		Users_btc_address.deleteAll();
		Users_withdrawals.deleteAll();
		Creators_withdrawals.deleteAll();
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

		Pieces_issued.createIt("creators_id",  leonardo.getId().toString(), 
				"time_", SDF.format(new Date()), 
				"pieces_issued", 200, 
				"price_per_piece", 1d);
		Pieces_issued.createIt("creators_id", leonardo.getId().toString(), 
				"time_", SDF.format(new Date(new Date().getTime()+86400000)), 
				"pieces_issued", 300,
				"price_per_piece", 1d);

//		Pieces_total piecesTotal = Pieces_total.findFirst("creators_id = ?", leonardo.getId().toString());
		
		Creator dusty = Creator.findFirst("username like ?", "Dusty%");
		Pieces_issued.createIt(
				"creators_id",  dusty.getId().toString(), 
				"time_", SDF.format(new Date()), 
				"pieces_issued", 50,
				"price_per_piece", 2d);
		
		
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
