
$(document).ready(function(){

	var creatorName = getParameterByName('creator');
	fillUserHighChartStandardTemplate(creatorName + '/get_pricing', '#pricing', 'Price ($/piece)', '$');
});