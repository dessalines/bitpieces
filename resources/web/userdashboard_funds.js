
$(document).ready(function(){


    sessionId = getCookie("authenticated_session_id");
    console.log(sessionId);

    fillUserHighChartStandardTemplate('get_users_funds_accum', '#users_funds');
});


