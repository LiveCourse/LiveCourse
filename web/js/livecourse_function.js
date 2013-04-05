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
					current_user_id = data.authentication.user_id;
					switch_ui_color(data.user.color_preference,false);
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
	setInterval(function() {update_participant_list();},10000);
	setInterval(function() {update_focus();},10000); //Check if we're focused and update the server.
}

function update_focus()
{
	if ($('body').hasClass("visible"))
	{
		//Update focus
		call_api("users/focus","POST",{},
			function(data)
			{
			
			},
			function(xhr,status)
			{
			
			});
	}
}

/**
 * Switches the UI to the color theme defined by 'code'
 * code - index of ui_colors array of color to set.
 */
function switch_ui_color(code,save)
{
	if (typeof save == "undefined")
		save = false;
	if (code > 0)
		$("#ui_stylesheet").attr('href','css/html5client_'+ui_colors[code]+'.css');
	else
		$("#ui_stylesheet").attr('href','css/html5client.css');
	setTimeout(Cufon.refresh,500);
	
	if (save)
	{
		call_api("users/update_color","POST",{color: code},
			function(data)
			{
			},
			function(xhr,status)
			{
			});
	}
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
 * Shows a dialog to the user to be used setting preferences
 */
function prefs_show()
{
	var dialog = dialog_clone("Preferences","#dialog_prefs",true,true);
	// dialog_addbutton(dialog,"Submit",login_submit,false); //This button'd look better under the actual form.
	dialog.find("#color_selection li").click(function() {
		dialog.find("#color_selection li").removeClass("selected");
		$(this).addClass("selected");
		switch_ui_color($(this).attr("value"),true);
	});
	dialog_show(dialog, function() { //Show it!
		
	}); 
}

/**
 * Shows a dialog with another user's profile information.
 */
function user_profile_show(user_id)
{
	//Run API query...
	call_api("users","GET",{id: user_id},
		function (data) {
			var user_data = data[0];
			call_api("chats","GET",{user_id: user_id},
				function (class_data) {
					var dialog = dialog_clone(user_data.display_name,"#dialog_profile",true,true);
					// dialog_addbutton(dialog,"Submit",login_submit,false); //This button'd look better under the actual form.
					for (i in class_data)
					{
						dialog.find("#class_list ul").append('<li>'+class_data[i].name+'</li>');
					}
					if ($.inArray(user_data.id,ignored_users) < 0)
					{ //If this user is not ignored...
						var ignorebutton = $('<button class="DialogButton">Ignore</button>');
						ignorebutton.click(function() {
							call_api("users/ignore_user","POST",{ignore_id: user_data.id},
								function (data) {
									ignorebutton.prop('disabled',true); //disable the form
									ignorebutton.html("Ignored");
									ignored_users.push(user_data.id);
								},
								function (xhr, status) {
									var errdialog = dialog_new("Error Ignoring User","An error occurred while attempting to ignore this user.",true,true);
									errdialog.find(".DialogContainer").addClass("error");
									dialog_show(errdialog);
								});
						});
						if (user_data.id != current_user_id) //Shouldn't be able to ignore yourself.
						{
							dialog.find(".DialogContainer .buttons").prepend(ignorebutton);
						}
					} else
					{
						var unignorebutton = $('<button class="DialogButton">Unignore</button>');
						unignorebutton.click(function() {
							call_api("users/unignore_user","POST",{unignore_id: user_data.id},
								function (data) {
									unignorebutton.prop('disabled',true); //disable the form
									unignorebutton.html("Unignored");
									ignored_users.splice( $.inArray(user_data.id, ignored_users), 1 );
								},
								function (xhr, status) {
									var errdialog = dialog_new("Error Unignoring User","An error occurred while attempting to unignore this user.",true,true);
									errdialog.find(".DialogContainer").addClass("error");
									dialog_show(errdialog);
								});
						});
						dialog.find(".DialogContainer .buttons").prepend(unignorebutton);
					}
					dialog_show(dialog);
				},
				function (xhr, status)
				{
					var errdialog = dialog_new("Error Fetching Classes","An error occurred while attempting to fetch this user's classes.",true,true);
					errdialog.find(".DialogContainer").addClass("error");
					dialog_show(errdialog);
				});
		},
		function (xhr, status)
		{
			var errdialog = dialog_new("Error Fetching User","An error occurred while attempting to fetch this user's information.",true,true);
			errdialog.find(".DialogContainer").addClass("error");
			dialog_show(errdialog);
		});
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
			if (data.length > 0)
			{
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
 * Update participant list for current room
 */
function update_participant_list()
{
	//Do nothing if we have no selected room.
	if (current_chat_room.length <= 0)
		return;
	call_api("chats/get_participants","GET",{id: current_chat_room},
		function (data) {
			var uids = new Array();
			ignored_users = new Array(); //reinitialize ignored users.
			// Add new participants
			var epoch = ((new Date).getTime())/1000;
			for (i in data)
			{
				//Add ignored users to the ignored users array.
				if (data[i].ignored == 1)
				{
					ignored_users.push(data[i].id);
				}
					
				uids.push(data[i].id);
				if ($("#UserList #"+data[i].id).length <= 0)
				{
					if (data[i].id == current_user_id)
						var newu = $('<li id="'+data[i].id+'" class="me">'+data[i].display_name+'</li>');
					else
						var newu = $('<li id="'+data[i].id+'">'+data[i].display_name+'</li>');
					newu.hide();
					$("#UserList").append(newu);
					newu.slideDown();
				}
				
				//Update online status.
				$("#UserList #"+data[i].id).removeClass("online").removeClass("idle");
				if (data[i].time_lastfocus > epoch - 30) //Focused closer than 30 secs ago? Online.
				{
					$("#UserList #"+data[i].id).addClass("online");
				} else if (data[i].time_lastrequest > epoch - 60) //Client connected closer than 60 secs ago? Idle.
				{
					$("#UserList #"+data[i].id).addClass("idle");
				}
			}
			// Remove old participants.
			$("#UserList li").each(function() {
				if ($.inArray($(this).attr('id'),uids) < 0)
				{
					$(this).slideUp(function() {
						$(this).remove();
					});
				}
			});
			
			//Sort it.
			//We come first!
			var li = $("#UserList .me");
			li.detach();
			$("#UserList").prepend(li);
			
			//Link it!
			$("#UserList li").unbind('click');
			$("#UserList li").click(function() {
				user_profile_show($(this).attr('id'));
			});
		},
		function (xhr, status)
		{
			//silently fail
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
				$("#ChatFrame #ChatFrameHeader #ChatHeaderMenu").fadeIn();
			});
			//Clear notifications...
			clear_notifications();
			//Clear out history...
			$("#HistoryMessages ul").html(''); // Empty it
			//Load recent chat...
			update_participant_list();
			
			setTimeout(function() {load_recent_chat_contents();},250);
			//Load participants...
			
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
 * Switches to chat tab via chat menu.
 */
function select_chat_tab()
{
	// Set buttons to proper selection
	$("#ChatFrame #ChatFrameHeader #ChatHeaderMenu li").removeClass("selected");
	$("#chat_button").addClass("selected");
	Cufon.refresh();
	// Fade out other panes if they're here.
	$("#ChatFrame #HistoryDateSelect").fadeOut(300);
	$("#ChatFrame #HistoryMessages").fadeOut(300,function() {
		$("#ChatFrame #ChatMessages").fadeIn(300,function() {
			$("#ChatMessages").mCustomScrollbar("update");
			$("#ChatMessages").mCustomScrollbar("scrollTo","bottom",{scrollInertia:0}); //scroll to bottom
		});
	});
}

/**
 * Switches to history tab via chat menu.
 */
function select_history_tab()
{
	// Set buttons to proper selection
	$("#ChatFrame #ChatFrameHeader #ChatHeaderMenu li").removeClass("selected");
	$("#history_button").addClass("selected");
	Cufon.refresh();
	// Fade out other panes if they're here.
	$("#ChatFrame #ChatMessages").fadeOut(300,function() {
		$("#ChatFrame #HistoryMessages").fadeIn(300, function() {
			$("#ChatFrame #HistoryDateSelect").css('opacity',0);
			$("#ChatFrame #HistoryDateSelect").css('top','100px');
			$("#ChatFrame #HistoryDateSelect").css('display','block');
			$("#ChatFrame #HistoryDateSelect").animate({"top":'124px',"opacity":1},300, "easeOutBack", function() {
				$( "#ChatFrame #HistoryDateSelect input" ).datepicker("show");
				$( "#ChatFrame #HistoryDateSelect input" ).datepicker("widget").position({
					my: "center top",
					at: "center bottom",
					of: $( "#ChatFrame #HistoryDateSelect a" )
				});
			});
			$("#ChatFrame #HistoryDateSelect").html('<input type="text" style="display:none;" /><a href="javascript:;">Select a Date</a>');
			$( "#ChatFrame #HistoryDateSelect input" ).datepicker({ onSelect: function(dp) {
				$( "#ChatFrame #HistoryDateSelect a" ).html($(this).val());
				var start_epoch = ($(this).datepicker('getDate')).getTime() / 1000;
				load_history(start_epoch);
			}});
			$( "#ChatFrame #HistoryDateSelect a" ).click(function() {
				var dp = $( "#ChatFrame #HistoryDateSelect input" );
				if (dp.datepicker('widget').is(':hidden')) {
					dp.datepicker("show");
					dp.datepicker("widget").position({
						my: "center top",
						at: "center bottom",
						of: this
					});
				} else {
					dp.hide();
				}
			});
		});
	});
}

/**
 * Loads a day's worth of history from the specified day forward into the history pane.
 */
function load_history(start_epoch)
{
	var load_ind = progress_indicator_show();
	call_api("chats/fetch_day","GET",{chat_id: current_chat_room, start_epoch: start_epoch},
		function (data) {
			$("#HistoryMessages ul").html(''); // Empty it
			for (i in data)
			{
				post_message(data[i],false,"#HistoryMessages");
			}
			$("#HistoryMessages").mCustomScrollbar("update");
			$("#HistoryMessages").mCustomScrollbar("scrollTo","top",{scrollInertia:1000}); //scroll to top
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
	if (typeof _clear_me != "undefined")
	{
		_clear_me.prop('disabled',true); //disable the form
	}
	call_api("chats/send","POST",{chat_id: current_chat_room, message: message_string},
		function (data) {
			if (typeof _clear_me != "undefined")
			{
				_clear_me.val("");
				_clear_me.prop('disabled',false); //enable the form
			}
			progress_indicator_hide(send_ind);
		},
		function (xhr, status)
		{
			var errdialog = dialog_new("Error Sending Message","An error occurred while attempting to send your message.",true,true);
			errdialog.find(".DialogContainer").addClass("error");
			dialog_show(errdialog);
			if (typeof _clear_me != "undefined")
			{
				_clear_me.prop('disabled',false); //enable the form
			}
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
			$("#ChatMessages").mCustomScrollbar("update");
			if (data.length > 0)
			{
				for (i in data)
				{
					post_message(data[i],false);
				}
			}
			$("#ChatMessages").mCustomScrollbar("scrollTo","bottom",{scrollInertia:1000}); //scroll to bottom
			//Add eventsource for updating with new messages...
			if (typeof eventsource != "undefined") //Close existing one
				eventsource.close();
			var event_auth_code = Sha1.hash(auth_token+auth_pass+"chats/eventsource");
			eventsource = new EventSource('index.php/api/chats/eventsource?auth_token='+auth_token+'&auth_code='+event_auth_code+'&chat_id='+current_chat_room);
			eventsource.addEventListener('message', function (e) {
				var data = JSON.parse(e.data);
				post_message(data);
				if ($('body').hasClass("hidden"))
				{
					waiting_notifications++;
					setNotifications(waiting_notifications);
				}
			});
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
function post_message(message,scroll,area)
{
	if (typeof area == "undefined")
		area = "#ChatMessages";
	if (typeof scroll == "undefined")
		scroll = true;
		
	//Ignore messages from ignored users.
	if ($.inArray(message.user_id,ignored_users) >= 0)
	{
		return;
	}
	
	//Parse the time stamp.
	var date = new Date(message.send_time*1000);
	var currentDate = new Date();
	//Was this message today?
	var timestamp = "";
	if (date.toDateString() != currentDate.toDateString())
		var timestamp = (('0'+(date.getMonth()+1)).slice(-2))+"/"+(('0'+(date.getDate()+1)).slice(-2))+"/"+date.getFullYear()+" @ ";
	timestamp += (('0'+date.getHours()).slice(-2))+":"+(('0'+date.getMinutes()).slice(-2))+":"+(('0'+date.getSeconds()).slice(-2));

	$(area+" ul").append('<li><div class="author">'+message.display_name+'</div><div class="timestamp">'+timestamp+'</div><div class="messageContainer"><div class="message">'+escapeHtml(message.message_string).parseURL()+'</div></div><div style="clear:both;"></div></li>');
	last_message_id = message.id;
	$.cookie("lc_last_msg", last_message_id); //Set last message id
	$(area).mCustomScrollbar("update");
	if (scroll)
		$(area).mCustomScrollbar("scrollTo","bottom",{scrollInertia:1000}); //scroll to bottom
}
