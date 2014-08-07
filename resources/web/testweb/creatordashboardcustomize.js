$(document).ready(function() {

    // fillFieldFromMustache('getcreatorpage', '#main_body_template', '#main_body', false);

    sessionId = getCookie("authenticated_session_id");

    setupSummerNote('/getcreatorpage', '#main_body', 'main_body');

    saveSummerNote('/savecreatorpage', '#saveBtn', '#main_body');

});