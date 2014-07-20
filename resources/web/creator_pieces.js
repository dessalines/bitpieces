$(document).ready(function() {
	var creatorName = getParameterByName('creator');
	fillUserHighChartPieChartTemplate(creatorName + '/get_pieces_owned_value_current_creator', '#pieces_owned_value_current_creator');
});