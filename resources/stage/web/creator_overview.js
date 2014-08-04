$(document).ready(function() {

    var creatorName = getParameterByName('creator');
    fillSimpleText(creatorName + '/get_main_body', '#main_body');

    // fillFieldFromMustache(creatorName + '/get_main_body', 
    //     '#worth_current_template' , '#worth_current', true);


    // fillFieldFromMustache(creatorName + '/get_pieces_owned_value_current_by_creator', 
    //     '#worth_current_template' , '#worth_current', true);

    // fillFieldFromMustache(creatorName + '/get_main_body_by_creator', 
    //     '#worth_current_template' , '#worth_current', true);




    fillUserHighChartStandardTemplate(creatorName + '/get_pricing', '#pricing', 'Price ($/piece)', '$');

    fillUserHighChartStandardTemplate(creatorName + '/get_safety', '#creators_safety', 'Able to pay X years of rewards to funders', 'Years');


    var template = $('#creators_activity_template').html();
    pageNumbers['#creators_activity_table'] = 1;
    setupPagedTable(creatorName + '/get_creators_activity/', template, '#creators_activity', '#creators_activity_table');




});