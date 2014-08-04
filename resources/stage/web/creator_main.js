$(document).ready(function() {
    var creatorName = getParameterByName('creator');

    fillSimpleText(creatorName + '/get_main_body', '#main_body');

    // if you're this creator, then set up summer note, issue pieces button
    var userName = getCookie('username');

    if (userName == creatorName) {
        // show the save btn
        setupSummerNote('getcreatorpage', '#main_body', 'main_body');

        saveSummerNote('savecreatorpage', '#saveBtn', '#main_body');

    }

    fillSimpleText(creatorName + '/get_pieces_owned_value_current_by_creator', '#worth_current');
    fillSimpleText(creatorName + '/get_funds_raised', '#funds_raised');

    fillSimpleText(creatorName + '/get_pieces_owned_total', '#pieces_held_current');
    fillSimpleText(creatorName + '/get_rewards_owed', '#rewards_paid');
    fillSimpleText(creatorName + '/get_backers_current_count', '#backers_current_count');
    fillSimpleText(creatorName + '/get_creators_reputation', '#creators_reputation');
    fillSimpleText(creatorName + '/get_safety_current', '#creators_safety_current');
    fillSimpleText(creatorName + '/get_price_per_piece_current', '#price_per_piece_current');
    fillSimpleText(creatorName + '/get_rewards_current', '#rewards_current');

    // the reward yield
    $.when(getJson(creatorName + '/get_price_per_piece_current'),
        getJson(creatorName + '/get_rewards_current')).done(function(a1, a2) {
        var pppCurrent = parseFloat(a1[0].replace(/[^0-9\.]+/g, ""));
        var rewards_current = parseFloat(a2[0].replace(/[^0-9\.]+/g, ""));
        var rewards_current_yield = (rewards_current * 100 / pppCurrent).toFixed(2) + '%';

        $('#rewards_current_yield').text(rewards_current_yield);

    });




    simpleFetch(creatorName + '/get_verified').done(function(result) {
        console.log('verified = ' + result);
        if (result == 'false') {
            $('#verified').addClass('text-danger');
            $('#verified').html('<i class="fa fa-exclamation"></i> Unverified');
            $("#verified").popover({
                trigger: "hover",
                content: 'This creator is unverified, they may not be authentic',
                placement: 'auto'
            });
        } else {
            $('#verified').addClass('text-success');
            $('#verified').html('<i class="fa fa-check"></i> Verified');
            $("#verified").popover({
                trigger: "hover",
                content: 'This creator has been verified by BitPieces',
                placement: 'auto'
            });
        }
    });

});