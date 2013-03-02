/**
 * One-use specific javascript functions to be used in the LiveCourse HTML5 UI.
 */

/**
 * Shows a dialog to the user to be used for logging in.
 */
function login_show()
{
	var dialog = dialog_clone("Log In","#dialog_login",true,true);
	// dialog_addbutton(dialog,"Submit",login_submit,false); //This button'd look better under the actual form.
	dialog_show(dialog);
}

/**
 * Submits currently shown log in form.
 */
function login_submit()
{
	alert("Log in!");
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
