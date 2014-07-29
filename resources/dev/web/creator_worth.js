
$(document).ready(function(){

	var creatorName = getParameterByName('creator');
	fillUserHighChartStandardTemplate(creatorName + '/get_worth', '#worth', 'Worth ($)', '$');

});