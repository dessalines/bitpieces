$(document).ready(function() {

    var creatorName = getParameterByName('creator');

    fillUserHighChartStandardTemplate(creatorName + '/get_creators_funds_accum', '#creators_funds', 'Funds(accumulated)', '$');


    var template = $('#creators_transactions_template').html();
    pageNumbers['#creators_transactions_table'] = 1;
    setupPagedTable(creatorName + '/get_creators_transactions/', template, '#creators_transactions', '#creators_transactions_table');
});