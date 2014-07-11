package com.heretic.bitpieces_practice.tables;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.BelongsTo;
import org.javalite.activejdbc.annotations.Table;

public class Tables {

	@Table("users") 
	public static class User extends Model {}

	@Table("creators") 
	public static class Creator extends Model {}

	@Table("users_btc_addresses") 
	public static class Users_btc_address extends Model {}

	@Table("creators_btc_addresses") 
	public static class Creators_btc_address extends Model {}

	@Table("pieces_issued") 
	public static class Pieces_issued extends Model {}

	@Table("pieces_owned") 
	public static class Pieces_owned extends Model {}
	
	@Table("pieces_total") 
	public static class Pieces_total extends Model {}

	@Table("bids") 
	public static class Bid extends Model {}

	@Table("asks") 
	public static class Ask extends Model {}

	@Table("sales_from_users") 
	public static class Sales_from_users extends Model {}
	
	@Table("sales_from_creators") 
	public static class Sales_from_creators extends Model {}

	@Table("rewards") 
	public static class Reward extends Model {}
	
	@Table("rewards_current") 
	public static class Rewards_current extends Model {}

	@Table("rewards_earned") 
	public static class Rewards_earned extends Model {}
	
	@Table("pieces_available")
	public static class Pieces_available extends Model {}
	
	@Table("pieces_owned_total")
	public static class Pieces_owned_total extends Model {}
	
	@Table("host_btc_addresses")
	public static class Host_btc_addresses extends Model {}
	
	@Table("ask_bid_accept_checker")
	public static class Ask_bid_accept_checker extends Model {}
	
	@Table("creators_page_fields")
	public static class Creators_page_fields extends Model {}
	
	@Table("users_deposits")
	public static class Users_deposits extends Model {}
	
	@Table("users_withdrawals")
	public static class Users_withdrawals extends Model {}
	
	@Table("creators_withdrawals")
	public static class Creators_withdrawals extends Model {}
	
	@Table("users_funds_current")
	public static class Users_funds_current extends Model {}
	
	@Table("creators_funds_current")
	public static class Creators_funds_current extends Model {}
	
	@Table("pieces_owned_value_current_by_creator")
	public static class Pieces_owned_value_current_by_creator extends Model {}
	
	@Table("pieces_owned_value_accum")
	public static class Pieces_owned_value_accum extends Model {}
	

}

