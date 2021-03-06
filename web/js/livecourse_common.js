/**
 * Common javascript functions to be used in the LiveCourse HTML5 UI.
 */

var auth_token = ""; // Global variable for storing the authentication code for the current user.
if (typeof($.cookie("lc_auth_token")) != "undefined") //Fetch the value from cookie if it exists
	auth_token = $.cookie("lc_auth_token");
var auth_pass = ""; // Global variable for storing the user's SHA1 encrypted password.
if (typeof($.cookie("lc_auth_pass")) != "undefined") //Fetch the value from cookie if it exists
	auth_pass = $.cookie("lc_auth_pass");
var current_user_id = -1;
var user_data;

var current_chat_room = ""; //Global variable for storing the current chat room.
var last_message_id;
var waiting_notifications = 0;

var last_sender = -1;
var last_sender_history = -1;

var ui_colors=["","red","brown","green","cyan","purple"];

var snd = new Audio("snd/update.wav"); // buffers automatically when created

var ignored_users = new Array();

/**
 * Shows or generates a horizontal pop-up dialog above the current page.
 * header - Title text shown at the top of the dialog
 * message - Content of the dialog
 * showCloseButton (optional) - true/false to show a button allowing the dialog to be closed
 * doNotOpen (optional) - true/false to specify whether to prevent opening the dialog after generation
 *
 * return - JQuery object containing the dialog
 */
function dialog_new(header,message,showCloseButton,doNotOpen)
{
	var dialog = $('<div class="DialogOverlay"><div class="DialogContainer"><h1 style="visibility:hidden;">'+header+'</h1><div class="DialogMessage"></div><div class="buttons"></div></div></div>');
	dialog.find(".DialogMessage").append(message);
	if (showCloseButton == true || typeof showCloseButton == "undefined")
	{
		dialog.find(".DialogContainer .buttons").append('<button class="DialogButton">Close</button>');
		dialog.find(".DialogButton").first().click(function() {
			dialog_close(dialog);
		});
	}
	dialog.appendTo("body");
	if (typeof doNotOpen == "undefined" || doNotOpen == false)
	{
		dialog_show(dialog);
	}
	Cufon.refresh();
	return dialog;
}


/**
 * Shows or generates a horizontal pop-up dialog above the current page with the content of the specified selector
 * header - Title text shown at the top of the dialog
 * targetSelector - Selector pointing to a specific element defined on the current page to show in the dialog
 * showCloseButton (optional) - true/false to show a button allowing the dialog to be closed
 * doNotOpen (optional) - true/false to specify whether to prevent opening the dialog after generation
 *
 * return - JQuery object containing the dialog
 */
function dialog_clone(header,targetSelector,showCloseButton,doNotOpen)
{
	var dialog = dialog_new(header,'<div style="text-align:center;">Loading . . .</div>',showCloseButton,true);
	dialog.find(".DialogMessage").html($(targetSelector).html());
	dialog.find(".DialogMessage").attr('id',$(targetSelector).attr('id'));
	Cufon.replace('#dialog_profile #class_list h2');
	if (typeof doNotOpen == "undefined" || doNotOpen == false)
	{
		dialog_show(dialog);
	}
	return dialog;
}


/**
 * Shows a dialog that is hidden after being generated by dialog_new
 * dialog - dialog object as returned by dialog_new
 * callback - function you want run after the dialog is done showing
 */
function dialog_show(dialog,callback)
{
	dialog.fadeIn(200,function() {
		// WOOOO HEADER ANIMATION!
		dialog.find("h1").css("opacity","0.0");
		dialog.find("h1").css("visibility","visible");
		dialog.find("h1").css("padding-left","1000px");
		dialog.find("h1").animate({"padding-left":0,"opacity":1.0},350, "easeOutQuart", null);
		dialog.find(".DialogContainer").fadeIn(250,function() {
			dialog.find(".DialogButton").first().focus();
			if (callback && typeof(callback) === "function") {
				callback();
			}
		});
	});
}

/**
 * Closes (and DESTROYS) a dialog previously created by dialog_new
 * dialog - dialog object as returned by dialog_new
 */
