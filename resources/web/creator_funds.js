
$(document).ready(function(){

	var creatorName = getParameterByName('creator');
	fillUserHighChartStandardTemplate(creatorName + '/get_creators_funds_accum', '#creators_funds', 'Funds(accumulated)', '$');
	fillTableFromMustache(creatorName + '/get_creators_transactions', '#creators_transactions_template', '#creators_transactions', 
		'#creators_transactions_table');
});