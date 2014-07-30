$(document).ready(function() {

    var creatorName = getParameterByName('creator');


    var template = $('#rewards_template').html();
    pageNumbers['#rewards_table'] = 1;
    setupPagedTable(creatorName + '/get_rewards/', template, '#rewards', '#rewards_table');


    var template = $('#rewards_owed_to_user_template').html();
    pageNumbers['#rewards_owed_to_user_table'] = 1;
    setupPagedTable(creatorName + '/get_rewards_owed_to_user/', template, '#rewards_owed_to_user', '#rewards_owed_to_user_table');

});