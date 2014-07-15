$(document).ready(function() {

  sessionId = getCookie("authenticated_session_id");
  console.log(sessionId);

  // fillUserInfoMustache('get_user_data');

  setupMiniSubmenu();

  fillUserInfoMustacheFromCookie();

  setupCreatorSearch();

  setupLogout();


});



