$(document).ready(function(){


	console.log(document.cookie);
	$('#registerForm').bootstrapValidator({
		message: 'This value is not valid',
		excluded: [':disabled'],

	});
	$('#loginForm').bootstrapValidator({
		message: 'This value is not valid',
		excluded: [':disabled'],

	});
	$('#creatorRegisterForm').bootstrapValidator({
		message: 'This value is not valid',
		excluded: [':disabled'],

	});

   // !!!!!!They must have names unfortunately
   $( "#creatorRegisterBtn" ).click(function( event ) {

	// serializes the form's elements.
	var formData = $("#creatorRegisterForm").serializeArray();
	console.log(formData);

	// Loading
	$(this).button('loading');

    var url = "http://localhost:4567/registercreator"; // the script where you handle the form input.

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

                $('#creatorRegisterForm')[0].reset();
                
                toastr.success('Registered and logged in.')
                
                


                console.log(document.cookie);

            },
            error: function (request, status, error) {
                delete_cookie("authenticated_session_id");
                toastr.error(request.responseText);
            }
        });



    event.preventDefault();
});




});