function dialog_close(dialog)
{
	// WOOO HEADER ANIMATION!
	dialog.find("h1").animate({"padding-left":'1000px',"opacity":0.0},200, "easeInQuint", null);
	dialog.fadeOut(300,function() {
		dialog.remove();
	});
}

/**
 * Adds a button to the button section of the passed dialog
 * dialog - dialog object as returned by dialog_new
 * buttonTitle - Button text of the button to be added
 * buttonCallback - Function to run when new button is selected
 * prepend - true/false, determines whether to place the button before or after existing buttons
 */
function dialog_addbutton(dialog,buttonTitle,buttonCallback,prepend)
{
	var button = $('<button class="DialogButton">'+buttonTitle+'</button>');
	if (prepend == true)
		dialog.find(".DialogContainer .buttons").prepend(button);
	else
		dialog.find(".DialogContainer .buttons").append(button);
	button.click(buttonCallback);
}


/**
 * Shows the global progress indicator
 * Returns an indicator object to hide when you're done with whatever you're doing.
 */
function progress_indicator_show()
{
	var indicator = $('<div id="GlobalLoadingIndicator" class="showing" src="" alt="Loading ..."></div>');
	$('body').append(indicator);
	setTimeout(function () {
		indicator.animate({"top":'-6px'},300, "easeOutBack", null);
	}, 1000);
	return indicator;
}

/**
 * Removes the passed indicator
 * indicator - progress indicator to remove
 */
function progress_indicator_hide(indicator)
{
	indicator.animate({"top":'-20px'},300, "easeInBack", function() {
		$(this).remove();
	});
}

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
function call_api(method,type,data,success_callback,error_callback)
{
	var auth_code = Sha1.hash(auth_token+auth_pass+method);
	$.ajax("index.php/api/"+method,{
		type: type,
		headers: {
			"Auth": "LiveCourseAuth token="+auth_token+" auth="+auth_code
		},
		data: data,
		success: success_callback,
		error: error_callback
	});
}

/**
 * Escapes provided text for display via HTML.
 */
function escapeHtml(text) {
	return text
		.replace(/&/g, "&amp;")
		.replace(/</g, "&lt;")
		.replace(/>/g, "&gt;")
		.replace(/"/g, "&quot;")
		.replace(/'/g, "&#039;");
}

/**
 * String prototype for parsing URLs out of messages and converting them to <a href ...
 */
String.prototype.parseURL = function() {
	return this.replace(/(([a-z]+:\/\/)?(([a-z0-9\-]+\.)+([a-z]{2}|aero|arpa|biz|com|coop|edu|gov|info|int|jobs|mil|museum|name|nato|net|org|pro|travel|local|internal))(:[0-9]{1,5})?(\/[a-z0-9_\-\.~]+)*(\/([a-z0-9_\-\.]*)(\?[a-z0-9+_\-\.%=&amp;]*)?)?(#[a-zA-Z0-9!$&'()*+.=-_~:@/?]*)?)(\s+|$)/gi, function(url) {
		//return url.link(url);
		return "<a target='_blank' href='"+url+"'>"+url+"</a>";
	});
};

/**
 * Sets the tab title to the specified count (when greater than zero)
 */
function setNotifications(notification_count)
{
	if (notification_count <= 0)
	{
		setTimeout(function() {document.title = "LiveCourse";},200);
	} else {
		if (notification_count == 1)
		{
			snd.currentTime=0;
			snd.play();
		}
		setTimeout(function() {document.title = "("+notification_count+") LiveCourse";},200);
	}
}

/**
 * Clears all tab title notifications.
 */
function clear_notifications()
{
	//Clear notifications
	waiting_notifications = 0;
	setNotifications(waiting_notifications);
}

/**
 * This should run when the browser window / tab is focused.
 */
function window_onfocus()
{
	clear_notifications();
	$("#ChatMessages").mCustomScrollbar("scrollTo","bottom",{scrollInertia:300}); //scroll to bottom
	//Update focus
	call_api("users/focus","POST",{},
		function(data)
		{
		
		},
		function(xhr,status)
		{
		
		});
}
