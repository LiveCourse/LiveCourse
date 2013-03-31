/**
 * One-use specific javascript functions to be used in the LiveCourse HTML5 UI.
 */

var eventsource;

/**
 * Shows a dialog to the user to be used for logging in.
 * allow_close - whether or not we should allow the log-in dialog to be closed.
 */
function login_show(allow_close)
{
	if (typeof showCloseButton == "undefined")
		allow_close = false;
	var dialog = dialog_clone("Log In","#dialog_login",allow_close,true);
	// dialog_addbutton(dialog,"Submit",login_submit,false); //This button'd look better under the actual form.
	dialog.find("form").submit(login_submit); //Bind form submission to submit function
	dialog.find("button.register").click(function() { //Bind registration button to show registration dialog
		registration_show(true);
		dialog_close(dialog);
	});
	dialog_show(dialog, function() { //Show it!
		dialog.find("form").find("input").first().focus(); //Focus the username box after the dialog loads
	}); 
	name
}

/**
 * Submits currently shown log in form.
 */
function login_submit()
{
	$(this).find("input").prop('disabled',true); //disable the form
	
	//Validate input
	var valid = true;
	var email_regex = /\S+@\S+\.\S+/;
	
	//Validate e-mail
	if (!email_regex.test($(this).find("input[name=email]").val()))
	{
		$(this).find("input[name=email]").addClass("invalid");
		valid = false;
	}
	//Validate password
	if ($(this).find("input[name=password]").val().length <= 0)
	{
		$(this).find("input[name=password]").addClass("invalid");
		valid = false;
	}
	
	if (!valid) { //Stop here if we have any invalid fields
		$(this).find("input").prop('disabled',false); //Re-enable the form
		return false; 
	}
	
	//Information is valid, let's send it to the server for additional validation
	var indicator = progress_indicator_show(); //Show a progress indicator for AJAX requests...
	
	//First, begin authentication challenge process
	//Grab form values...
	var email = $(this).find("input[name=email]").val();
	
	//Send API request
	//Fetch authentication token
	var _this = this;
	$.ajax("index.php/api/auth",{
		type: "GET",
		data:
		{
			'email': email
		}, 
		success: function(data) {
			auth_token = data.authentication.token;
			auth_pass = Sha1.hash($(_this).find("input[name=password]").val());
			//Now let's try authentication...
			call_api("auth/verify","GET",{},
				function(data)
				{
					//We have successfully authenticated!
					$.cookie("lc_auth_token", auth_token); //Set authentication cookies
					$.cookie("lc_auth_pass", auth_pass); //Set authentication cookies
					init_ui();
					progress_indicator_hide(indicator);
					dialog_close($(_this).parents(".DialogOverlay").first());
				},
				function(xhr,status)
				{
					//Our API code didn't validate with the server. Oops.
					$(_this).find("input[name=password]").addClass("invalid"); //Failure at this point means our password is probably invalid.
					$(_this).find("input").prop('disabled',false); //Re-enable the form
					progress_indicator_hide(indicator); //Hide progress indicator.
				});
		},
		error: function(xhr, status) {
			//Couldn't generate an API token for this e-mail.
			progress_indicator_hide(indicator); //Hide progress indicator.
			$(_this).find("input[name=email]").addClass("invalid"); //Failure at this point means our e-mail is probably invalid.
			$(_this).find("input").prop('disabled',false); //Re-enable the form
		}
	});
	return false;
}

/**
 * Initializes LiveCourse UI - supposed to be called after SUCCESSFULLY authenticating.
 */
function init_ui()
{
	update_chat_list();
}

/**
 * Shows a dialog to the user to be used for registration.
 * require_login - If true, when registration window is closed, log-in dialog will be shown.
 */
function registration_show(require_login)
{
	var dialog = dialog_clone("Registration","#dialog_registration",false,true);
	dialog.find("form").submit(registration_submit); //Bind form submission to submit function
	if (require_login) //If we are required to sign in, when the close button is pressed we should return to the log in dialog.
	{
		dialog_addbutton(dialog,"Close",function() {
			login_show();
			dialog_close(dialog);
		},false);
	} else {
		dialog_addbutton(dialog,"Close",function() {
			dialog_close(dialog);
		},false);
	}
	
	dialog_show(dialog, function() { //Show it!
		dialog.find("form").find("input").first().focus(); //Focus the e-mail input after the dialog loads
	});
}

