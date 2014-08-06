package com.bitpieces.dev.init;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import org.javalite.activejdbc.Base;

import com.bitpieces.shared.DataSources;
import com.bitpieces.shared.Tables.Ask;
import com.bitpieces.shared.Tables.Badge;
import com.bitpieces.shared.Tables.Bid;
import com.bitpieces.shared.Tables.Categories;
import com.bitpieces.shared.Tables.Creator;
import com.bitpieces.shared.Tables.Creators_badges;
import com.bitpieces.shared.Tables.Creators_btc_address;
import com.bitpieces.shared.Tables.Creators_categories;
import com.bitpieces.shared.Tables.Creators_page_fields;
import com.bitpieces.shared.Tables.Creators_withdrawals;
import com.bitpieces.shared.Tables.Currencies;
import com.bitpieces.shared.Tables.Host_btc_addresses;
import com.bitpieces.shared.Tables.Pieces_issued;
import com.bitpieces.shared.Tables.Pieces_owned;
import com.bitpieces.shared.Tables.Reward;
import com.bitpieces.shared.Tables.Sales_from_creators;
import com.bitpieces.shared.Tables.Sales_from_users;
import com.bitpieces.shared.Tables.User;
import com.bitpieces.shared.Tables.Users_badges;
import com.bitpieces.shared.Tables.Users_btc_address;
import com.bitpieces.shared.Tables.Users_deposits;
import com.bitpieces.shared.Tables.Users_withdrawals;
import com.bitpieces.shared.tools.DBActions;
import com.bitpieces.shared.tools.Tools;

/**
 * 
 * 
 * @author tyler
 *
 */
public class InitializeTables {


