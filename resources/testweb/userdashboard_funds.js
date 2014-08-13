$(document).ready(function() {

    var userName = window.location.pathname.split('/').pop();

    fillUserHighChartStandardTemplate(userName + '/get_users_funds_accum', '#users_funds', 'Funds ($)', '$');


    var template = $('#transactions_template').html();
    pageNumbers['#transactions_table'] = 1;
    setupPagedTable(userName + '/get_users_transactions/', template, '#transactions', '#transactions_table');

});