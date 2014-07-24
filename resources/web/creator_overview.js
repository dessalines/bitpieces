$(document).ready(function() {

    var creatorName = getParameterByName('creator');
    fillSimpleText(creatorName + '/get_main_body', '#main_body');

    // fillFieldFromMustache(creatorName + '/get_main_body', 
    //     '#worth_current_template' , '#worth_current', true);



    // fillFieldFromMustache(creatorName + '/get_pieces_owned_value_current_by_creator', 
    //     '#worth_current_template' , '#worth_current', true);

    // fillFieldFromMustache(creatorName + '/get_main_body_by_creator', 
    //     '#worth_current_template' , '#worth_current', true);


    fillFieldFromMustache(creatorName + '/get_pieces_owned_value_current_by_creator',
        '#worth_current_template', '#worth_current', true);

    fillFieldFromMustache(creatorName + '/get_price_per_piece_current',
        '#price_per_piece_current_template', '#price_per_piece_current', true);

    fillFieldFromMustache(creatorName + "/get_pieces_owned_total",
        '#pieces_held_current_template', '#pieces_held_current', true);

    fillFieldFromMustache(creatorName + '/get_rewards_owed',
        '#rewards_paid_template', '#rewards_paid', true);

    fillFieldFromMustache(creatorName + '/get_backers_current_count',
        '#backers_current_count_template', '#backers_current_count', false);

    fillFieldFromMustache(creatorName + '/get_creators_reputation',
        '#creators_reputation_template', '#creators_reputation', false);



    fillTableFromMustache(creatorName + '/get_creators_activity', '#creators_activity_template', '#creators_activity', '#creators_activity_table');
    fillUserHighChartStandardTemplate(creatorName + '/get_pricing', '#pricing', 'Price ($/piece)', '$');


});