$(document).ready(function(){

// !!!!!!They must have names unfortunately
$( "#saveBtn" ).click(function( event ) {

  // serializes the form's elements.
  var formData = $("#customizeForm").serializeArray();
  console.log(formData);

  var btn = $(this);
  // Loading
  btn.button('loading');

    var url = "http://localhost:4567/savecreatorpage"; // the script where you handle the form input.

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
                btn.button('reset');
                
                toastr.success('Page saved')
                


                console.log(document.cookie);

              },
              error: function (request, status, error) {

                toastr.error(request.responseText);
              }
            });



    event.preventDefault();
  });


});