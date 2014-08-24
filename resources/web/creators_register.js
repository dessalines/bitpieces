$(document).ready(function() {
    setupMiniSubmenu();


    var currIso = null;

    showRecaptcha("recaptcha_div");

    setupSaveCategories();

    console.log(document.cookie);


    $('#creatorRegisterForm').bootstrapValidator({
        message: 'This value is not valid',
        excluded: [':disabled'],
        submitButtons: 'button[type="submit"]'

    }).on('success.form.bv', function(event) {
        event.preventDefault();
        standardFormPost('registercreator', "#creatorRegisterForm", null, false, 
           itWorked);
// $('#categories,#registerCreator').collapse()
     

        // data-target="#registerCreator,#categories" 
    });



    $("#takeMeHomeBtn").click(function(e) {
        getJson('/get_users_settings').done(function(e) {
            var creatorName = getCookie("username");
            var url = "/creators/main/" + creatorName;
            window.location.replace(url);
        });
    });




});
function itWorked() {
    console.log('it worked')
}



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

                    // this is handled in userdashboard_settings.js
                    full();

                });
            });



        });



}