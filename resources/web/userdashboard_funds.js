$(document).ready(function() {

    var userName = getParameterByName('user');

    fillUserHighChartStandardTemplate(userName + '/get_users_funds_accum', '#users_funds', 'Funds ($)', '$');
    fillTableFromMustache(userName + '/get_users_transactions', '#transactions_template', '#transactions', '#transactions_table');
});