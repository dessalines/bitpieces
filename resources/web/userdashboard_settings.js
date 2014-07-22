$(document).ready(function(){


	
	sessionId = getCookie("authenticated_session_id");

	
	// fillFieldFromMustacheCustom(sessionId + '/get_users_settings', '#users_settings_template', '#users_settings', false);

	// fillFieldFromMustacheCustom('get_currencies', '#currency_template', '#currency', false);

	full();

	
});

function full() {
	var template = $('#users_settings_template').html();

	$.when(getJson(sessionId + '/get_users_settings'), 
		getJson('get_currencies')).done(function(a1, a2){
    // the code here will be executed when all four ajax requests resolve.
    // a1, a2, a3 and a4 are lists of length 3 containing the response text,
    // status, and jqXHR object for each of the four ajax calls respectively.

    var usersSettingsJSON = JSON.parse(a1[0]);
    var currenciesJSON = JSON.parse(a2[0]);
    // var jsonObj = $.extend({}, usersSettingsJSON, currenciesJSON);
    jsonObj = usersSettingsJSON;
    jsonObj['currencies'] = currenciesJSON;
    

    var rendered = Mustache.render(template,jsonObj);
    $('#users_settings').html(rendered);
    console.log(jsonObj);        
    console.log(template);
    console.log(rendered);

    setupSaveSettings(sessionId + '/save_users_settings', '#settingsForm', '#settingsSaveBtn')

     // now do teh mustache
 });
	}


	 function fillFieldFromMustacheCustom(url, templateId, divId, isMoney) {
	 	var template = $(templateId).html();

       var url = sparkService + url// the script where you handle the form input.
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
            var rendered = Mustache.render(template,jsonObj);
            $(divId).html(rendered);
            console.log(jsonObj);              
            console.log(template);
            console.log(rendered);
            
            setupSaveSettings(sessionId + '/save_users_settings', '#settingsForm', '#settingsSaveBtn')

        },
        error: function (request, status, error) {

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

                // alert(data); // show response from the php script.

                xhr.getResponseHeader('Set-Cookie');

                // old way
                // document.cookie="authenticated_session_id=" + data + 
                // "; expires=" + expireTimeString(60*60); // 1 hour (field is in seconds)
                // Hide the modal, reset the form, show successful

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