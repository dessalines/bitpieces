package com.bitpieces.shared;

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
	
	@Table("pieces_issued_view") 
	public static class Pieces_issued_view extends Model {}

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
	
	@Table("rewards_view") 
	public static class Rewards_view extends Model {}
	
	@Table("rewards_current") 
	public static class Rewards_current extends Model {}
	
	@Table("rewards_owed") 
	public static class Rewards_owed extends Model {}
	
	@Table("rewards_owed_to_user") 
	public static class Rewards_owed_to_user extends Model {}

	@Table("rewards_earned") 
	public static class Rewards_earned extends Model {}
	
	@Table("rewards_earned_total_by_user") 
	public static class Rewards_earned_total_by_user extends Model {}
	
	@Table("rewards_earned_total") 
	public static class Rewards_earned_total extends Model {}
	
	@Table("pieces_available")
	public static class Pieces_available extends Model {}
	
	@Table("pieces_available_view")
	public static class Pieces_available_view extends Model {}
	
	@Table("pieces_owned_total")
	public static class Pieces_owned_total extends Model {}
	
	@Table("pieces_owned_value_current_by_owner")
	public static class Pieces_owned_value_current_by_owner extends Model {}
	
	@Table("host_btc_addresses")
	public static class Host_btc_addresses extends Model {}
	
	@Table("ask_bid_accept_checker")
	public static class Ask_bid_accept_checker extends Model {}
	
	@Table("creators_page_fields")
	public static class Creators_page_fields extends Model {}
	
	@Table("creators_page_fields_view")
	public static class Creators_page_fields_view extends Model {}
	
	@Table("users_deposits")
	public static class Users_deposits extends Model {}
	
	@Table("users_withdrawals")
	public static class Users_withdrawals extends Model {}
	
	@Table("creators_withdrawals")
	public static class Creators_withdrawals extends Model {}
	
	@Table("users_funds_current")
	public static class Users_funds_current extends Model {}
	
	@Table("users_funds_accum")
	public static class Users_funds_accum extends Model {}
	
	@Table("users_transactions")
	public static class Users_transactions extends Model {}
	
	@Table("users_activity")
	public static class Users_activity extends Model {}
	
	@Table("creators_transactions")
	public static class Creators_transactions extends Model {}
	
	@Table("creators_activity")
	public static class Creators_activity extends Model {}
	
	@Table("creators_funds_current")
	public static class Creators_funds_current extends Model {}
	
	@Table("pieces_owned_value_current_by_creator")
	public static class Pieces_owned_value_current_by_creator extends Model {}
	
	@Table("pieces_owned_value_accum")
	public static class Pieces_owned_value_accum extends Model {}
	
	@Table("pieces_owned_value_current")
	public static class Pieces_owned_value_current extends Model {}
	
	@Table("pieces_owned_accum")
	public static class Pieces_owned_accum extends Model {}
	
	@Table("prices_for_user")
	public static class Prices_for_user extends Model {}
	
	@Table("badges")
	public static class Badge extends Model {}
	
	@Table("users_badges")
	public static class Users_badges extends Model {}
	
	@Table("creators_badges")
	public static class Creators_badges extends Model {}
	
	@Table("users_reputation")
	public static class Users_reputation extends Model {}
	
	@Table("creators_reputation")
	public static class Creators_reputation extends Model {}
	
	@Table("prices")
	public static class Prices extends Model {}
	
	@Table("worth")
	public static class Worth extends Model {}
	
	@Table("bids_asks")
	public static class Bids_asks extends Model {}
	
	@Table("bids_asks_current")
	public static class Bids_asks_current extends Model {}
	
	@Table("backers_current_count")
	public static class Backers_current_count extends Model {}
	
	@Table("backers_current")
	public static class Backers_current extends Model {}
	
	@Table("creators_funds_accum")
	public static class Creators_funds_accum extends Model {}
	
	@Table("rewards_earned_accum")
	public static class Rewards_earned_accum extends Model {}
	
	@Table("categories")
	public static class Categories extends Model {}
	
	@Table("creators_categories")
	public static class Creators_categories extends Model {}
	
	@Table("creators_search_view")
	public static class Creators_search_view extends Model {}
	
	@Table("currencies")
	public static class Currencies extends Model {}
	
	@Table("users_settings")
	public static class Users_settings extends Model {}
	
	@Table("creators_settings")
	public static class Creators_settings extends Model {}
	
	@Table("users_buttons")
	public static class Users_buttons extends Model {}
	
	@Table("orders")
	public static class Orders extends Model {}
	

	
	
	
	
	

}

