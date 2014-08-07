$(document).ready(function() {

    var userName = window.location.pathname.split('/').pop();



    fillUserHighChartStandardTemplate(userName + '/get_prices_for_user', '#highcharts-chart-area', 'Price ($/piece)', '$');

});