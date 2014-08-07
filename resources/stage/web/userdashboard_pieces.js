$(document).ready(function() {




    var userName = window.location.pathname.split('/').pop();

    fillUserHighChartStandardTemplate(userName + '/get_pieces_owned_value_accum', "#pieces_owned_value", 'Value ($)', '$');
    fillUserHighChartStandardTemplate(userName + '/get_pieces_owned_accum', '#pieces_owned', '# of Pieces owned', '');
    fillUserHighChartPieChartTemplate(userName + '/get_pieces_owned_value_current', '#pieces_owned_value_current');
    fillUserHighChartStandardTemplate(userName + '/get_rewards_earned_accum', '#rewards_earned_accum', 'Reward ($)', '$');


    fillUserHighChartPieChartTemplate(userName + '/get_rewards_earned_total', '#rewards_earned_total');


});