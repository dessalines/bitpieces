$(document).ready(function() {

    sessionId = getCookie("authenticated_session_id");
    userType = getCookie("usertype");
    setupMiniSubmenu();
    navigateWithParams();


    // var creatorName = window.location.pathname.split('/').pop();
    var creatorName = getParameterByName('creator');
    $('#page_title').text(creatorName);


    if (sessionId != null && userType == 'User') {
        showHideButtons(creatorName);
        bidAskOrBuySetup("/placeask", creatorName, '#askForm', "#placeaskBtn", "#askModal");
        bidAskOrBuySetup("/placebid", creatorName, '#bidForm', "#placebidBtn", "#bidModal");
        bidAskOrBuySetup("/placebuy", creatorName, '#buyForm', "#placebuyBtn", "#buyModal");


        fillFieldFromMustache('deposit_button', '#deposit_template', '#deposit_div', false);
    }




    // if you're this creator, then set up summer note, issue pieces button
    var userName = getCookie('username');
    console.log('un = ' + userName);
    if (userName == creatorName) {
        // show the save btn
        setupModal("issue_pieces", '#issueForm', "#placeIssuePiecesBtn", "#issueModal");

        setupIssueForm(creatorName);

        setupChangeRewardForm(creatorName);
        setupModal("new_reward", '#rewardForm', "#placeChangeRewardBtn", "#rewardModal");

        setupWithdrawalForm(creatorName);
        setupModal("creator_withdraw", '#withdrawForm', "#placeWithdrawBtn", "#withdrawModal");
        showHideCreatorButtons();

    }

    // fillFieldFromMustache(creatorName + '/get_main_body', 
    //     '#worth_current_template' , '#worth_current', true);



    // fillFieldFromMustache(creatorName + '/get_pieces_owned_value_current_by_creator', 
    //     '#worth_current_template' , '#worth_current', true);

    // fillFieldFromMustache(creatorName + '/get_main_body_by_creator', 
    //     '#worth_current_template' , '#worth_current', true);



    // The date picker
    $('.datepicker').pickadate({
        format: 'yyyy-mm-dd'
    });



});



function showHideButtons(creatorName) {
    // Only show these things if its a user type
    var userName = getCookie('username');
    if (getCookie('usertype') == 'User') {
        // Showing or hiding the bid/ask/buy buttons
        simpleFetch(creatorName + "/get_pieces_available").done(function(result) {
            console.log('result = ' + result);
            if (result > 0) {
                $("#buyBtn").removeClass("hide");
                $('[name="buyPieces"]').attr('placeholder', 'There are ' + result + ' pieces left');
                simpleFetch(creatorName + "/get_pieces_issued_most_recent_price").done(function(result) {
                    // $('[name="buy"]').attr('placeholder','$' + result + '/piece');
                    var buyNum = result.replace(/^\D+/g, '');
                    $('[name="buy"]').val(buyNum);
                });

            }
        });
        simpleFetch(creatorName + "/get_pieces_owned_total").done(function(result) {
            console.log('result = ' + result);
            if (result > 0) {
                $('[name="bidPieces"]').attr('placeholder', 'There are ' + result + ' pieces owned');
            }
        });


        simpleFetch(userName + "/" + creatorName + "/get_pieces_owned_current").done(function(result) {
            console.log('result = ' + result);

            if (result > 0) {
                $("#askBtn").removeClass("hide");
                $('[name="askPieces"]').attr('placeholder', 'You own ' + result + ' pieces');

            }
        });

        if (sessionId != null) {
            $("#bidBtn").removeClass("hide");
            var url = creatorName + '/get_price_per_piece_current';
            simpleFetch(url).done(function(result) {

                $('[name="bid"]').attr('placeholder', 'Last Price was ' + result + '/piece');
                $('[name="ask"]').attr('placeholder', 'Last Price was ' + result + '/piece');
            });

        }




        simpleFetch(userName + '/get_users_funds_current').done(function(result) {
            // This part adds the totals and such
            var types = ['bid', 'ask', 'buy'];
            types.forEach(function(e) {
                var fundsNum = result.replace(/[^0-9\.]+/g, "");
                var currSymbol = result[0];
                var usersFunds = parseFloat(fundsNum);
                // $('[name="usersFunds"]').text(result);
                $('[name="usersFunds"]').text(result);
                $('#' + e + 'Symbol').text(currSymbol);
                $('[name="' + e + 'Pieces' + '"],[name="' + e + '"]').bind('keyup', function(f) {

                    var buyPieces = parseFloat($(this).val());

                    // var buyPrice = $('[name="buy"]').text();
                    // var buyPrice = parseFloat($('[name="buy"]').attr('placeholder').substring(1).split('/')[0]);
                    var buyPrice = parseFloat($('[name="' + e + '"]').val());
                    // alert(buyPieces + ' ' + buyPrice)
                    var total = buyPrice * buyPieces;


                    if (!isNaN(total)) {
                        $('#' + e + 'Total').text(currSymbol + total);
                        var fundsLeft;
                        if (e == 'ask') {
                            fundsLeft = usersFunds + total;
                        } else {
                            fundsLeft = usersFunds - total;
                        }
                        //        console.log(total);
                        // console.log(fundsNum);
                        //     console.log(usersFunds);
                        //     console.log(e);
                        $('#' + e + 'FundsLeft').text(currSymbol + fundsLeft);

                        if (fundsLeft <= 0) {
                            $('button[href="#depositModal"]').removeClass("hide");
                            $('#' + e + 'FundsLeft').addClass("text-danger");
                            $('#' + e + 'FundsLeft').removeClass("text-success");
                            $('#place' + e + 'Btn').addClass("hide");
                        } else {
                            $('button[href="#depositModal"]').addClass("hide");
                            $('#' + e + 'FundsLeft').addClass("text-success");
                            $('#' + e + 'FundsLeft').removeClass("text-danger");
                            $('#place' + e + 'Btn').removeClass("hide");
                        }


                    }
                });
            });
        });


    }
}


