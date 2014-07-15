$(document).ready(function(){


	sessionId = getCookie("authenticated_session_id");
	console.log(sessionId);

	// fillUserHighChartStandardTemplate('get_users_funds_accum', '#users_funds', 'Funds ($)', '$');
	fillTableFromMustache('get_users_activity', '#recent_activity_template', '#recent_activity', '#recent_activity_table');
	fillUserHighChartPieChartTemplate('get_pieces_owned_value_current', '#pieces_owned_value_current');
	fillFieldFromMustache('get_users_funds_current', '#funds_current_template' , '#funds_current', true);
	fillFieldFromMustache('get_rewards_earned_total_by_user', '#rewards_earned_total_by_user_template' , '#rewards_earned_total_by_user', true);
	fillFieldFromMustache('get_pieces_value_current_by_owner', '#pieces_owned_current_template' , '#pieces_owned_current', true);
	fillFieldFromMustache('get_users_reputation', '#users_reputation_template' , '#users_reputation', false);

});