/**
 * Validates registration and submits for processing if passed
 */
function registration_submit()
{
	//Hide messages...
	$(this).parent().find(".status_message").slideUp();
	
	//Set all fields as valid
	$(this).find("input").removeClass("invalid");
	
	//Disable the form
	$(this).find("input").prop('disabled',true);
	
	//Validate input
	var valid = true;
	var email_regex = /\S+@\S+\.\S+/;
	
	//Validate e-mail
	if (!email_regex.test($(this).find("input[name=email]").val()))
	{
		$(this).find("input[name=email]").addClass("invalid");
		valid = false;
	}
	
	//Validate name
	if ($(this).find("input[name=name]").val().length <= 0)
	{
		$(this).find("input[name=name]").addClass("invalid");
		valid = false;
	}
	
	//Validate password
	if ($(this).find("input[name=password]").val().length <= 0 || $(this).find("input[name=password]").val() != $(this).find("input[name=password_confirmation]").val())
	{
		$(this).find("input[name=password]").addClass("invalid");
		$(this).find("input[name=password_confirmation]").addClass("invalid");
		valid = false;
	}
	
	if (!valid) { //Stop here if we have any invalid fields
		$(this).find("input").prop('disabled',false); //Re-enable the form
		return false; 
	}
	
	//Information is valid, let's send it to the server for additional validation
	var indicator = progress_indicator_show(); //Show a progress indicator for AJAX requests...
	
	//TODO: Ajax Request for user registration
	var email = $(this).find("input[name=email]").val();
	var encpass = Sha1.hash($(this).find("input[name=password]").val());
	var display_name = $(this).find("input[name=name]").val();
	
	var _this = this;
	
	//Send API request
	$.ajax("index.php/api/users/add",{
		type: "POST",
		data:
		{
			'email': email,
			'password': encpass,
			'display_name': display_name
		}, 
		success: function(data) {
			$(_this).slideUp();
			$(_this).parent().find(".status_message").html("<br><br>Account created successfully.<br><br>");
			var login_button = $('<button type="button" style="width:160px;font-size:24px;">Log In</button>').click(function() {
				login_show();
				dialog_close($(this).parents(".DialogOverlay").first());
			});
			$(_this).parent().find(".status_message").append(login_button);
			$(_this).parent().find(".status_message").slideDown();
			Cufon.refresh();
			progress_indicator_hide(indicator);
		},
		error: function(xhr, status) {
			var err = JSON.parse(xhr.responseText);
			$(_this).parent().find(".status_message").html("An error was encountered while processing your registration.");
			if (err.errors.length > 0)
			{
				for (var e in (err.errors))
				{
					$(_this).parent().find(".status_message").append("<br>"+err.errors[e]);
				}
			}
			Cufon.refresh();
			$(_this).parent().find(".status_message").slideDown();
			progress_indicator_hide(indicator);
			$(_this).find("input").prop('disabled',false); //Re-enable the form
		}
	});
	
	return false;
}

/**
 * Shows a dialog to the user to be used for logging in.
 */
