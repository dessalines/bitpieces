$(document).ready(function() {

    var creatorName = window.location.pathname.split('/').pop();


    fillUserHighChartPieChartTemplate(creatorName + '/get_rewards_owed_to_user', '#rewards_owed_to_users');

    var template = $('#rewards_template').html();
    pageNumbers['#rewards_table'] = 1;
    setupPagedTable(creatorName + '/get_rewards/', template, '#rewards', '#rewards_table');




});