function bidAskOrBuySetup(shortUrl, creatorName, formId, buttonId, modalId) {
    $(formId).bootstrapValidator({
        message: 'This value is not valid',
        excluded: [':disabled'],

    });

    // Placing the Ask
    $(buttonId).click(function(event) {

        // serializes the form's elements.
        var formData = $(formId).serializeArray();

        // Set the creator id
        var creator = {
            name: "creatorName",
            value: creatorName
        };
        formData.push(creator);

        console.log(formData);

        // Loading
        $(this).button('loading');

        // username = $('#userLoginDiv').find('input[name="username"]').val();
        // // = $("#inputUsername3").val();
        // document.cookie = "username=" + username ;

        var url = sparkService + shortUrl;
        $.ajax({
            type: "POST",
            url: url,
            xhrFields: {
                withCredentials: true
            },
            data: formData,
            success: function(data, status, xhr) {

                //    console.log(xhr);
                // console.log(asdf);
                // console.log(xhr.getAllResponseHeaders()); 

                // alert(data); // show response from the php script.

                xhr.getResponseHeader('Set-Cookie');

                // old way
                // document.cookie="authenticated_session_id=" + data + 
                // "; expires=" + expireTimeString(60*60); // 1 hour (field is in seconds)
                // Hide the modal, reset the form, show successful
                $(modalId).modal('hide');
                $(formId)[0].reset();

                toastr.success(data);



            },
            error: function(request, status, error) {
                toastr.error(request.responseText);
            }



        });

        $(buttonId).button('reset');
        event.preventDefault();
        return false;
    });


}


function showHideCreatorButtons() {
    $("#saveBtn").removeClass("hide");
    $("#issueBtn").removeClass("hide");
    $("#changeRewardBtn").removeClass("hide");
}

function setupChangeRewardForm(creatorName) {
    var url = creatorName + '/get_rewards_current';
    simpleFetch(url).done(function(result) {

        $('[name="reward_per_piece_per_year"]').attr('placeholder', 'Last was ' + result + ' per piece per year');

    });

}

