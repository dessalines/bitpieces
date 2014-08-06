$(document).ready(function() {
    setupMiniSubmenu();




    showRecaptcha("recaptcha_div");

    setupSaveCategories();

    console.log(document.cookie);


    $('#creatorRegisterForm').bootstrapValidator({
        message: 'This value is not valid',
        excluded: [':disabled'],
        submitButtons: 'button[type="submit"]'

    }).on('success.form.bv', function(event) {
        event.preventDefault();
        standardFormPost('registercreator', "#creatorRegisterForm");

        $('#categories,#registerCreator').collapse();

        // data-target="#registerCreator,#categories" 
    });


    $("#raiseFundsForm").bootstrapValidator({
        message: 'This value is not valid',
        excluded: [':disabled'],
        submitButtons: 'button[type="submit"]'

    }).on('success.form.bv', function(e) {
        e.preventDefault();
        console.log('test');
        raiseFundsPost('raise_funds', "#raiseFundsForm");
    });

    getJson('/get_users_settings').done(function(e) {
        var currIso = e[0]['curr_iso'];
        $('[name="issuePieces"],[name="issuePrice"]').bind('keyup', function(f) {

            var pieces = parseFloat($('[name="issuePieces"]').val());

            // var issuePrice = $('[name="buy"]').text();
            // var issuePrice = parseFloat($('[name="buy"]').attr('placeholder').substring(1).split('/')[0]);
            var issuePrice = parseFloat($('[name="issuePrice"]').val());
            // alert(pieces + ' ' + issuePrice)
            var total = issuePrice * pieces;

            if (!isNaN(total)) {
                $('#issueTotal').text(total + ' ' + currIso);


            }
        });
    });



});



function setupSaveCategories() {

    fillJSONFieldFromMustache('get_categories', '#categories_template', '#categoriesDiv', false)
        .done(function(result) {
            var last_valid_selection = null;
            $('#categorySelect').change(function(event) {
                if ($(this).val().length > 3) {
                    $(this).val(last_valid_selection);
                } else {
                    last_valid_selection = $(this).val();
                }
            });

            $("#saveCategoriesBtn").click(function(event) {
                var sessionId = getCookie("authenticated_session_id");
                standardFormPost("/save_creators_categories", "#categoriesForm");
                console.log('getting the users settings');
                // $('.collapse').collapse();
                getJson('/get_users_settings').done(function(e) {
                    console.log(sessionId);
                    full();
                });
            });



        });



}

function raiseFundsPost(shortUrl, formId) {
    // !!!!!!They must have names unfortunately

    var creatorName = getCookie("username");
    // serializes the form's elements.
    var formData = $(formId).serializeArray();
    console.log(formData);

    // Loading
    // $(this).button('loading');

    var url = sparkService + shortUrl; // the script where you handle the form input.
    console.log(url);
    var post = $.ajax({
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

            $(formId)[0].reset();

            toastr.success(data);

            setTimeout(
                function() {
                    var url = "creator_main?creator=" + creatorName;
                    window.location.replace(url);

                }, 2000);


            console.log(document.cookie);
            return data;

        },
        error: function(request, status, error) {
            toastr.error(request.responseText);
        }
    });

    event.preventDefault();
    return false;



    // event.preventDefault();


}