function joinroom_show()
{
	var dialog = dialog_clone("Add a Class","#dialog_joinroom",true,true);
	$(dialog).find("input[name=classnumber]").observe_field(0.5, function( ) {
		if ($(dialog).find("input[name=classnumber]").val().length <= 0)
		{
			$(dialog).find("#joinroom_results").html("Please enter a course number or course name.");
		} else {
			var ind = progress_indicator_show();
			$(dialog).find("#joinroom_results").html();
			call_api("chats/search","GET",{query: $(dialog).find("input[name=classnumber]").val()},
				function (data) {
					$(dialog).find("#joinroom_results").html('<ul></ul>');
					for (var i in data)
					{
						var start_hour = Math.floor(data[i].start_time/60);
						var start_minute = data[i].start_time%60;
						var end_hour = Math.floor(data[i].end_time/60);
						var end_minute = data[i].end_time%60;
						var dow = '';
						if (data[i].dow_monday == 1) dow+='M';
						if (data[i].dow_tuesday == 1) dow+='T';
						if (data[i].dow_wednesday == 1) dow+='W';
						if (data[i].dow_thursday == 1) dow+='R';
						if (data[i].dow_friday == 1) dow+='F';
						if (data[i].dow_saturday == 1) dow+='S';
						if (data[i].dow_sunday == 1) dow+='U';
						var item = $('<li id="'+data[i].id_string+'"><span class="name">'+data[i].name+'</span><br><span class="time">'+dow+' from '+start_hour+':'+start_minute+' - '+end_hour+':'+end_minute+'</span></li>');
						item.hide();
						$(dialog).find("#joinroom_results ul").append(item);
						item.fadeIn();
						item.click(function() {
							var _this = this;
							class_join($(this).attr('id'),function (data) {
									dialog_close($(_this).parents('.DialogOverlay').first());
								}, function(xhr, status)
								{
									var errdialog = dialog_new("Error Joining","An error occurred while attempting to join this class.",true,true);
									errdialog.find(".DialogContainer").addClass("error");
									dialog_show(errdialog);
								});
						});
					}
					Cufon.refresh();
					progress_indicator_hide(ind);
				},
				function (xhr, status)
				{
					if (xhr.status == 404)
						$(dialog).find("#joinroom_results").html("No matching classes could be found.");
					else
						$(dialog).find("#joinroom_results").html("There was an error processing your query.");
					progress_indicator_hide(ind);
				});
		}
	});
	dialog_show(dialog);
}

/**
 * Joins the logged in user to the specified class
 */
function class_join(class_idstring,success_callback,error_callback)
{
	var join_ind = progress_indicator_show();
	call_api("chats/join","POST",{id: class_idstring},
		function (data) {
			if (typeof success_callback != "undefined")
			{
				success_callback(data);
			}
			update_chat_list();
			progress_indicator_hide(join_ind);
		},
		function (xhr, status)
		{
			if (typeof error_callback != "undefined")
			{
				error_callback(xhr,status);
			}
			progress_indicator_hide(join_ind);
		});

}

/**
 * Update chat room list
 */
function update_chat_list()
{
	var upd_ind = progress_indicator_show();
	call_api("chats","GET",{},
		function (data) {
			$('#CourseList li').slideUp(300,function() {$(this).remove();});
			for (i in data)
			{
				var listitem = $('<li id="'+data[i].id_string+'"><span class="title">'+data[i].name+'</span><br /><span class="subTitle">0 members, 0 online</span></li>');
				listitem.hide();
				if (data[i].id_string == current_chat_room)
					listitem.addClass("selected");
				$('#CourseList').append(listitem);
				listitem.slideDown();
				listitem.click(function() {
					switch_chat_room($(this).attr('id'));
				});
			}
			progress_indicator_hide(upd_ind);
		},
		function (xhr, status)
		{
			var errdialog = dialog_new("Error Refreshing Class List","An error occurred while attempting to refresh your class list.",true,true);
			errdialog.find(".DialogContainer").addClass("error");
			dialog_show(errdialog);
			progress_indicator_hide(upd_ind);
		});
}

/**
 * Sets the active chat room - switches context and loads content of new chat.
 * room - string ID of room to switch to.
 */
function switch_chat_room(room)
{
	var switch_ind = progress_indicator_show();
	last_message_id = 0;
	$.cookie("lc_last_msg", last_message_id); //Set last message id
	call_api("chats/info","GET",{id: room},
		function (data) {
			// Set global variable
			current_chat_room = room;
			// Set selected class icon
			$("#CourseList li").removeClass("selected");
			$("#CourseList #"+room).addClass("selected");
			$("#ChatFrame h1").css('overflow','hidden');
			if ($("#ChatFrame h1").height() > 0)
				$("#ChatFrame h1").height($("#ChatFrame h1").height());
			// Header flies away...
			$("#ChatFrame h1").animate({"padding-left":'328px',"opacity":0.0},200, "easeInQuint", function ()
			{
				// Changes name
				$("#ChatFrame h1").html(data.name);
				Cufon.refresh(); //Reload font
				//And flies back.
				$("#ChatFrame h1").animate({"padding-left":0,"opacity":1.0},350, "easeOutQuart", function ()
				{
					$("#ChatFrame h1").css('overflow','');
					$("#ChatFrame h1").height('');
				});
			});
			
			//Load recent chat...
			load_recent_chat_contents();
			
			progress_indicator_hide(switch_ind);
		},
		function (xhr, status)
		{
			var errdialog = dialog_new("Error Switching Class Context","An error occurred while attempting to view a chat.",true,true);
			errdialog.find(".DialogContainer").addClass("error");
			dialog_show(errdialog);
			progress_indicator_hide(switch_ind);
		});
}

