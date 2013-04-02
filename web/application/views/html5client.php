<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8">
		<title>LiveCourse</title>
		<link id="ui_stylesheet" rel="stylesheet" href="<?php echo(base_url("css/html5client.css")); ?>" />
		<link rel="stylesheet" href="<?php echo(base_url("css/livecourse-theme/jquery-ui-1.10.2.custom.css")); ?>" />
		<link rel="stylesheet" href="<?php echo(base_url("css/jquery.mCustomScrollbar.css")); ?>" />
		<script src="<?php echo(base_url("js/sha1.js")); ?>"></script>
		<script src="<?php echo(base_url("js/json2.js")); ?>"></script>
		<script src="<?php echo(base_url("js/eventsource.js")); ?>"></script>
		<script src="<?php echo(base_url("js/jquery-1.9.1.js")); ?>"></script>
		<script src="<?php echo(base_url("js/jquery-ui-1.10.2.custom.min.js")); ?>"></script>
		<script src="<?php echo(base_url("js/jquery.observe_field.js")); ?>"></script>
		<script src="<?php echo(base_url("js/jquery.cookie.js")); ?>"></script>
		<script src="<?php echo(base_url("js/jquery.mCustomScrollbar.js")); ?>"></script>
		<script src="<?php echo(base_url("js/cufon-yui.js")); ?>"></script>
		<script src="<?php echo(base_url("fonts/font.cicle.js")); ?>"></script>
		
		<script src="<?php echo(base_url("js/livecourse_common.js")); ?>"></script>
		<script src="<?php echo(base_url("js/livecourse_function.js")); ?>"></script>
		
		<script type="text/javascript">
			Cufon.replace('#LeftSideBar h1,#RightSideBar h1,#ChatFrame h1,.DialogContainer h1,.DialogMessage .status_message,#joinroom_results ul li .name');
			Cufon.replace('#ChatFrame #ChatFrameHeader #ChatHeaderMenu li a',{
				hover: true
			});
			$(function() {
				//Check authentication and prompt for log-in if we're not authenticated.
				if (auth_token != "")
				{
					var indicator = progress_indicator_show(indicator);
					call_api("auth/verify","GET",{},
						function(data)
						{
							//We have successfully authenticated!
							$.cookie("lc_auth_token", auth_token); //Set authentication cookies
							$.cookie("lc_auth_pass", auth_pass); //Set authentication cookies
							current_user_id = data.authentication.user_id;
							init_ui();
							progress_indicator_hide(indicator);
						},
						function(xhr,status)
						{
							//Our API code didn't validate with the server. Oops.
							login_show();
							progress_indicator_hide(indicator); //Hide progress indicator.
						});
				} else {
					login_show();
				}
				

				var hidden = "hidden";

				// Standards:
				if (hidden in document)
					document.addEventListener("visibilitychange", onchange);
				else if ((hidden = "mozHidden") in document)
					document.addEventListener("mozvisibilitychange", onchange);
				else if ((hidden = "webkitHidden") in document)
					document.addEventListener("webkitvisibilitychange", onchange);
				else if ((hidden = "msHidden") in document)
					document.addEventListener("msvisibilitychange", onchange);

				// IE 9 and lower:
				else if ('onfocusin' in document)
					document.onfocusin = document.onfocusout = onchange;

				// All others:
				else
					window.onfocus = window.onblur = onchange;
	
				function onchange (evt) {
					var body = document.body;
					evt = evt || window.event;

					if (evt.type == "focus" || evt.type == "focusin")
					{
						body.className = "visible";
						window_onfocus();
					}
					else if (evt.type == "blur" || evt.type == "focusout")
						body.className = "hidden";
					else
					{
						if (this[hidden])
							body.className = "hidden";
						else
						{
							body.className = "visible";
							window_onfocus();
						}
						//body.className = this[hidden] ? "hidden" : "visible";
					}
				}
				
				// Code to submit message on enter.
				$('#form_message textarea').keydown(function(event) {
					if (event.keyCode == 13) {
						send_message($(this).val(),$(this));
						return false;
					}
				});
				$("#form_message").submit(function() {
					send_message($(this).find("textarea").val(),$(this).find("textarea"));
					return false;
				});
				$("#ChatMessages").mCustomScrollbar({scrollInertia:0});
				$("#HistoryMessages").mCustomScrollbar({scrollInertia:0});
			});
		</script>
		
	</head>
	<body>
		<div id="LeftSideBar">
			<h1>LiveCourse</h1>
			<ul id="CourseList">
				<!--
				<li class="selected">
					<span class="title">Software Engineering I</span>
					<br />
					<span class="subTitle">20 members, 10 online</span>
				</li>
				-->
			</ul>
			<button onclick="joinroom_show()">Add a Class</button><br>
			<button onclick="prefs_show()">Preferences</button>
		</div>
		
		<div id="ChatFrame">
			<div id="ChatFrameHeader">
				<h1></h1>
				<ul id="ChatHeaderMenu">
					<li id="chat_button" class="selected"><a href="javascript:;" onclick="select_chat_tab();">chat</a></li>
					<li id="documents_button"><a href="javascript:;">documents</a></li>
					<li id="history_button"><a href="javascript:;" onclick="select_history_tab();">history</a></li>
					<li id="options_button"><a href="javascript:;">options</a></li>
				</ul>
				<div style="clear:both;"></div>
			</div>
			<div id="HistoryDateSelect">
				This is a test date.
			</div>
			<div id="ChatMessages" class="nano">
				<ul>
				</ul>
			</div>
			<div id="HistoryMessages" class="nano">
				<ul>
				</ul>
			</div>
		</div>
		
		<div id="ComposeFrame">
			<form id="form_message">
				<textarea name="message" placeholder="Type your message here."></textarea>
			</form>
		</div>
		
		<div id="RightSideBar">
			<h1>Participants</h1>
			<ul id="UserList">
				<!--
				<li class="me">
					Me
				</li>
				-->
				<!--
					<li>
						This is a user
					</li>
				-->
			</ul>
		</div>
		
		<!-- Hidden Dialogs beyond this point... -->
		
		<!-- Log-in dialog -->
		<div id="dialog_login" style="display:none;">
			<form id="form_login" class="large_form">
				<input name="email" type="email" placeholder="E-Mail Address" /><br>
				<input name="password" type="password" placeholder="Password" /><br><br>
				<input type="submit" value="Log In" /><br>
				<button type="button" class="register">Register</button>
			</form>
		</div>
		
		<!-- Registration dialog -->
		<div id="dialog_registration" style="display:none;">
			<div class="status_message"></div>
			<form id="form_registration" class="large_form">
				<input name="email" type="email" placeholder="E-Mail Address" /><br>
				<input name="name" type="text" placeholder="Your Name" /><br>
				<input name="password" type="password" placeholder="Password" class="half" />
				<input name="password_confirmation" type="password" placeholder="Password" class="half" /><br>
				<input type="submit" value="Register" /><br>
			</form>
		</div>
		
		<!-- Add a class dialog -->
		<div id="dialog_joinroom" style="display:none;">
			<form id="form_joinroom" class="large_form">
				<input name="classnumber" type="text" placeholder="Course Number" />
			</form>
			<div id="joinroom_results">
			</div>
		</div>
		
		<!-- User preferences dialog -->
		<div id="dialog_prefs" style="display:none;">
			<ul id="color_selection">
				<li value="0" style="background-color:#2E587E;"></li>
				<li value="1" style="background-color:#7e2e2e;"></li>
				<li value="2" style="background-color:#7e4f2e;"></li>
				<li value="3" style="background-color:#2e7e3b;"></li>
				<li value="4" style="background-color:#2e7e7e;"></li>
				<li value="5" style="background-color:#7e2e7e;"></li>
			</ul>
		</div>
	</body>
</html>
