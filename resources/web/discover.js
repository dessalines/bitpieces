$(document).ready(function(){

	fillFieldFromMustache('/get_categories', '#categories_template' , '#categories', false);

	setupDiscoverSearch('discover', '#discover_search', '#discover_search_btn', 
		'#discover_creators', '#discover_creators_template', '#discover_creators_table')
	

});

function setupDiscoverSearch(shortUrl, formId, btnId, divId, templateId, tableId) {
	var template = $(templateId).html();

	$(btnId).click(function() {

		var formData = $(formId).serializeArray();
        // Loading
        var btnId = $(this);
        btnId.button('loading');
        console.log(formData);
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
                // toastr.success(data);

                var jsonObj = JSON.parse(data);


            // Mustache.parse(template);   // optional, speeds up future uses
            var rendered = Mustache.render(template,jsonObj);
            $(divId).html(rendered);
            $(tableId).tablesorter({debug: true}); 
            console.log(jsonObj);              
            console.log(template);
            console.log(rendered);

            console.log(document.cookie);

        },
        error: function(request, status, error) {

        	toastr.error(request.responseText);
        }
    });



event.preventDefault();
});

}