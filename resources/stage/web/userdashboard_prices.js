$(document).ready(function() {

    var userName = getParameterByName('user');



    fillUserHighChartStandardTemplate(userName + '/get_prices_for_user', '#highcharts-chart-area', 'Price ($/piece)', '$');

});