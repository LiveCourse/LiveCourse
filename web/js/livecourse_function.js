/**
 * One-use specific javascript functions to be used in the LiveCourse HTML5 UI.
 */

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
	
}

/**
 * Submits currently shown log in form.
 */
function login_submit()
{
	console.log("Log in!");
	return false;
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
	
	
	//Just for kicks
	setTimeout(function() {this.progress_indicator_hide(indicator);},2000);
	
	return false;
}

/**
 * Shows a dialog to the user to be used for logging in.
 */
function joinroom_show()
{
	var dialog = dialog_clone("Add a Class","#dialog_joinroom",true,true);
	// dialog_addbutton(dialog,"Submit",login_submit,false); //This button'd look better under the actual form.
	dialog_show(dialog);
}
