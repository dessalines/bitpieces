$(document).ready(function() {
    // var creatorName = window.location.pathname.split('/').pop();
    var creatorName = window.location.pathname.split('/').pop();
    console.log('cname = ' + creatorName);

    fillSimpleText(creatorName + '/get_main_body', '#main_body');

    // if you're this creator, then set up summer note, issue pieces button
    var userName = getCookie('username');


    if (userName == creatorName) {

        // show the creator content
        $("#creator_edit_content").removeClass("hide");



        // show the save btn
        setupSummerNote('getcreatorpage', '#main_body', 'main_body');

        saveSummerNote('savecreatorpage', '#saveBtn', '#main_body');

        simpleFetch('getcreatorpage').done(function(result) {
            var jsonObj = JSON.parse(result);
            console.log(jsonObj);
            var youtube_link = jsonObj['youtube_link'];
            console.log('youtube ' + youtube_link);
            if (youtube_link != null) {
                $('#shortDescriptionInput').text(youtube_link);
            }

            var short_desc = jsonObj['description'];
            console.log('short_desc ' + short_desc);
            if (short_desc != null) {
                // $('#shortDescriptionInput').text(short_desc);
            }



            var emptyObj = '{"main_body": "Nothing here yet"}';
            console.log('page = ' + result);
            console.log('emptyObj = ' + emptyObj);
            if (result == emptyObj) {
                console.log('unhiding');
                $('.first-timers').removeClass('hide');
            }
        });


        $('#youtubeLinkForm').bootstrapValidator({
            message: 'This value is not valid',
            excluded: [':disabled'],
            submitButtons: 'button[type="submit"]'

        }).on('success.form.bv', function(event) {
            event.preventDefault();
            standardFormPost('save_youtube_link', "#youtubeLinkForm");
        });

        $('#shortDescriptionForm').bootstrapValidator({
            message: 'This value is not valid',
            excluded: [':disabled'],
            submitButtons: 'button[type="submit"]'

        }).on('success.form.bv', function(event) {
            event.preventDefault();
            standardFormPost('save_creator_description', "#shortDescriptionForm");
        });

    }



    fillSimpleText(creatorName + '/get_pieces_owned_value_current_by_creator', '#worth_current');
    fillSimpleText(creatorName + '/get_funds_raised', '#funds_raised');

    fillSimpleText(creatorName + '/get_pieces_owned_total', '#pieces_held_current');
    fillSimpleText(creatorName + '/get_pieces_available_total', '#pieces_available_current');
    fillSimpleText(creatorName + '/get_rewards_owed', '#rewards_paid');
    fillSimpleText(creatorName + '/get_backers_current_count', '#backers_current_count');
    fillSimpleText(creatorName + '/get_creators_reputation', '#creators_reputation');
    fillSimpleText(creatorName + '/get_safety_current', '#creators_safety_current');
    fillSimpleText(creatorName + '/get_price_per_piece_current', '#price_per_piece_current');
    fillSimpleText(creatorName + '/get_rewards_current', '#rewards_current');
    fillSimpleText(creatorName + '/get_rewards_yield_current', '#rewards_current_yield');




    simpleFetch(creatorName + '/get_safety_current').done(function(result) {
        var safetyRating = parseFloat(result);
        if (safetyRating < 5.0) {
            $('#creators_safety_current').addClass('text-danger');
            // $('#creators_safety_current').html('<i class="fa fa-exclamation"></i> ' + result);
            $("#creators_safety_current").popover({
                trigger: "hover",
                content: 'This creator cannot pay rewards to its funders for more than 5 years.',
                placement: 'auto'
            });
        } else {
            $('#creators_safety_current').addClass('text-success');
            // $('#creators_safety_current').html('<i class="fa fa-check"></i> ' + result);
            $("#creators_safety_current").popover({
                trigger: "hover",
                content: 'This creator can currently pay rewards to its funders for more than 5 years.',
                placement: 'auto'
            });
        }
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
            if (userName == creatorName) {
                $('.unverified').removeClass('hide');
            }
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