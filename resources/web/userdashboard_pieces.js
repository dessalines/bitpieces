
$(document).ready(function(){


    sessionId = getCookie("authenticated_session_id");
    console.log(sessionId);

    fillUserHighChartStandardTemplate(sessionId + '/get_pieces_owned_value_accum', "#pieces_owned_value", 'Value ($)', '$');
    fillUserHighChartStandardTemplate(sessionId + '/get_pieces_owned_accum', '#pieces_owned', '# of Pieces owned', '');
    fillUserHighChartPieChartTemplate(sessionId + '/get_pieces_owned_value_current', '#pieces_owned_value_current');
    fillUserHighChartStandardTemplate(sessionId + '/get_rewards_earned', '#rewards_earned', 'Reward ($)', '$');
});


