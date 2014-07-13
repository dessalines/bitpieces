$(document).ready(function(){


    sessionId = getCookie("authenticated_session_id");
    console.log(sessionId);

    fillUserHighChartStandardTemplate('get_prices_for_user', '#highcharts-chart-area');

    


});