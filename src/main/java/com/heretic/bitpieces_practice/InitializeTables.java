package com.heretic.bitpieces_practice;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.javalite.activejdbc.Base;

import com.heretic.bitpieces_practice.tables.Tables.Creator;
import com.heretic.bitpieces_practice.tables.Tables.Creators_btc_address;
import com.heretic.bitpieces_practice.tables.Tables.Creators_required_fields;
import com.heretic.bitpieces_practice.tables.Tables.Pieces_issued;
import com.heretic.bitpieces_practice.tables.Tables.Pieces_total;
import com.heretic.bitpieces_practice.tables.Tables.User;
import com.heretic.bitpieces_practice.tables.Tables.Users_btc_address;
import com.heretic.bitpieces_practice.tables.Tables.Users_required_fields;


public class InitializeTables {
	public static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	
	
	public static void main(String[] args) {

		System.out.println( "Hello World!" );

		Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/bitpieces_practice", "root", "teresa");

		
		setup_users();

		setup_creators();
		
		issue_pieces();
		
		


	}

	private static void issue_pieces() {
		// A creator issues some pieces
		// Find leonardo davinci
		Creators_required_fields leonardo = Creators_required_fields.findFirst("first_name = 'Leonardo'");
		
		Pieces_issued.createIt("creators_id",  leonardo.get("creators_id"), "time_", SDF.format(new Date()), "pieces_issued", 200);
		Pieces_issued.createIt("creators_id", leonardo.get("creators_id"), 
				"time_", SDF.format(new Date(new Date().getTime()+86400000)), 
				"pieces_issued", 200);
		
		Pieces_total piecesTotal = Pieces_total.findFirst("creators_id = ?", leonardo.get("creators_id"));
		System.out.println(piecesTotal);
	}

	private static void setup_creators() {
		Creators_required_fields.deleteAll();
		Creators_btc_address.deleteAll();
		Pieces_issued.deleteAll();
		Creator.deleteAll();
		
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
		Users_required_fields.deleteAll();
		Users_btc_address.deleteAll();
		User.deleteAll();
		
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
