$(document).ready(function() {

    var creatorName = getParameterByName('creator');
    fillUserHighChartStandardTemplate(creatorName + '/get_pricing', '#pricing', 'Price ($/piece)', '$');
    fillTableFromMustache(creatorName + '/get_bids_asks_current', '#bids_asks_template', '#bids_asks', '#bids_asks_table');

});