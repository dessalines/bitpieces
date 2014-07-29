$(document).ready(function(){

  // fillFieldFromMustache('getcreatorpage', '#main_body_template', '#main_body', false);

  sessionId = getCookie("authenticated_session_id");

  setupSummerNote(sessionId + '/getcreatorpage', '#main_body', 'main_body');

  saveSummerNote(sessionId + '/savecreatorpage', '#saveBtn', '#main_body');

});
