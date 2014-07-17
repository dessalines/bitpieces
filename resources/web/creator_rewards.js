
$(document).ready(function(){

	var creatorName = getParameterByName('creator');
	fillTableFromMustache(creatorName + '/get_rewards_pct', '#rewards_pct_template', '#rewards_pct', '#rewards_pct_table');
	fillTableFromMustache(creatorName + '/get_rewards_owed_to_user', '#rewards_owed_to_user_template', '#rewards_owed_to_user', '#rewards_owed_to_user_table');
});