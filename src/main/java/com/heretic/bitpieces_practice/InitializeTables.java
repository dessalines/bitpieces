package com.heretic.bitpieces_practice;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.Model;

import com.heretic.bitpieces_practice.actions.Actions;
import com.heretic.bitpieces_practice.tables.Tables.Ask;
import com.heretic.bitpieces_practice.tables.Tables.Bid;
import com.heretic.bitpieces_practice.tables.Tables.Creator;
import com.heretic.bitpieces_practice.tables.Tables.Creators_btc_address;
import com.heretic.bitpieces_practice.tables.Tables.Creators_required_fields;
import com.heretic.bitpieces_practice.tables.Tables.Pieces_issued;
import com.heretic.bitpieces_practice.tables.Tables.Pieces_owned;
import com.heretic.bitpieces_practice.tables.Tables.Pieces_total;
import com.heretic.bitpieces_practice.tables.Tables.Sales_from_creators;
import com.heretic.bitpieces_practice.tables.Tables.Sales_from_users;
import com.heretic.bitpieces_practice.tables.Tables.User;
import com.heretic.bitpieces_practice.tables.Tables.Users_btc_address;
import com.heretic.bitpieces_practice.tables.Tables.Users_required_fields;


public class InitializeTables {
	public static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	
	
	public static void main(String[] args) {

		System.out.println( "Hello World!" );

		Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/bitpieces_practice", "root", "teresa");

		delete_all();
		
		setup_users();

		setup_creators();
		
		issue_pieces();
		
		sell_from_creator();
		
		sell_from_user();
		
		create_bid();
		
		create_ask();


	}
	
	private static void sell_from_user() {
		
		// Bill is buying some from dick
		Users_required_fields bill = Users_required_fields.findFirst("first_name = 'Bill'");
		Integer billUserId = bill.getInteger("users_id");
		Users_btc_address billBtcAddr = Users_btc_address.findFirst("users_id = ?", billUserId);
		
		Users_required_fields dick = Users_required_fields.findFirst("first_name = 'Dick'");
		Integer dickUserId = dick.getInteger("users_id");
		Users_btc_address dickBtcAddr = Users_btc_address.findFirst("users_id = ?", dickUserId);
		
		Creators_required_fields leonardo = Creators_required_fields.findFirst("first_name = 'Leonardo'");
		Integer leonardoUserId = leonardo.getInteger("creators_id");
		
		Actions.sellFromUser(dickBtcAddr, billBtcAddr, leonardoUserId, 5, 3d);
		
		
	}
	
	private static void sell_from_creator() {
		// Dick is buying from leonardo, the creator
		Users_required_fields dick = Users_required_fields.findFirst("first_name = 'Dick'");
		Integer dickUserId = dick.getInteger("users_id");
		Users_btc_address userBtcAddr = Users_btc_address.findFirst("users_id = ?", dickUserId);
		
		Creators_required_fields leonardo = Creators_required_fields.findFirst("first_name = 'Leonardo'");
		Integer leonardoUserId = leonardo.getInteger("creators_id");
		Creators_btc_address creatorBtcAddr = Creators_btc_address.findFirst("creators_id = ?", leonardoUserId);
		
		Actions.sellFromCreator(creatorBtcAddr, userBtcAddr, 20, 10d);
		

		
	}
	
	
	private static void create_ask() {
	
		
		
		// TODO don't use the Parent thing, just get the users_id
		Users_required_fields dick = Users_required_fields.findFirst("first_name = 'Dick'");
		Integer dickUserId = dick.getInteger("users_id");
		
		Creators_required_fields leonardo = Creators_required_fields.findFirst("first_name = 'Leonardo'");
		Integer leonardoCreatorId = leonardo.getInteger("creators_id");
		
		Actions.createAsk(dickUserId, leonardoCreatorId, 10, 100d);
		
	}
	
	private static void create_bid() {
		
		// TODO find a way to validate a bid
		
		// Find Bill
		Users_required_fields bill = Users_required_fields.findFirst("first_name = 'Bill'");
		Integer billUserId = bill.getInteger("users_id");
		
		Creators_required_fields leonardo = Creators_required_fields.findFirst("first_name = 'Leonardo'");
		Integer leonardoCreatorId = leonardo.getInteger("creators_id");
		
		Actions.createBid(billUserId, leonardoCreatorId, 5, 100d);
				
	}
	
	private static final void delete_all() {
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
		Creators_required_fields leonardo = Creators_required_fields.findFirst("first_name = 'Leonardo'");
		
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
		
		Creators_required_fields.createIt("creators_id", creator1.getId(), "first_name", "Leonardo", "last_name", "Davinci");
		Creators_required_fields.createIt("creators_id", creator2.getId(), "first_name", "Dusty", "last_name", "Springfield");
		
		Creators_btc_address.createIt("creators_id", creator1.getId(), "btc_addr", "fake");
		Creators_btc_address.createIt("creators_id", creator2.getId(), "btc_addr", "fake");
		
	
		
	}

	private static void setup_users() {

		
		User user1 = new User();
		user1.saveIt();

		User user2 = new User();
		user2.saveIt();

		Users_required_fields.createIt("users_id", user1.getId(), "first_name", "Bill", "last_name", "Jeffries");
		Users_required_fields.createIt("users_id", user2.getId(), "first_name", "Dick", "last_name", "Tatum");
		
		Users_btc_address.createIt("users_id", user1.getId(), "btc_addr", "fake");
		Users_btc_address.createIt("users_id", user1.getId(), "btc_addr", "fake2");
		
		Users_btc_address.createIt("users_id", user2.getId(), "btc_addr", "fake");
		
		
	}

}
