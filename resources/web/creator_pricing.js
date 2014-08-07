$(document).ready(function() {

    var creatorName = window.location.pathname.split('/').pop();
    fillUserHighChartStandardTemplate(creatorName + '/get_pricing', '#pricing', 'Price ($/piece)', '$');

    var template = $('#bids_asks_template').html();
    pageNumbers['#bids_asks_table'] = 1;
    setupPagedTable(creatorName + '/get_bids_asks_current/', template, '#bids_asks', '#bids_asks_table');

});