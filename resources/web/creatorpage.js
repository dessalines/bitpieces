$(document).ready(function(){

    var creatorName = window.location.pathname.split('/').pop();
    setupMiniSubmenu();

    fillFieldFromMustache(creatorName + '/get_pieces_owned_value_current_by_creator', 
        '#worth_current_template' , '#worth_current', true);
    fillFieldFromMustache(creatorName + '/get_price_per_piece_current', 
        '#price_per_piece_current_template' , '#price_per_piece_current', true);
    fillFieldFromMustache(creatorName + '/get_rewards_owed', 
        '#rewards_paid_template' , '#rewards_paid', true);
    fillFieldFromMustache(creatorName + '/get_backers_current_count', 
        '#backers_current_count_template' , '#backers_current_count', false);
    
    console.log(document.cookie);

    $('#bidForm').bootstrapValidator({
      message: 'This value is not valid',
      excluded: [':disabled'],

  });

	// The date picker
	$('.datepicker').pickadate({
		format:'yyyy-mm-dd'
	});

	
	console.log(creatorName);

 // Placing the bid
 $( "#placebidBtn" ).click(function( event ) {

	// serializes the form's elements.
	var formData = $("#bidForm").serializeArray();

	// Set the creator id
	var creator = {name:"creatorName", value:creatorName};
	formData.push(creator);

	console.log(formData);

	// Loading
	$(this).button('loading');

	var url = "http://localhost:4567/placebid";


    // username = $('#userLoginDiv').find('input[name="username"]').val();
    // // = $("#inputUsername3").val();
    // document.cookie = "username=" + username ;


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
                $("#bidModal").modal('hide');
                $('#bidForm')[0].reset();
                
                toastr.success('Placed bid');
                


            },
            error: function (request, status, error) {
            	toastr.error(request.responseText);
            }



        });

    $("#placebidBtn").button('reset');
    event.preventDefault();
    return false;
});

// Placing the Ask
$( "#placeaskBtn" ).click(function( event ) {

	// serializes the form's elements.
	var formData = $("#askForm").serializeArray();

	// Set the creator id
	var creator = {name:"creatorName", value:creatorName};
	formData.push(creator);

	console.log(formData);

	// Loading
	$(this).button('loading');


	var url = "http://localhost:4567/placeask";


    // username = $('#userLoginDiv').find('input[name="username"]').val();
    // // = $("#inputUsername3").val();
    // document.cookie = "username=" + username ;


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
                $("#askModal").modal('hide');
                $('#askForm')[0].reset();
                
                toastr.success('Placed ask');
                


            },
            error: function (request, status, error) {
            	toastr.error(request.responseText);
            }



        });

    $("#placeaskBtn").button('reset');
    event.preventDefault();
    return false;
});

});