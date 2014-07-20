$(document).ready(function() {

    sessionId = getCookie("authenticated_session_id");
    setupMiniSubmenu();
    setupCreatorSearch();

    fillUserInfoMustacheFromCookie();
    setupLogout();


    // var creatorName = window.location.pathname.split('/').pop();
    var creatorName = getParameterByName('creator');

    showHideButtons(creatorName);

    bidAskOrBuySetup(sessionId + "/placeask", creatorName, '#askForm', "#placeaskBtn", "#askModal", "Placed Ask");
    bidAskOrBuySetup(sessionId + "/placebid", creatorName, '#bidForm', "#placebidBtn", "#bidModal", "Placed Bid");
    bidAskOrBuySetup(sessionId + "/placebuy", creatorName, '#buyForm', "#placebuyBtn", "#buyModal", "Placed Buy");

    setupDepositButton(sessionId + "/make_deposit_fake", '#placedepositBtn', '#depositForm', '#depositModal');
    $('#page_title').text(creatorName);
    navigateWithParams();


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

function setupDepositButton(shortUrl, btnId, formId, modalId) {
    simpleFetch(sessionId + '/get_users_funds_current').done(function(result) {
        $('[name="deposit"]').attr('placeholder', 'Current funds:' + result);
    });

    // !!!!!!They must have names unfortunately
    $(btnId).click(function(event) {
        var formData = $(formId).serializeArray();

        var btnId = $(this);
        // Loading
        btnId.button('loading');

        var url = sparkService + shortUrl; // the script where you handle the form input.

        $.ajax({
            type: "POST",
            url: url,
            xhrFields: {
                withCredentials: true
            },
            data: formData,
            success: function(data, status, xhr) {


                xhr.getResponseHeader('Set-Cookie');
                // document.cookie="authenticated_session_id=" + data + 
                // "; expires=" + expireTimeString(60*60); // 1 hour (field is in seconds)
                // Hide the modal, reset the form, show successful

                // Loading
                btnId.button('reset');

                toastr.success(data);
                $(modalId).modal('hide');
                $(formId)[0].reset();


                console.log(document.cookie);

            },
            error: function(request, status, error) {

                toastr.error(request.responseText);
            }
        });



        event.preventDefault();
    });
}

function showHideButtons(creatorName) {
    // Only show these things if its a user type
    if (getCookie('usertype') == 'User') {
        // Showing or hiding the bid/ask/buy buttons
        simpleFetch(creatorName + "/get_pieces_available").done(function(result) {
            console.log('result = ' + result);
            if (result > 0) {
                $("#buyBtn").removeClass("hide");
                $('[name="buyPieces"]').attr('placeholder', 'There are ' + result + ' pieces left');
                simpleFetch(creatorName + "/get_pieces_issued_most_recent_price").done(function(result) {
                    // $('[name="buy"]').attr('placeholder','$' + result + '/piece');
                    $('[name="buy"]').val(result);
                });

            }
        });
        simpleFetch(creatorName + "/get_pieces_owned_total").done(function(result) {
            console.log('result = ' + result);
            if (result > 0) {
                $('[name="buyPieces"]').attr('placeholder', 'There are ' + result + ' pieces left');
            }
        });

        simpleFetch(sessionId + "/" + creatorName + "/get_pieces_owned_current").done(function(result) {
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
                $('[name="bid"]').attr('placeholder', 'Last Price was $' + result + '/piece');
                $('[name="ask"]').attr('placeholder', 'Last Price was $' + result + '/piece');
            });

        }
        // This part adds the totals and such
        var types = ['bid', 'ask', 'buy'];
        types.forEach(function(e) {



            simpleFetch(sessionId + '/get_users_funds_current').done(function(result) {
                var usersFunds = parseFloat(result);
                $('[name="usersFunds"]').text('$' + result);

                $('[name="' + e + 'Pieces' + '"]').bind('keyup', function(f) {

                    var buyPieces = parseFloat($(this).val());

                    // var buyPrice = $('[name="buy"]').text();
                    // var buyPrice = parseFloat($('[name="buy"]').attr('placeholder').substring(1).split('/')[0]);
                    var buyPrice = parseFloat($('[name="' + e + '"]').val());
                    // alert(buyPieces + ' ' + buyPrice)
                    var total = buyPrice * buyPieces;

                    if (!isNaN(total)) {
                        $('#' + e + 'Total').text('$' + total);
                        var fundsLeft;
                        if (e == 'ask') {
                            fundsLeft = usersFunds + total;
                        } else {
                            fundsLeft = usersFunds - total;
                        }
                        $('#' + e + 'FundsLeft').text('$' + fundsLeft);

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


function bidAskOrBuySetup(shortUrl, creatorName, formId, buttonId, modalId, message) {
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

                toastr.success(message);



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