/**
 * Sends a message to the current chat room.
 * message - Message string to send.
 * clear_me - JQuery element to clear after the process successfully completes.
 */
function send_message(message_string,clear_me)
{

	if (message_string.length < 1)
	{
		return;
	}
	
	if (current_chat_room.length <= 0)
	{
		var errdialog = dialog_new("Error Sending Message","You have not selected a chat room to participate in.",true,true);
		errdialog.find(".DialogContainer").addClass("error");
		dialog_show(errdialog);
		return;
	}
	
	var send_ind = progress_indicator_show();
	var _clear_me = clear_me;
	call_api("chats/send","POST",{chat_id: current_chat_room, message: message_string},
		function (data) {
			if (typeof _clear_me != "undefined")
			{
				_clear_me.val("");
			}
			progress_indicator_hide(send_ind);
		},
		function (xhr, status)
		{
			var errdialog = dialog_new("Error Sending Message","An error occurred while attempting to send your message.",true,true);
			errdialog.find(".DialogContainer").addClass("error");
			dialog_show(errdialog);
			progress_indicator_hide(send_ind);
		});
}

/**
 * Loads recent chat history for selected chat room.
 */
function load_recent_chat_contents()
{
	var load_ind = progress_indicator_show();
	call_api("chats/fetch_recent","GET",{chat_id: current_chat_room},
		function (data) {
			$("#ChatMessages ul").html('<li class="spacer"></li>');
			for (i in data)
			{
				post_message(data[i],false);
			}
			$("#ChatMessages").mCustomScrollbar("scrollTo","bottom",{scrollInertia:1000}); //scroll to bottom
			//Add eventsource for updating with new messages...
			if (typeof eventsource != "undefined") //Close existing one
				eventsource.close();
			var event_auth_code = Sha1.hash(auth_token+auth_pass+"chats/eventsource");
			eventsource = new EventSource('index.php/api/chats/eventsource?auth_token='+auth_token+'&auth_code='+event_auth_code+'&chat_id='+current_chat_room);
			eventsource.onmessage = function(e) {
				var data = JSON.parse(e.data);
				post_message(data);
			};
			progress_indicator_hide(load_ind);
		},
		function (xhr, status)
		{
			var errdialog = dialog_new("Error Loading Messages","An error occurred while attempting to load your messages.",true,true);
			errdialog.find(".DialogContainer").addClass("error");
			dialog_show(errdialog);
			progress_indicator_hide(load_ind);
		});
}

/**
 * Posts the specified message object in the chat messages frame for user view.
 */
function post_message(message,scroll)
{
	if (typeof scroll == "undefined")
		scroll = true;
		
	//Parse the time stamp.
	var date = new Date(message.send_time*1000);
	var currentDate = new Date();
	//Was this message today?
	var timestamp = "";
	if (date.toDateString() != currentDate.toDateString())
		var timestamp = (('0'+(date.getMonth()+1)).slice(-2))+"/"+(('0'+(date.getDate()+1)).slice(-2))+"/"+date.getFullYear()+" @ ";
	timestamp += (('0'+date.getHours()).slice(-2))+":"+(('0'+date.getMinutes()).slice(-2))+":"+(('0'+date.getSeconds()).slice(-2));

	$("#ChatMessages ul").append('<li><div class="author">'+message.display_name+'</div><div class="timestamp">'+timestamp+'</div><div class="messageContainer"><div class="message">'+escapeHtml(message.message_string)+'</div></div><div style="clear:both;"></div></li>');
	last_message_id = message.id;
	$.cookie("lc_last_msg", last_message_id); //Set last message id
	$("#ChatMessages").mCustomScrollbar("update");
	if (scroll)
		$("#ChatMessages").mCustomScrollbar("scrollTo","bottom",{scrollInertia:1000}); //scroll to bottom
}
