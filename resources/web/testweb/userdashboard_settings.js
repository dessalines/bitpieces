$(document).ready(function() {



    var sessionId = getCookie("authenticated_session_id");


    // fillFieldFromMustacheCustom('/get_users_settings', '#users_settings_template', '#users_settings', false);

    // fillFieldFromMustacheCustom('get_currencies', '#currency_template', '#currency', false);

    if (sessionId != null) {
        full();
    }

    $("#changePasswordForm").bootstrapValidator({
        message: 'This value is not valid',
        excluded: [':disabled'],
        submitButtons: 'button[type="submit"]'
    }).on('success.form.bv', function(event) {
        event.preventDefault();
        standardFormPost('change_password', "#changePasswordForm", "#changePasswordModal");
    });


});

function full() {
    var template = $('#users_settings_template').html();
    var sessionId = getCookie("authenticated_session_id");
    $.when(getJson('/get_users_settings'),
        getJson('get_currencies')).done(function(a1, a2) {
        // the code here will be executed when all four ajax requests resolve.
        // a1, a2, a3 and a4 are lists of length 3 containing the response text,
        // status, and jqXHR object for each of the four ajax calls respectively.

        var usersSettingsJSON = JSON.parse(a1[0]);
        var currenciesJSON = JSON.parse(a2[0]);
        // var jsonObj = $.extend({}, usersSettingsJSON, currenciesJSON);
        jsonObj = usersSettingsJSON;
        jsonObj['currencies'] = currenciesJSON;


        var rendered = Mustache.render(template, jsonObj);
        $('#users_settings').html(rendered);
        console.log(jsonObj);
        console.log(template);
        console.log(rendered);

        // Setting the correct selected based on it
        $("#inputCurrency").val(jsonObj['curr_iso']).prop('selected', true);
        $("#inputPrecision").val(jsonObj['precision_']).prop('selected', true);

        setupSaveSettings('/save_settings', '#settingsForm', '#settingsSaveBtn')

        // now do teh mustache
    });
}


function fillFieldFromMustacheCustom(url, templateId, divId, isMoney) {
    var template = $(templateId).html();

    var url = sparkService + url // the script where you handle the form input.
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
            if (jsonObj > 0 && isMoney) {
                jsonObj = jsonObj.toFixed(2).replace(/\d(?=(\d{3})+\.)/g, '$&,');
            }

            // Mustache.parse(template);   // optional, speeds up future uses
            var rendered = Mustache.render(template, jsonObj);
            $(divId).html(rendered);
            console.log(jsonObj);
            console.log(template);
            console.log(rendered);

            setupSaveSettings('/save_users_settings', '#settingsForm', '#settingsSaveBtn');

        },
        error: function(request, status, error) {

            toastr.error(request.responseText);
        }
    });

}

function setupSaveSettings(shortUrl, formId, buttonId) {
    // Placing the Ask
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

                $('#raiseFunds').collapse();
                $('#units').collapse('hide');
                // $('#raiseFunds').attr("data-toggle", "collapse");
                $('#units').attr("data-target", "#units,#raiseFunds");

                var currIso = formData[0]['value'];
                console.log('curr iso = ' + currIso);
                $('[name="curr_iso"]').text(currIso);
                // $('#raiseFunds').attr("data-target", "");
                // alert(data); // show response from the php script.

                xhr.getResponseHeader('Set-Cookie');

                // old way
                // document.cookie="authenticated_session_id=" + data + 
                // "; expires=" + expireTimeString(60*60); // 1 hour (field is in seconds)
                // Hide the modal, reset the form, show successful

                // $('#units,#raiseFunds').collapse();
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