function setupWithdrawalForm(creatorName) {
    var url = creatorName + '/get_rewards_current';
    // Need totals, get_rewards current, get_pieces_owned_total, get_creators_funds_current'
    $.when(getJson(creatorName + '/get_creators_funds_current'),
        getJson(creatorName + '/get_pieces_owned_total'),
        getJson(creatorName + '/get_rewards_current')).done(function(a1, a2, a3) {
        // the code here will be executed when all four ajax requests resolve.
        // a1, a2, a3 and a4 are lists of length 3 containing the response text,
        // status, and jqXHR object for each of the four ajax calls respectively.

        // var creatorsFundsStr = JSON.parse(a1[0]);
        var creatorsFundsStr = a1[0];
        var piecesOwnedTotalStr = a2[0];
        var rewardsPerPiecePerYearStr = a3[0];

        if (creatorsFundsStr != "0") {
            $('#withdrawBtn').removeClass('hide');
        }


        var creatorsFunds = parseFloat(creatorsFundsStr.replace(/^\D+/g, ''));
        var piecesOwnedTotal = parseFloat(piecesOwnedTotalStr.replace(/^\D+/g, ''));
        var rewardsPerPiecePerYear = parseFloat(rewardsPerPiecePerYearStr.replace(/^\D+/g, ''));

        console.log(piecesOwnedTotalStr);
        console.log(creatorsFunds + '|' + piecesOwnedTotal + '|' + rewardsPerPiecePerYear);
        // $("#creatorsFunds").text(result);
        $('[name="withdrawAmount"]').attr('placeholder', 'Current funds : ' + creatorsFunds);
        $('#funds').text(creatorsFundsStr);
        $("#withdrawSymbol").text(rewardsPerPiecePerYearStr[0]);
        $('[name="withdrawAmount"]').bind('keyup', function(f) {
            var withdrawAmount = parseFloat($(this).val());
            var withdrawAmountAfterFee = withdrawAmount * .95;
            var fundsLeft = creatorsFunds - withdrawAmountAfterFee;
            safetyRatingAfter = fundsLeft / (piecesOwnedTotal * rewardsPerPiecePerYear);

            if (!isNaN(withdrawAmountAfterFee) && fundsLeft > 0) {
                $('#withdrawAmountAfterFee').text('$' + withdrawAmountAfterFee);
                $('#safetyRatingAfter').text(safetyRatingAfter);
                $('#fundsLeft').text('$' + fundsLeft);

                $('#placeWithdrawBtn').prop('disabled', false);
                $('#fundsLeft').addClass("text-success");
                $('#fundsLeft').removeClass("text-danger");
            } else {

                // $('#placeWithdrawBtn').prop('disabled', true);
                $('#fundsLeft').addClass("text-danger");
                $('#fundsLeft').removeClass("text-success");
            }

        });

    });

}


function setupIssueForm(creatorName) {
    // Stuff with the issue modal
    var url = creatorName + '/get_price_per_piece_current';
    simpleFetch(url).done(function(result) {

        $('[name="issuePrice"]').attr('placeholder', 'Last Price was ' + result + '/piece');

    });

    simpleFetch(creatorName + '/get_creators_funds_current').done(function(result) {

        var creatorsFunds = parseFloat(result.replace(/^\D+/g, ''));
        $('[name="creatorsFunds"]').text(result);

        $('[name="issuePieces"],[name="issuePrice"]').bind('keyup', function(f) {

            var pieces = parseFloat($(this).val());

            // var issuePrice = $('[name="buy"]').text();
            // var issuePrice = parseFloat($('[name="buy"]').attr('placeholder').substring(1).split('/')[0]);
            var issuePrice = parseFloat($('[name="issuePrice"]').val());
            // alert(pieces + ' ' + issuePrice)
            var total = issuePrice * pieces;

            if (!isNaN(total)) {
                $('#issueTotal').text('$' + total);
                var fundsLeft = creatorsFunds + total;

                $('#creatorsFundsLeft').text('$' + fundsLeft);

            }
        });



    });
}

function setupModal(shortUrl, formId, buttonId, modalId) {

    $(formId).bootstrapValidator({
        message: 'This value is not valid',
        excluded: [':disabled'],

    });

    // Placing the issue
    $(buttonId).click(function(event) {

        // serializes the form's elements.
        var formData = $(formId).serializeArray();

        console.log(formData);

        // Loading
        $(this).button('loading');

        // username = $('#userLoginDiv').find('input[name="username"]').val();
        // // = $("#inputUsername3").val();
        // document.cookie = "username=" + username ;

        var url = sparkService + shortUrl;
        $.ajax({
            type: "POST",
            url: url,
            xhrFields: {
                withCredentials: true
            },
            data: formData,
            success: function(data, status, xhr) {

                //    console.log(xhr);
                // console.log(asdf);
                // console.log(xhr.getAllResponseHeaders()); 

                // alert(data); // show response from the php script.

                xhr.getResponseHeader('Set-Cookie');

                // old way
                // document.cookie="authenticated_session_id=" + data + 
                // "; expires=" + expireTimeString(60*60); // 1 hour (field is in seconds)
                // Hide the modal, reset the form, show successful
                $(modalId).modal('hide');
                $(formId)[0].reset();

                toastr.success(data);



            },
            error: function(request, status, error) {
                toastr.error(request.responseText);
            }



        });

        $(buttonId).button('reset');
        event.preventDefault();
        return false;
    });



}