$(document).ready(function() {
	var creatorName = getParameterByName('creator');
	fillUserHighChartPieChartTemplate(creatorName + '/get_pieces_owned_value_current_creator', '#pieces_owned_value_current_creator');
	fillTableFromMustache(creatorName + '/get_pieces_issued', '#pieces_issued_template', '#pieces_issued', '#pieces_issued_table');
});