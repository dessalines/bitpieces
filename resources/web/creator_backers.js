
$(document).ready(function(){

	var creatorName = getParameterByName('creator');

	fillTableFromMustache(creatorName + '/get_backers_current', '#backers_current_template', '#backers_current', '#backers_current_table');
	fillTableFromMustache(creatorName + '/get_pieces_issued', '#pieces_issued_template', '#pieces_issued', '#pieces_issued_table');
});