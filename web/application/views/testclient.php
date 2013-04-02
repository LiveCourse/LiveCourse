<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8">
		<title>LiveCourse Test Suite</title>
		<style>
			input, textarea
			{
				border:0;
				border-bottom:3px solid #2E587E;
				background-color:#dadee6;
				resize: none; 
			}

			input[type="submit"],button
			{
				background-color:#2E587E;
				border:2px solid #fff;
				color:#fff;
				font-size:18px;
				padding:5px 10px;
				cursor:pointer;
			}

			input[type="submit"]:active,button:active
			{
				background-color:#fff;
				border-color:#2E587E;
				color:#2E587E;
			}
			
			.large_form input::-webkit-input-placeholder {
				color:#B8C9E5;
			}

			.large_form input::-moz-placeholder {
				color:#B8C9E5;
			}

			.large_form input:-ms-input-placeholder {
				color:#B8C9E5;
			}

			.large_form input, .large_form select
			{
				width:256px;
				font-size:24px;
				padding:5px 8px;
				margin:16px 0;
				background-color:#2E587E;
				color:#fff;
			}

			.large_form input:disabled
			{
				color:#B8C9E5;
			}

			.large_form input[type="email"],.large_form input[type="text"],.large_form input[type="password"]
			{
				border-bottom:3px solid #B8C9E5;
			}

			.large_form input[type="email"]:hover,.large_form input[type="text"]:hover,.large_form input[type="password"]:hover,.large_form input[type="email"]:focus,.large_form input[type="text"]:focus,.large_form input[type="password"]:focus
			{
				outline-width: 0;
				border-color:#fff;
			}

			.large_form input[type="submit"],.large_form button
			{
				width:256px;
				font-size:20px;
				margin:6px 0;
			}
			.large_form button
			{
				font-size:16px;
			}

			.large_form input[type="submit"]:active, .large_form button:active
			{
				background-color:#fff;
				border-color:#2E587E;
				color:#2E587E;
			}

			.large_form input.invalid
			{
				border-bottom:3px solid #e54545; /* #e5b8b8 */
			}

			.large_form input[type="password"].half
			{
				width:120px;
			}
			#LeftSideBar
			{
				position:fixed;
				left:0;
				top:0;
				bottom:0;
				width:320px;
				background-color:#2E587E;
				text-align:center;
			}

			#LeftSideBar h1
			{
				text-align:center;
				font-family:'cicle';
				font-size:48px;
				color:#fff;
				margin:0.4em 0;
			}
			#ParamSideBar
			{
				position:fixed;
				text-align:center;
				left:320px;
				top:0;
				bottom:0;
				width:320px;
				background-color:#B8C9E5;
				overflow-y:auto;
			}

			#ParamSideBar h1
			{
				text-align:center;
				font-family:'cicle';
				font-size:48px;
				color:#2E587E;
				margin:0.4em 0;
			}
			#ResultsFrame
			{
				position:fixed;
				left:675px;
				right:0;
				top:0;
				bottom:0px;
				overflow-x:scroll;
			}

			#ResultsFrame h1
			{
				font-family:'cicle';
				font-size:48px;
				color:#2E587E;
				margin:0.4em 0;
			}
			
			/* Syntax highlighting */
			pre {outline: 1px solid #ccc; padding: 5px; margin: 5px; }
			.string { color: green; }
			.number { color: darkorange; }
			.boolean { color: blue; }
			.null { color: magenta; }
			.key { color: red; }
		</style>

		
		<script src="<?php echo(base_url("js/sha1.js")); ?>"></script>
		<script src="<?php echo(base_url("js/json2.js")); ?>"></script>
		<script src="<?php echo(base_url("js/jquery-1.9.1.js")); ?>"></script>
		<script src="<?php echo(base_url("js/jquery-ui-1.10.2.custom.min.js")); ?>"></script>
		<script src="<?php echo(base_url("js/jquery.observe_field.js")); ?>"></script>
		<script src="<?php echo(base_url("js/jquery.cookie.js")); ?>"></script>
		<script src="<?php echo(base_url("js/cufon-yui.js")); ?>"></script>
		<script src="<?php echo(base_url("fonts/font.cicle.js")); ?>"></script>
		
		<script type="text/javascript">
			function syntaxHighlight(json) {
				json = json.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
				return json.replace(/("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)/g, function (match) {
					var cls = 'number';
					if (/^"/.test(match)) {
						if (/:$/.test(match)) {
							cls = 'key';
						} else {
							cls = 'string';
						}
					} else if (/true|false/.test(match)) {
						cls = 'boolean';
					} else if (/null/.test(match)) {
						cls = 'null';
					}
					return '<span class="' + cls + '">' + match + '</span>';
				});
			}
			function addKey()
			{
				$("#keys").append('<input name="key[]" placeholder="Key" style="width:112px;"> <input name="value[]" placeholder="Value" style="width:112px;">');
			}
			function escapeHtml(text) {
				return text
					.replace(/&/g, "&amp;")
					.replace(/</g, "&lt;")
					.replace(/>/g, "&gt;")
					.replace(/"/g, "&quot;")
					.replace(/'/g, "&#039;");
			}
		</script>
		
		<script type="text/javascript">
			var auth_token;
			var auth_pass;
			Cufon.replace('#LeftSideBar h1,#ParamSideBar h1,#ResultsFrame h1');
			
			/**
			 * Calls the specified API function via JQuery ajax
			 * Automatically includes required authentication information.
			 * method - URL of API method to execute
			 * type - Type of API Method (GET,POST,DELETE,etc)
			 * data - Data... yeah.
			 * success_callback - function to be called on successful call.
			 * error_callback - function to be called on error.
			 * returns - nothing... I don't think... yup nothing.
			 */
			function call_api(method,type,data,success_callback,error_callback,plain)
			{
				if (typeof plain == "undefined")
					plain = "json";
				else
					plain = "text";
				var auth_code = Sha1.hash(auth_token+auth_pass+method);
				$.ajax("api/"+method,{
					type: type,
					headers: {
						"Auth": "LiveCourseAuth token="+auth_token+" auth="+auth_code
					},
					data: data,
					dataType: plain,
					success: success_callback,
					error: error_callback
				});
			}
			$(function() {
				$("#Login").submit(function() {
					$(this).find("input").prop('disabled',false); //Re-enable the form
					$(this).find("input").removeClass('invalid'); //Re-enable the form
					//Send API request
					//Fetch authentication token
					var _this = this;
					$.ajax("api/auth",{
						type: "GET",
						data:
						{
							'email': $(_this).find("input[name=email]").val()
						}, 
						success: function(data) {
							auth_token = data.authentication.token;
							auth_pass = Sha1.hash($(_this).find("input[name=password]").val());
							//Now let's try authentication...
							call_api("auth/verify","GET",{},
								function(data)
								{
									//We have successfully authenticated!
									$(_this).find("input[type=submit]").prop('value','Authenticated!');
									$(_this).find("input").prop('disabled',true); //Disable the form
								},
								function(xhr,status)
								{
									//Our API code didn't validate with the server. Oops.
									$(_this).find("input[name=password]").addClass("invalid"); //Failure at this point means our password is probably invalid.
									$(_this).find("input").prop('disabled',false); //Re-enable the form
								});
						},
						error: function(xhr, status) {
							//Couldn't generate an API token for this e-mail.
							$(_this).find("input[name=email]").addClass("invalid"); //Failure at this point means our e-mail is probably invalid.
							$(_this).find("input").prop('disabled',false); //Re-enable the form
						}
					});
					return false;
				});
				$("#REST").submit(function () {
					var func = $(this).find("input[name=call]").val();
					var method = $(this).find("select[name=method]").val();
					var values = {};
					var i = 0;
					var _this = this;
					$(this).find("input[name='key\\[\\]']").each(function() {
						var val = $(_this).find("input[name='key\\[\\]']").eq(i).val();
						if (val.length > 0)
						{
							values[$(_this).find("input[name='key\\[\\]']").eq(i).val()] = $(_this).find("input[name='value\\[\\]']").eq(i).val();
						}
						i++;
					});
					call_api(func,method,values,
						function(data)
						{
							var content = syntaxHighlight(JSON.stringify(JSON.parse(data),undefined,4));
							$("#ResultsFrame").html("<h1>Successful Response:</h1><br><pre>"+content+"</pre>");
							Cufon.refresh();
						},
						function(xhr,status)
						{
							$("#ResultsFrame").html("<h1>Error "+xhr.status+"</h1><br>" + escapeHtml(xhr.responseText));
							Cufon.refresh();
						},"text");
					return false;
				});
				$('#addKeyButton').click(addKey);
			});
		</script>
		
	</head>
	<body>
		<div id="LeftSideBar">
			<h1>TEST MODE</h1>
			<form id="Login" class="large_form">
				<input name="email" type="email" placeholder="E-Mail Address" class="large_form">
				<input name="password" type="password" placeholder="Password" class="large_form">
				<input type="submit" value="Authenticate">
			</form>
		</div>
		<div id="ParamSideBar">
			<h1>Parameters</h1>
			<form id="REST" class="large_form">
				<input name="call" placeholder="REST Function" class="large_form">
				<select name="method">
					<option value="GET">GET</option>
					<option value="POST">POST</option>
					<option value="PUT">PUT</option>
					<option value="DELETE">DELETE</option>
				</select>
				<div id="keys">
					<input name="key[]" placeholder="Key" style="width:112px;"> <input name="value[]" placeholder="Value" style="width:112px;"><br>
					<input name="key[]" placeholder="Key" style="width:112px;"> <input name="value[]" placeholder="Value" style="width:112px;"><br>
					<input name="key[]" placeholder="Key" style="width:112px;"> <input name="value[]" placeholder="Value" style="width:112px;">
				</div>
				<a id="addKeyButton" href="javascript:;">Add Key</a>
				<br>
				<input type="submit" value="Submit">
			</form>
		</div>
		<div id="ResultsFrame">
			<h1>Results</h1>
		</div>
	</body>
</html>