	public static void main(String[] args) {

		Properties prop = Tools.loadProperties(DataSources.DEV_DB_PROP);

		Base.open("com.mysql.jdbc.Driver", 
				prop.getProperty("dburl"), 
				prop.getProperty("dbuser"), 
				prop.getProperty("dbpassword"));

		System.out.println("Initializing tables...");
		
		delete_all();

		setup_currencies();

		setup_users();

		setup_host_btc_address();

		setup_categories();

		setup_creators();

		setup_badges();

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


	private static void setup_currencies() {

		
		for (Entry<String, String> e : Tools.CURRENCY_MAP.entrySet()) {
			// Unicode still not working
			Currencies.createIt("iso", e.getKey(), "name", e.getValue(), "unicode" , Tools.CURRENCY_UNICODES.get(e.getKey()));
		}


	}


	private static void setup_categories() {

		for (String e : Tools.CATEGORIES) {
			Categories.createIt("name", e);
		}

	}


	private static void setup_badges() {




		Badge.createIt("name", "Padawan Learner", "description", "Created an account");

		Badge padawanBadge = Badge.findFirst("name=?", "Padawan Learner");

		User bill = User.findFirst("username like 'Bill%'");

		// Give bill a padawan badge for registering

		Users_badges.createIt("users_id", bill.getId().toString(), "badges_id", padawanBadge.getId().toString());

		Creator leo = Creator.findFirst("username like 'Leonardo%'");
		Creators_badges.createIt("creators_id", leo.getId().toString(), "badges_id", padawanBadge.getId().toString());



	}


	private static void issue_new_reward() {
		String now = Tools.SDF.get().format(new Date());

		Creator leo = Creator.findFirst("username like 'Leonardo%'");
		
		DBActions.issueReward(leo.getId().toString(), 0.00008d);

	}


	private static void creator_withdrawal() {
		Creator leo = Creator.findFirst("username like 'Leonardo%'");

		DBActions.creatorWithdrawalFake(leo.getId().toString(), .08d);


	}


	private static void user_deposit() {

		User dick = User.findFirst("username like 'Dick%'");
		User john = User.findFirst("username like 'John%'");
		User bill = User.findFirst("username like 'Bill%'");
		User terry = User.findFirst("username like 'Terry%'");


		DBActions.makeOrUpdateOrder("fake1", "ofake1");
		DBActions.makeDepositFake(dick.getId().toString(), .15d, "fake1");

		// an exact amount, or a straight up pieces buy
		DBActions.makeOrUpdateOrder("fake2", "ofake2");
		DBActions.makeDepositFake(john.getId().toString(), .17d, "fake2");


		DBActions.makeOrUpdateOrder("fake3", "ofake3");
		DBActions.makeDepositFake(bill.getId().toString(), .015d, "fake3");


		DBActions.makeOrUpdateOrder("fake4", "ofake4");
		DBActions.makeDepositFake(terry.getId().toString(), .3d, "fake4");


		Tools.Sleep(1000L);
	}


	private static void user_withdrawal() {

		User terry = User.findFirst("username like 'Terry%'");

		DBActions.userWithdrawalFake(terry.getId().toString(), .140d);



	}


	private static void ask_bid_acceptor() {
		DBActions.askBidAccepter();

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

		DBActions.sellFromUser(dick.getId().toString(), bill.getId().toString(), leonardo.getId().toString(), 5, 0.0012d);

		Tools.Sleep(1000L);

	}

	private static void sell_from_creator() {
		// Dick is buying from leonardo, the creator
		User dick = User.findFirst("username like 'Dick%'");

		Creator leonardo = Creator.findFirst("username like 'Leonardo%'");


		DBActions.sellFromCreator(leonardo.getId().toString(), dick.getId().toString(), 101, 0.001d);

		Tools.Sleep(1000L);

		User john = User.findFirst("username like 'John%'");
		Creator dusty = Creator.findFirst("username like 'Dusty%'");


		DBActions.sellFromCreator(dusty.getId().toString(), john.getId().toString(), 50, 0.002d);


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

		DBActions.createAsk(dickUserId, leonardoCreatorId, 75, 0.001d,"2014-12-12", true);
		Tools.Sleep(1000L);

		User john = User.findFirst("username like 'John%'");
		String johnUserId = john.getId().toString();

		DBActions.createAsk(johnUserId, dustyCreatorId, 50, 0.00205d,"2014-12-12", true);
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

		DBActions.createBid(billUserId, leonardoCreatorId, 5, 0.0012263d, "2014-12-12", true);
		Tools.Sleep(1000L);

		// Find John, also wants to bid on it, at a higher bid, and more pieces
		User john = User.findFirst("username like 'John%'");
		String johnUserId = john.getId().toString();

		DBActions.createBid(johnUserId, leonardoCreatorId, 10, 0.0015d, "2014-12-12", true);
		Tools.Sleep(1000L);

		// Finally Terry, wants to bid the lowest, but more than the asker has to sell
		User terry = User.findFirst("username like 'Terry%'");
		String terryUserId = terry.getId().toString();

		DBActions.createBid(terryUserId, leonardoCreatorId, 30, 0.00116d, "2014-12-12", true);
		Tools.Sleep(1000L);

		DBActions.createBid(terryUserId, dustyCreatorId, 60, 0.0025d, "2014-12-12", true);
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
		Creators_categories.deleteAll();
		Categories.deleteAll();
		Users_badges.deleteAll();
		Creators_badges.deleteAll();
		Badge.deleteAll();
		Bid.deleteAll();
		Ask.deleteAll();
		Reward.deleteAll();
		User.deleteAll();
		Creator.deleteAll();
		Currencies.deleteAll();

	}

	private static void issue_pieces() {
		// A creator issues some pieces
		// Find leonardo davinci
		//		Creators_required_fields leonardo = Creators_required_fields.findFirst("username = 'Leonardo'");
		Creator leonardo = Creator.findFirst("username like ?", "Leonardo%");
		
		DBActions.issuePieces(leonardo.getId().toString(),
				200,
				0.001d);

		Creator dusty = Creator.findFirst("username like ?", "Dusty%");
		
		DBActions.issuePieces(dusty.getId().toString(),
				50,
				0.002d);


	}

	private static void setup_creators() {
		Currencies btc = Currencies.findFirst("iso=?", "BTC");
		String now = Tools.SDF.get().format(new Date());
		Creator leo = Creator.createIt("username", "Leonardo_Davinci",
				"password_encrypted", Tools.PASS_ENCRYPT.encryptPassword("dog"),
				"email", "asdf1@gmail.com",
				"local_currency_id", btc.getId());
		Creator dusty = Creator.createIt("username", "Dusty_Springfield",
				"password_encrypted", Tools.PASS_ENCRYPT.encryptPassword("dog"),
				"email", "asdf2@gmail.com",
				"local_currency_id", btc.getId());

		Creators_btc_address.createIt("creators_id", leo.getId(), "btc_addr", "fake");
		Creators_btc_address.createIt("creators_id", dusty.getId(), "btc_addr", "fake");

		
		DBActions.issueReward(leo.getId().toString(), 0.00001d);
		DBActions.issueReward(dusty.getId().toString(), 0.00005d);

		Creators_page_fields.createIt("creators_id", leo.getId(),
				"main_body", "The main body of leo's page");

		Categories visualArts = Categories.findFirst("name = ?", "Visual Arts");
		Categories design = Categories.findFirst("name = ?", "Design");
		Categories music = Categories.findFirst("name = ?", "Music");

		Creators_categories.createIt("creators_id", leo.getId(), "categories_id", visualArts.getId());
		Creators_categories.createIt("creators_id", leo.getId(), "categories_id", design.getId());
		Creators_categories.createIt("creators_id", dusty.getId(), "categories_id", music.getId());



	}

	private static void setup_users() {

		// Find BTC currency
		Currencies btc = Currencies.findFirst("iso=?", "BTC");
		
		for (String name : Arrays.asList("Bill_Jeffries", "Dick_Tatum", "John_Himperdinkle", "Terry_Westworth")) {
			User cUser = User.createIt("username", name, 
					"password_encrypted", Tools.PASS_ENCRYPT.encryptPassword("dog"),
					"email", name + "22@gmail.com", 
					"local_currency_id", btc.getId());
			Users_btc_address.createIt("users_id", cUser.getId().toString(), "btc_addr", "fake");
		}

	}

}
