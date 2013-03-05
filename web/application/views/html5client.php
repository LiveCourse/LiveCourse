<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8">
		<title>LiveCourse</title>
		<link rel="stylesheet" href="<?php echo(base_url("css/html5client.css")); ?>" />
		<script src="<?php echo(base_url("js/json2.js")); ?>"></script>
		<script src="<?php echo(base_url("js/jquery-1.9.0.js")); ?>"></script>
		<script src="<?php echo(base_url("js/jquery-ui-1.10.0.custom.min.js")); ?>"></script>
		<script src="<?php echo(base_url("js/underscore.js")); ?>"></script>
		<script src="<?php echo(base_url("js/backbone.js")); ?>"></script>
		<script src="<?php echo(base_url("js/cufon-yui.js")); ?>"></script>
		<script src="<?php echo(base_url("fonts/font.cicle.js")); ?>"></script>
		
		<script src="<?php echo(base_url("js/livecourse_common.js")); ?>"></script>
		<script src="<?php echo(base_url("js/livecourse_function.js")); ?>"></script>
		
		<script type="text/javascript">
			Cufon.replace('#LeftSideBar h1,#RightSideBar h1,#ChatFrame h1,.DialogContainer h1');
			$(function() {
				login_show();
			});
		</script>
		
	</head>
	<body>
		<div id="LeftSideBar">
			<h1>LiveCourse</h1>
			<ul id="CourseList">
				<li>
					<span class="title">Software Engineering I</span>
					<br />
					<span class="subTitle">20 members, 10 online</span>
				</li>
				<li class="selected">
					<span class="title">Systems Programming</span>
					<br />
					<span class="subTitle">30 members, 8 online</span>
				</li>
				<li>
					<span class="title">Social Psychology</span>
					<br />
					<span class="subTitle">25 members, 12 online</span>
				</li>
			</ul>
			<button onclick="joinroom_show()">Add a Class</button>
		</div>
		
		<div id="ChatFrame">
			<h1>Systems Programming</h1>
		</div>
		
		<div id="ComposeFrame">
			<textarea name="message" placeholder="Type your message here."></textarea>
		</div>
		
		<div id="RightSideBar">
			<h1>Participants</h1>
			<ul id="UserList">
				<li class="me">
					Bob Test User
				</li>
				<li>
					Darren Cheng
				</li>
				<li>
					Lee Engelman
				</li>
				<li>
					Brandon Klen
				</li>
				<li>
					Hayden McAfee
				</li>
				<li>
					Jeremy Meyer
				</li>
				<li>
					Lars Sorenson
				</li>
			</ul>
		</div>
		
		<!-- Hidden Dialogs beyond this point... -->
		
		<!-- Log-in dialog -->
		<div id="dialog_login" style="display:none;">
			<form id="form_login" class="large_form">
				<input name="email" type="email" placeholder="E-Mail Address" /><br>
				<input name="password" type="password" placeholder="Password" /><br><br>
				<input type="submit" value="Log In" /><br>
				<button class="register">Register</button>
			</form>
		</div>
		
		<!-- Registration dialog -->
		<div id="dialog_registration" style="display:none;">
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
		</div>
	</body>
</html>
