$(document).ready(function() {
    var creatorName = window.location.pathname.split('/').pop();
    fillUserHighChartPieChartTemplate(creatorName + '/get_pieces_owned_value_current_creator', '#pieces_owned_value_current_creator');



    var template = $('#pieces_issued_template').html();
    pageNumbers['#pieces_issued_table'] = 1;
    setupPagedTable(creatorName + '/get_pieces_issued/', template, '#pieces_issued', '#pieces_issued_table');


    var template = $('#backers_current_template').html();
    pageNumbers['#backers_current_table'] = 1;
    setupPagedTable(creatorName + '/get_backers_current/', template, '#backers_current', '#backers_current_table');




});