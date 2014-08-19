$(document).ready(function() {

    // coinbase stuff


    sessionId = getCookie("authenticated_session_id");
    console.log(sessionId);

    showHideDepositButton();

    // fillUserHighChartStandardTemplate('get_users_funds_accum', '#users_funds', 'Funds ($)', '$');

    var userName = window.location.pathname.split('/').pop();


    var template = $('#recent_activity_template').html();
    pageNumbers['#recent_activity_table'] = 1;
    setupPagedTable(userName + '/get_users_activity/', template, '#recent_activity', '#recent_activity_table');


    fillUserHighChartPieChartTemplate(userName + '/get_pieces_owned_value_current', '#pieces_owned_value_current');
    fillFieldFromMustache(userName + '/get_users_funds_current', '#funds_current_template', '#funds_current', true);
    fillFieldFromMustache(userName + '/get_rewards_earned_total_by_user', '#rewards_earned_total_by_user_template', '#rewards_earned_total_by_user', true);
    fillFieldFromMustache(userName + '/get_pieces_value_current_by_owner', '#pieces_owned_current_template', '#pieces_owned_current', true);
    fillFieldFromMustache(userName + '/get_users_reputation', '#users_reputation_template', '#users_reputation', false);

    fillTableFromMustacheSpecial(userName + '/get_users_bids_asks_current', '#users_bids_asks_current_template', '#users_bids_asks_current',
        '#users_bids_asks_current_table',
        ".remove-button", sessionId);



    // TODO do this in a paged way
    // var template = $('#users_bids_asks_current_template').html();
    // pageNumbers['#users_bids_asks_current_table'] = 1;
    // setupPagedTable(creatorName + '/get_users_bids_asks_current/', template, '#users_bids_asks_current', '#users_bids_asks_current_table');

    simpleFetch(userName + '/get_pieces_value_current_by_owner').done(function(result) {
        if (result == 0) {
            $('.first-timers').removeClass('hide');
        }
    });

    setupWithdrawForm();

});

function setupWithdrawForm() {
    $('#withdrawForm').bootstrapValidator({
        message: 'This value is not valid',
        excluded: [':disabled'],

    });

    var userName = getCookie('username');

    simpleFetch(userName + '/get_users_funds_current').done(function(result) {
        var fundsNum = result.replace(/[^0-9\.]+/g, "");
        var usersFunds = parseFloat(fundsNum);

        $("#funds").text(result);
        $('[name="withdrawAmount"]').attr('placeholder', 'Current funds : ' + result);
        $('[name="withdrawAmount"]').bind('keyup', function(f) {
            var withdrawAmount = parseFloat($(this).val());

            var fundsLeft = usersFunds - withdrawAmount;
            if (!isNaN(fundsLeft)) {

                $('#fundsLeft').text('$' + fundsLeft);

                if (fundsLeft < 0) {

                    $('#placeWithdrawBtn').prop('disabled', true);
                    $('#fundsLeft').addClass("text-danger");
                    $('#fundsLeft').removeClass("text-success");

                } else {
                    $('#placeWithdrawBtn').prop('disabled', false);
                    $('#fundsLeft').addClass("text-success");
                    $('#fundsLeft').removeClass("text-danger");
                }
            }

        });
    });
    $("#placeWithdrawBtn").click(function(event) {
        standardFormPost('make_withdrawal_fake', '#withdrawForm', '#withdrawModal');
        event.preventDefault();
    });


}

function showHideDepositButton() {
    var userName = window.location.pathname.split('/').pop();
    var sessionUserName = getCookie("username");

    if (userName == sessionUserName) {
        $('#depositBtn').removeClass("hide");
        $('#withdrawBtn').removeClass("hide");
        setupDepositButton("make_deposit_fake", '#placedepositBtn', '#depositForm', '#depositModal');
        setupCurrFields();

    }

}


function fillTableFromMustacheSpecial(url, templateId, divId, tableId, buttonName, sessionId) {
    var url = sparkService + url // the script where you handle the form input.
    var userName = window.location.pathname.split('/').pop();
    var sessionUserName = getCookie("username");
    $.ajax({
        type: "GET",
        url: url,
        xhrFields: {
            withCredentials: true
        },
        // data: seriesData, 
        success: function(data, status, xhr) {
            console.log(data);
            // var jsonObj = JSON.parse(data);
            // JSON.useDateParser();
            // var jsonObj = jQuery.parseJSON(data);
            // JSON.useDateParser();
            var jsonObj = JSON.parse(data);

            var template = $(templateId).html();
            Mustache.parse(template); // optional, speeds up future uses
            var rendered = Mustache.render(template, jsonObj);
            $(divId).html(rendered);
            $(tableId).tablesorter({
                debug: true
            });
            console.log(jsonObj);
            console.log(template);
            console.log(rendered);
            if (userName == sessionUserName) {
                $('.remove-button').removeClass("hide");
            }

            $(buttonName).click(function(event) {
                // var id2= $(this).closest("tr")
                var row = $(this).closest("tr"); // Finds the closest row <tr> 

                var json = {};
                json['time_'] = row.find("td:nth-child(1)").text();
                json['creatorName'] = row.find("td:nth-child(2)").text(); // Finds the 2nd <td> element
                json['type'] = row.find("td:nth-child(3)").text();


                console.log(row);
                console.log(json);

                var url = sparkService + "/delete_bid_ask";
                $.ajax({
                    type: "POST",
                    url: url,
                    xhrFields: {
                        withCredentials: true
                    },
                    data: json,
                    success: function(data, status, xhr) {

                        row.remove();
                        xhr.getResponseHeader('Set-Cookie');



                        toastr.success("Deleted the " + json['type']);



                    },
                    error: function(request, status, error) {
                        toastr.error(request.responseText);
                    }



                });

            });

        },
        error: function(request, status, error) {

            toastr.error(request.responseText);
        }
    });

}