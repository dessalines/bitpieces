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

    fillUserHighChartStandardTemplate(creatorName + '/get_pricing', '#pricing', 'Price ($/piece)', '$');


    var template = $('#creators_activity_template').html();
    pageNumbers['#creators_activity_table'] = 1;
    setupPagedTable(creatorName + '/get_creators_activity/', template, '#creators_activity', '#creators_activity_table');


    // if you're this creator, then set up summer note, issue pieces button
    var userName = getCookie('username');
    console.log('un = ' + userName);
    if (userName == creatorName) {
        // show the save btn
        setupModal("/issue_pieces", '#issueForm', "#placeIssuePiecesBtn", "#issueModal");
        setupIssueForm(creatorName);
        setupSummerNote('/getcreatorpage', '#main_body', 'main_body');

        saveSummerNote('/savecreatorpage', '#saveBtn', '#main_body');

        setupChangeRewardForm(creatorName);
        setupModal("/new_reward", '#rewardForm', "#placeChangeRewardBtn", "#rewardModal");

        showHideCreatorButtons();



    }



});

function setupPagedTable(shortUrl, templateHtml, divId, tableId) {
    var pageNum = pageNumbers[tableId];

    var nextId = divId + "_pager_next";
    var prevId = divId + "_pager_prev";
    console.log(nextId);
    fillTableFromMustache(shortUrl + pageNum,
        templateHtml, divId, tableId);

    $(nextId).click(function(e) {
        pageNum++;
        $(prevId).removeClass('disabled');

        fillTableFromMustache(shortUrl + pageNum,
            templateHtml, divId, tableId);

    });
    $(prevId).click(function(e) {
        if (pageNum > 1) {
            pageNum--;
        }
        if (pageNum == 1) {
            $(this).addClass('disabled');
        }

        fillTableFromMustache(shortUrl + pageNum,
            templateHtml, divId, tableId);

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


function setupIssueForm(creatorName) {
    // Stuff with the issue modal
    var url = creatorName + '/get_price_per_piece_current';
    simpleFetch(url).done(function(result) {

        $('[name="issuePrice"]').attr('placeholder', 'Last Price was ' + result + '/piece');

    });

    simpleFetch('/get_creators_funds_current').done(function(result) {
        var fundsNum = result.replace(/^\D+/g, '')
        var creatorsFunds = parseFloat(fundsNum);
        $('[name="creatorsFunds"]').text(result);

        $('[name="issuePieces"]').bind('keyup', function(f) {

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