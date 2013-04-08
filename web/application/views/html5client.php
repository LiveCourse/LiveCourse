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
			Cufon.replace('#TopBar div.classTitle, #LeftSideBar h1,#RightSideBar h1,#ChatFrame h1,.DialogContainer h1,.DialogMessage .status_message,#joinroom_results ul li .name');
			Cufon.replace('#TopBar div.userInfo, #ChatFrame #ChatFrameHeader #ChatHeaderMenu li a, a.buttonLink',{
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
							user_data = data.user;
							switch_ui_color(data.user.color_preference,false);
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
				$('body').addClass("visible");
				
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
				
				$("#TopBar div.classTitle").click(function() {
					toggle_course_selector();
				});
			});
			
			jQuery(document).ready(function ()
			{
				$(window)
					.blur(function() {$('body').addClass("hidden").removeClass("visible");})
					.focus(function() {$('body').addClass("visible").removeClass("hidden");window_onfocus();});
			});
		</script>
		
	</head>
	<body>
		<div id="TopBar">
			<img src="img/logo_48.png" alt="LiveCourse" class="logo">
			<div class="classTitle"><span>Class List</span><img src="img/arrow_dropdown.png" alt="Selector"></div>
			<ul class="options">
				<li><a href="javascript:;" onclick="prefs_show();"><img src="img/icon_gear_20.png" alt="Preferences"></a></li>
				<li><a href="javascript:;" onclick="info_show();"><img src="img/icon_info_20.png" alt="Information"></a></li>
			</ul>
			<div class="userInfo"><img src="img/user_icon_sm.png" alt=""></div>
			
		</div>
		
		<div id="ChatFrame">
			<!--
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
			-->
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
		
		<div id="SideBar">
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
		
		<div id="CourseSelector">
			<ul id="CourseList">
				<!--
				<li class="selected">
					<span class="title">Software Engineering I</span>
					<br />
					<span class="subTitle">20 members, 10 online</span>
				</li>
				-->
			</ul>
			<a class="buttonLink" href="javascript:;" onclick="joinroom_show()">Add a Class</a><br>
		</div>
		
		<!-- Log-in dialog -->
		<div id="dialog_login" style="display:none;">
			<img class="logo_large" src="img/logo_large.png" alt="LiveCourse">
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
		
		<!-- Information dialog -->
		<div id="dialog_info" style="display:none;">
			<div class="pane">
				<h2>Dev Team</h2>
				LiveCourse brought to you by...
				<ul>
					<li>Darren Cheng</li>
					<li>Lee Engelman</li>
					<li>Brandon Klen</li>
					<li>Hayden McAfee</li>
					<li>Jeremy Meyer</li>
					<li>Lars Sorenson</li>
				</ul>
			</div>
			<div class="pane">
				<h2>Artwork</h2>
				HTML5 interface design and implementation by Hayden McAfee<br><br>
				<a href="http://thenounproject.com/noun/gear/#icon-No2789" target="_blank">Gear</a> designed by <a href="http://thenounproject.com/somerandomdude" target="_blank">P.J. Onori</a> from The Noun Project<br>
				<a href="http://thenounproject.com/noun/user/#icon-No2281" target="_blank">User</a> designed by <a href="http://thenounproject.com/elordin" target="_blank">Thomas Weber</a> from The Noun Project
			</div>
		</div>
		
		<!-- User profile dialog -->
		<div id="dialog_profile" style="display:none;">
			<div id="user_icon">
				<img src="img/user_icon.png" alt="User Photo" />
			</div>
			<div id="class_list">
				<h2>Classes</h2>
				<ul>
				</ul>
			</div>
		</div>
	</body>
</html>
